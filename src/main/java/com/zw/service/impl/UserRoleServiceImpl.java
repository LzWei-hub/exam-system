package com.zw.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zw.dto.UserRoleBatchDTO;
import com.zw.entity.Role;
import com.zw.entity.User;
import com.zw.entity.UserRole;
import com.zw.exception.BusinessException;
import com.zw.mapper.RoleMapper;
import com.zw.mapper.UserMapper;
import com.zw.mapper.UserRoleMapper;
import com.zw.service.UserRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserRoleServiceImpl extends ServiceImpl<UserRoleMapper, UserRole> implements UserRoleService {

    private final RoleMapper roleMapper;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public boolean batchAssignUserRole(UserRoleBatchDTO userRoleBatchDTO) {
        // 检查角色是否存在
        Role role = roleMapper.selectById(userRoleBatchDTO.getRoleId());
        if (role == null) {
            throw new BusinessException("角色不存在");
        }
        
        // 检查用户是否存在
        LambdaQueryWrapper<User> userQueryWrapper = new LambdaQueryWrapper<>();
        userQueryWrapper.in(User::getId, userRoleBatchDTO.getUserIds());
        List<User> users = userMapper.selectList(userQueryWrapper);
        if (users.size() != userRoleBatchDTO.getUserIds().size()) {
            throw new BusinessException("部分用户不存在");
        }
        
        // 删除原有角色关联
        LambdaQueryWrapper<UserRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(UserRole::getUserId, userRoleBatchDTO.getUserIds());
        remove(wrapper);
        
        // 批量添加新角色关联
        List<UserRole> userRoles = userRoleBatchDTO.getUserIds().stream()
                .map(userId -> {
                    UserRole userRole = new UserRole();
                    userRole.setUserId(userId);
                    userRole.setRoleId(userRoleBatchDTO.getRoleId());
                    return userRole;
                }).collect(Collectors.toList());
        
        // 使用MyBatis-Plus的批量保存方法
        for (UserRole userRole : userRoles) {
            baseMapper.insert(userRole);
        }
        
        return true;
    }

    @Override
    public List<Long> getUserRoleIds(Long userId) {
        LambdaQueryWrapper<UserRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserRole::getUserId, userId);
        wrapper.select(UserRole::getRoleId);
        
        List<UserRole> userRoles = list(wrapper);
        return userRoles.stream()
                .map(UserRole::getRoleId)
                .collect(Collectors.toList());
    }
} 