package com.zw.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zw.dto.LogQueryDTO;
import com.zw.entity.AccessLog;
import com.zw.entity.OperationLog;
import com.zw.vo.AccessStatsVO;
import com.zw.vo.SystemHealthVO;

public interface MonitorService {
    /**
     * 查询操作日志
     */
    IPage<OperationLog> getOperationLogs(LogQueryDTO queryDTO);
    
    /**
     * 查询访问日志
     */
    IPage<AccessLog> getAccessLogs(LogQueryDTO queryDTO);
    
    /**
     * 获取访问统计信息
     */
    AccessStatsVO getAccessStats();
    
    /**
     * 获取系统健康信息
     */
    SystemHealthVO getSystemHealth();
} 