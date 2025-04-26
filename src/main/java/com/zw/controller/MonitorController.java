package com.zw.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zw.common.Result;
import com.zw.dto.LogQueryDTO;
import com.zw.entity.AccessLog;
import com.zw.entity.OperationLog;
import com.zw.service.MonitorService;
import com.zw.vo.AccessStatsVO;
import com.zw.vo.SystemHealthVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "系统监控", description = "系统监控和日志相关接口")
@RestController
@RequestMapping("/api/admin/monitor")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class MonitorController {
    
    private final MonitorService monitorService;
    
    @Operation(summary = "查询操作日志", description = "分页查询系统操作日志")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "查询成功"),
        @ApiResponse(responseCode = "403", description = "无权限")
    })
    @GetMapping("/operation-logs")
    public Result<IPage<OperationLog>> getOperationLogs(
            @Parameter(description = "日志查询参数", required = true)
            LogQueryDTO queryDTO) {
        IPage<OperationLog> logs = monitorService.getOperationLogs(queryDTO);
        return Result.success(logs);
    }
    
    @Operation(summary = "查询访问日志", description = "分页查询系统访问日志")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "查询成功"),
        @ApiResponse(responseCode = "403", description = "无权限")
    })
    @GetMapping("/access-logs")
    public Result<IPage<AccessLog>> getAccessLogs(
            @Parameter(description = "日志查询参数", required = true)
            LogQueryDTO queryDTO) {
        IPage<AccessLog> logs = monitorService.getAccessLogs(queryDTO);
        return Result.success(logs);
    }
    
    @Operation(summary = "获取访问统计", description = "获取系统访问统计信息")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "获取成功"),
        @ApiResponse(responseCode = "403", description = "无权限")
    })
    @GetMapping("/access-stats")
    public Result<AccessStatsVO> getAccessStats() {
        AccessStatsVO stats = monitorService.getAccessStats();
        return Result.success(stats);
    }
    
    @Operation(summary = "获取系统健康信息", description = "获取系统运行状态和健康信息")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "获取成功"),
        @ApiResponse(responseCode = "403", description = "无权限")
    })
    @GetMapping("/health")
    public Result<SystemHealthVO> getSystemHealth() {
        SystemHealthVO health = monitorService.getSystemHealth();
        return Result.success(health);
    }
} 