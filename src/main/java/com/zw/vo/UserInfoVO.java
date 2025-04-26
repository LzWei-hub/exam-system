package com.zw.vo;

import lombok.Data;

@Data
public class UserInfoVO {
    private Long id;
    private String username;
    private String realName;
    private String role;
    private String email;
    private String avatar;
    private Integer status;
} 