package com.zw.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("sys_access_log")
public class AccessLog {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String ipAddress;
    private String requestUri;
    private String requestMethod;
    private String userAgent;
    private String username;
    private Integer status;
    private Long responseTime;
    private LocalDateTime accessTime;
} 