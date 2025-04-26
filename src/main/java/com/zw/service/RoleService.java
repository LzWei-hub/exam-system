package com.zw.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zw.dto.RoleDTO;
import com.zw.dto.RolePermissionDTO;
import com.zw.entity.Role;

import java.util.List;
import java.util.Set;

public interface RoleService extends IService<Role> {
    /**
     * 创建角色
     */
    Role createRole(RoleDTO roleDTO);
    
    /**
     * 更新角色信息
     */
    Role updateRole(Long roleId, RoleDTO roleDTO);
    
    /**
     * 分页查询角色列表
     */
    IPage<Role> getRolePage(int pageNum, int pageSize, String name);
    
    /**
     * 查询所有角色列表
     */
    List<Role> getRoleList();
    
    /**
     * 删除角色
     */
    boolean deleteRole(Long roleId);
    
    /**
     * 分配角色权限
     */
    boolean assignRolePermissions(RolePermissionDTO rolePermissionDTO);
    
    /**
     * 获取角色拥有的权限ID列表
     */
    List<Long> getRolePermissionIds(Long roleId);
    
    /**
     * 获取用户的角色列表
     */
    List<Role> getRolesByUserId(Long userId);
    
    /**
     * 获取用户的角色编码集合
     */
    Set<String> getRoleCodesByUserId(Long userId);
    
    /**
     * 为用户分配角色
     */
    void assignRolesToUser(Long userId, List<Long> roleIds);
    
    /**
     * 为角色分配权限
     */
    void assignPermissionsToRole(Long roleId, List<Long> permissionIds);
} 