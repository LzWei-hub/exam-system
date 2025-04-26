package com.zw.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zw.dto.RoleDTO;
import com.zw.dto.RolePermissionDTO;
import com.zw.entity.Role;
import com.zw.entity.RolePermission;
import com.zw.exception.BusinessException;
import com.zw.mapper.RoleMapper;
import com.zw.mapper.RolePermissionMapper;
import com.zw.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {

    private final RolePermissionMapper rolePermissionMapper;

    @Override
    @Transactional
    public Role createRole(RoleDTO roleDTO) {
        // 检查角色编码是否已存在
        LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Role::getCode, roleDTO.getCode());
        if (count(wrapper) > 0) {
            throw new BusinessException("角色编码已存在");
        }

        // 创建角色
        Role role = new Role();
        BeanUtils.copyProperties(roleDTO, role);
        role.setStatus(1); // 默认启用
        role.setSort(0); // 默认排序值
        role.setCreateTime(LocalDateTime.now());
        role.setUpdateTime(LocalDateTime.now());
        
        // 保存角色
        save(role);
        return role;
    }

    @Override
    @Transactional
    public Role updateRole(Long roleId, RoleDTO roleDTO) {
        // 检查角色是否存在
        Role role = getById(roleId);
        if (role == null) {
            throw new BusinessException("角色不存在");
        }
        
        // 检查角色编码是否与其他角色冲突
        if (!role.getCode().equals(roleDTO.getCode())) {
            LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Role::getCode, roleDTO.getCode());
            if (count(wrapper) > 0) {
                throw new BusinessException("角色编码已存在");
            }
        }
        
        // 更新角色信息
        BeanUtils.copyProperties(roleDTO, role);
        role.setUpdateTime(LocalDateTime.now());
        
        // 保存更新
        updateById(role);
        return role;
    }

    @Override
    public IPage<Role> getRolePage(int pageNum, int pageSize, String name) {
        LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(name)) {
            wrapper.like(Role::getName, name);
        }
        
        // 设置排序
        wrapper.orderByDesc(Role::getCreateTime);
        
        // 执行分页查询
        Page<Role> page = new Page<>(pageNum, pageSize);
        return page(page, wrapper);
    }

    @Override
    public List<Role> getRoleList() {
        LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Role::getStatus, 1); // 只查询启用的角色
        wrapper.orderByAsc(Role::getSort);
        return list(wrapper);
    }

    @Override
    @Transactional
    public boolean deleteRole(Long roleId) {
        // 检查角色是否存在
        Role role = getById(roleId);
        if (role == null) {
            throw new BusinessException("角色不存在");
        }
        
        // 删除角色权限关联
        LambdaQueryWrapper<RolePermission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RolePermission::getRoleId, roleId);
        rolePermissionMapper.delete(wrapper);
        
        // 删除角色
        return removeById(roleId);
    }

    @Override
    @Transactional
    public boolean assignRolePermissions(RolePermissionDTO rolePermissionDTO) {
        // 检查角色是否存在
        Role role = getById(rolePermissionDTO.getRoleId());
        if (role == null) {
            throw new BusinessException("角色不存在");
        }
        
        // 删除原有权限
        LambdaQueryWrapper<RolePermission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RolePermission::getRoleId, rolePermissionDTO.getRoleId());
        rolePermissionMapper.delete(wrapper);
        
        // 批量添加新权限
        List<RolePermission> rolePermissions = rolePermissionDTO.getPermissionIds().stream()
                .map(permissionId -> {
                    RolePermission rolePermission = new RolePermission();
                    rolePermission.setRoleId(rolePermissionDTO.getRoleId());
                    rolePermission.setPermissionId(permissionId);
                    return rolePermission;
                }).collect(Collectors.toList());
        
        if (!rolePermissions.isEmpty()) {
            // 使用MyBatis-Plus的批量保存方法
            for (RolePermission rolePermission : rolePermissions) {
                rolePermissionMapper.insert(rolePermission);
            }
        }
        
        return true;
    }

    @Override
    public List<Long> getRolePermissionIds(Long roleId) {
        // 检查角色是否存在
        Role role = getById(roleId);
        if (role == null) {
            throw new BusinessException("角色不存在");
        }
        
        // 查询角色权限ID列表
        LambdaQueryWrapper<RolePermission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RolePermission::getRoleId, roleId);
        wrapper.select(RolePermission::getPermissionId);
        
        List<RolePermission> rolePermissions = rolePermissionMapper.selectList(wrapper);
        return rolePermissions.stream()
                .map(RolePermission::getPermissionId)
                .collect(Collectors.toList());
    }

    @Override
    public List<Role> getRolesByUserId(Long userId) {
        return baseMapper.selectRolesByUserId(userId);
    }

    @Override
    public Set<String> getRoleCodesByUserId(Long userId) {
        List<Role> roles = getRolesByUserId(userId);
        return roles.stream()
                .map(Role::getCode)
                .collect(Collectors.toSet());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignRolesToUser(Long userId, List<Long> roleIds) {
        // 先删除用户原有角色
        baseMapper.deleteUserRoles(userId);
        // 批量插入新的角色关联
        if (roleIds != null && !roleIds.isEmpty()) {
            baseMapper.insertUserRoles(userId, roleIds);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignPermissionsToRole(Long roleId, List<Long> permissionIds) {
        // 先删除角色原有权限
        baseMapper.deleteRolePermissions(roleId);
        // 批量插入新的权限关联
        if (permissionIds != null && !permissionIds.isEmpty()) {
            baseMapper.insertRolePermissions(roleId, permissionIds);
        }
    }
} 