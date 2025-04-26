package com.zw.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UserRoleDTO {
    @NotBlank(message = "角色不能为空")
    @Pattern(regexp = "ADMIN|TEACHER|STUDENT", message = "角色必须是ADMIN、TEACHER或STUDENT")
    private String role;
} 