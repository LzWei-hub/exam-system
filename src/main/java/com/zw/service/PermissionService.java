package com.zw.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zw.entity.Permission;
import com.zw.vo.PermissionTreeVO;

import java.util.List;
import java.util.Set;

public interface PermissionService extends IService<Permission> {
    /**
     * 获取权限树
     */
    List<PermissionTreeVO> getPermissionTree();
    
    /**
     * 获取用户的所有权限（包括角色关联的权限）
     */
    List<Permission> getUserPermissions(Long userId);
    
    /**
     * 获取用户的权限编码集合
     */
    Set<String> getUserPermissionCodes(Long userId);
    
    /**
     * 获取角色的权限列表
     */
    List<Permission> getRolePermissions(Long roleId);
    
    /**
     * 构建权限树
     */
    List<Permission> buildPermissionTree(List<Permission> permissions);
} 