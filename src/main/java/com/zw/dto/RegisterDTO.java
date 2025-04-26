package com.zw.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "注册请求参数")
public class RegisterDTO {
    
    @Schema(description = "用户名", example = "zhangsan")
    @NotBlank(message = "用户名不能为空")
    @Size(min = 4, max = 20, message = "用户名长度应在4-20个字符之间")
    private String username;
    
    @Schema(description = "密码", example = "Abc123456")
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 20, message = "密码长度应在6-20个字符之间")
    private String password;
    
    @Schema(description = "真实姓名", example = "张三")
    @NotBlank(message = "真实姓名不能为空")
    private String realName;
    
    @Schema(description = "角色", example = "STUDENT", allowableValues = {"ADMIN", "TEACHER", "STUDENT"})
    @NotBlank(message = "角色不能为空")
    @Pattern(regexp = "STUDENT|TEACHER", message = "角色只能是 STUDENT 或 TEACHER")
    private String role;
    
    @Schema(description = "邮箱", example = "zhangsan@example.com")
    @Email(message = "邮箱格式不正确")
    private String email;
} 