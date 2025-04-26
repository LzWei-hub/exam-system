package com.zw.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class RolePermissionDTO {
    @NotNull(message = "角色ID不能为空")
    private Long roleId;
    
    @NotEmpty(message = "权限列表不能为空")
    private List<Long> permissionIds;
} 