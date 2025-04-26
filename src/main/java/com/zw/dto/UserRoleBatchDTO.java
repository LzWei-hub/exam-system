package com.zw.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class UserRoleBatchDTO {
    @NotEmpty(message = "用户列表不能为空")
    private List<Long> userIds;
    
    @NotNull(message = "角色ID不能为空")
    private Long roleId;
} 