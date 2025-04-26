package com.zw.vo;

import lombok.Data;

import java.util.Map;

@Data
public class SystemHealthVO {
    private String status;
    private Map<String, Object> components;
    private Map<String, Object> details;
    
    // CPU使用率
    private Double cpuUsage;
    
    // 内存信息
    private Long totalMemory;
    private Long usedMemory;
    private Long freeMemory;
    
    // JVM信息
    private Long jvmTotalMemory;
    private Long jvmUsedMemory;
    private Long jvmFreeMemory;
    
    // 磁盘信息
    private Long totalDiskSpace;
    private Long usedDiskSpace;
    private Long freeDiskSpace;
    
    // 运行时信息
    private String javaVersion;
    private String osName;
    private Long upTime;
} 