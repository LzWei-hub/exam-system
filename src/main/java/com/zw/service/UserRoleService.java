package com.zw.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zw.dto.UserRoleBatchDTO;
import com.zw.entity.UserRole;

import java.util.List;

public interface UserRoleService extends IService<UserRole> {
    /**
     * 批量分配用户角色
     */
    boolean batchAssignUserRole(UserRoleBatchDTO userRoleBatchDTO);
    
    /**
     * 获取用户角色ID列表
     */
    List<Long> getUserRoleIds(Long userId);
} 