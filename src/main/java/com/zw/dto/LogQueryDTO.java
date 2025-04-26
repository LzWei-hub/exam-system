package com.zw.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class LogQueryDTO {
    private String username;
    private String ipAddress;
    private Integer status;
    private String businessType;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer pageNum = 1;
    private Integer pageSize = 10;
} 