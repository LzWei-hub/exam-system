package com.zw.dto;

import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class UserUpdateDTO {
    private String realName;
    
    @Email(message = "邮箱格式不正确")
    private String email;
    
    private String avatar;
} 