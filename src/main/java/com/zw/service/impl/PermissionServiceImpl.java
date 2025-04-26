package com.zw.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zw.entity.Permission;
import com.zw.mapper.PermissionMapper;
import com.zw.service.PermissionService;
import com.zw.vo.PermissionTreeVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PermissionServiceImpl extends ServiceImpl<PermissionMapper, Permission> implements PermissionService {

    @Override
    public List<PermissionTreeVO> getPermissionTree() {
        // 获取所有权限
        List<Permission> permissions = list();
        
        // 转换为VO
        List<PermissionTreeVO> permissionVOList = permissions.stream().map(permission -> {
            PermissionTreeVO vo = new PermissionTreeVO();
            BeanUtils.copyProperties(permission, vo);
            return vo;
        }).collect(Collectors.toList());
        
        // 构建权限树
        return buildPermissionTreeVO(permissionVOList);
    }

    private List<PermissionTreeVO> buildPermissionTreeVO(List<PermissionTreeVO> permissionVOList) {
        // 构建父子关系的树形结构
        Map<Long, List<PermissionTreeVO>> parentMap = permissionVOList.stream()
                .collect(Collectors.groupingBy(PermissionTreeVO::getParentId));
        
        // 设置子节点
        permissionVOList.forEach(permission -> {
            List<PermissionTreeVO> children = parentMap.get(permission.getId());
            if (children != null) {
                permission.setChildren(children);
            }
        });
        
        // 返回顶层节点（parentId = 0的节点）
        return permissionVOList.stream()
                .filter(permission -> permission.getParentId() == 0)
                .collect(Collectors.toList());
    }

    @Override
    public List<Permission> getUserPermissions(Long userId) {
        return baseMapper.selectPermissionsByUserId(userId);
    }

    @Override
    public Set<String> getUserPermissionCodes(Long userId) {
        List<Permission> permissions = getUserPermissions(userId);
        return permissions.stream()
                .map(Permission::getCode)
                .collect(Collectors.toSet());
    }

    @Override
    public List<Permission> getRolePermissions(Long roleId) {
        return baseMapper.selectPermissionsByRoleId(roleId);
    }

    @Override
    public List<Permission> buildPermissionTree(List<Permission> permissions) {
        // 构建父子关系的树形结构
        Map<Long, List<Permission>> parentMap = permissions.stream()
                .collect(Collectors.groupingBy(Permission::getParentId));
        
        // 设置子节点
        permissions.forEach(permission -> {
            List<Permission> children = parentMap.get(permission.getId());
            if (children != null) {
                permission.setChildren(children);
            }
        });
        
        // 返回顶层节点（parentId = 0的节点）
        return permissions.stream()
                .filter(permission -> permission.getParentId() == 0)
                .collect(Collectors.toList());
    }
} 