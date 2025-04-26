package com.zw.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zw.dto.LogQueryDTO;
import com.zw.entity.AccessLog;
import com.zw.entity.OperationLog;
import com.zw.mapper.AccessLogMapper;
import com.zw.mapper.OperationLogMapper;
import com.zw.service.MonitorService;
import com.zw.vo.AccessStatsVO;
import com.zw.vo.SystemHealthVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.RuntimeMXBean;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MonitorServiceImpl implements MonitorService {

    private final OperationLogMapper operationLogMapper;
    private final AccessLogMapper accessLogMapper;

    @Override
    public IPage<OperationLog> getOperationLogs(LogQueryDTO queryDTO) {
        LambdaQueryWrapper<OperationLog> wrapper = new LambdaQueryWrapper<>();
        
        // 添加查询条件
        if (StringUtils.hasText(queryDTO.getUsername())) {
            wrapper.like(OperationLog::getOperName, queryDTO.getUsername());
        }
        
        if (StringUtils.hasText(queryDTO.getIpAddress())) {
            wrapper.like(OperationLog::getOperIp, queryDTO.getIpAddress());
        }
        
        if (queryDTO.getStatus() != null) {
            wrapper.eq(OperationLog::getStatus, queryDTO.getStatus());
        }
        
        if (StringUtils.hasText(queryDTO.getBusinessType())) {
            wrapper.eq(OperationLog::getBusinessType, queryDTO.getBusinessType());
        }
        
        if (queryDTO.getStartTime() != null) {
            wrapper.ge(OperationLog::getOperTime, queryDTO.getStartTime());
        }
        
        if (queryDTO.getEndTime() != null) {
            wrapper.le(OperationLog::getOperTime, queryDTO.getEndTime());
        }
        
        // 设置排序
        wrapper.orderByDesc(OperationLog::getOperTime);
        
        // 执行分页查询
        Page<OperationLog> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        return operationLogMapper.selectPage(page, wrapper);
    }

    @Override
    public IPage<AccessLog> getAccessLogs(LogQueryDTO queryDTO) {
        LambdaQueryWrapper<AccessLog> wrapper = new LambdaQueryWrapper<>();
        
        // 添加查询条件
        if (StringUtils.hasText(queryDTO.getUsername())) {
            wrapper.like(AccessLog::getUsername, queryDTO.getUsername());
        }
        
        if (StringUtils.hasText(queryDTO.getIpAddress())) {
            wrapper.like(AccessLog::getIpAddress, queryDTO.getIpAddress());
        }
        
        if (queryDTO.getStatus() != null) {
            wrapper.eq(AccessLog::getStatus, queryDTO.getStatus());
        }
        
        if (queryDTO.getStartTime() != null) {
            wrapper.ge(AccessLog::getAccessTime, queryDTO.getStartTime());
        }
        
        if (queryDTO.getEndTime() != null) {
            wrapper.le(AccessLog::getAccessTime, queryDTO.getEndTime());
        }
        
        // 设置排序
        wrapper.orderByDesc(AccessLog::getAccessTime);
        
        // 执行分页查询
        Page<AccessLog> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        return accessLogMapper.selectPage(page, wrapper);
    }

    @Override
    public AccessStatsVO getAccessStats() {
        AccessStatsVO statsVO = new AccessStatsVO();
        
        // 总访问量
        statsVO.setTotalVisits(accessLogMapper.selectCount(null));
        
        // 今日访问量
        LambdaQueryWrapper<AccessLog> todayWrapper = new LambdaQueryWrapper<>();
        LocalDateTime today = LocalDate.now().atStartOfDay();
        todayWrapper.ge(AccessLog::getAccessTime, today);
        statsVO.setTodayVisits(accessLogMapper.selectCount(todayWrapper));
        
        // 每小时访问统计
        Map<Integer, Long> hourlyStats = new HashMap<>();
        for (int i = 0; i < 24; i++) {
            hourlyStats.put(i, 0L);
        }
        
        // 最近一周每天访问统计
        Map<String, Long> dailyStats = new HashMap<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate now = LocalDate.now();
        for (int i = 6; i >= 0; i--) {
            LocalDate date = now.minusDays(i);
            dailyStats.put(date.format(formatter), 0L);
        }
        
        // 查询最近一周的访问记录
        LambdaQueryWrapper<AccessLog> weekWrapper = new LambdaQueryWrapper<>();
        weekWrapper.ge(AccessLog::getAccessTime, now.minusDays(6).atStartOfDay());
        List<AccessLog> weekLogs = accessLogMapper.selectList(weekWrapper);
        
        // 计算统计数据
        for (AccessLog log : weekLogs) {
            // 小时统计
            if (log.getAccessTime().toLocalDate().equals(now)) {
                int hour = log.getAccessTime().getHour();
                hourlyStats.put(hour, hourlyStats.get(hour) + 1);
            }
            
            // 日期统计
            String dateStr = log.getAccessTime().toLocalDate().format(formatter);
            if (dailyStats.containsKey(dateStr)) {
                dailyStats.put(dateStr, dailyStats.get(dateStr) + 1);
            }
        }
        
        statsVO.setHourlyStats(hourlyStats);
        statsVO.setDailyStats(dailyStats);
        
        // 访问量最高的10个页面
        LambdaQueryWrapper<AccessLog> topPagesWrapper = new LambdaQueryWrapper<>();
        topPagesWrapper.select(AccessLog::getRequestUri, AccessLog::getId);
        topPagesWrapper.orderByDesc(AccessLog::getId);
        List<AccessLog> allLogs = accessLogMapper.selectList(topPagesWrapper);
        
        Map<String, Long> pageCountMap = allLogs.stream()
            .collect(Collectors.groupingBy(AccessLog::getRequestUri, Collectors.counting()));
        
        List<AccessStatsVO.PageAccessVO> topPages = pageCountMap.entrySet().stream()
            .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
            .limit(10)
            .map(entry -> {
                AccessStatsVO.PageAccessVO pageVO = new AccessStatsVO.PageAccessVO();
                pageVO.setUri(entry.getKey());
                pageVO.setCount(entry.getValue());
                return pageVO;
            })
            .collect(Collectors.toList());
        
        statsVO.setTopPages(topPages);
        
        // 访问量最高的10个IP
        Map<String, Long> ipCountMap = allLogs.stream()
            .collect(Collectors.groupingBy(AccessLog::getIpAddress, Collectors.counting()));
        
        List<AccessStatsVO.IpAccessVO> topIps = ipCountMap.entrySet().stream()
            .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
            .limit(10)
            .map(entry -> {
                AccessStatsVO.IpAccessVO ipVO = new AccessStatsVO.IpAccessVO();
                ipVO.setIpAddress(entry.getKey());
                ipVO.setCount(entry.getValue());
                return ipVO;
            })
            .collect(Collectors.toList());
        
        statsVO.setTopIps(topIps);
        
        // 平均响应时间
        double avgResponseTime = allLogs.stream()
            .mapToLong(AccessLog::getResponseTime)
            .average()
            .orElse(0.0);
        
        statsVO.setAvgResponseTime(avgResponseTime);
        
        return statsVO;
    }

    @Override
    public SystemHealthVO getSystemHealth() {
        SystemHealthVO healthVO = new SystemHealthVO();
        
        try {
            // 获取操作系统信息
            OperatingSystemMXBean osMXBean = ManagementFactory.getOperatingSystemMXBean();
            healthVO.setCpuUsage(osMXBean.getSystemLoadAverage());
            healthVO.setOsName(osMXBean.getName() + " " + osMXBean.getVersion());
            
            // 获取JVM内存信息
            MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
            healthVO.setJvmTotalMemory(memoryMXBean.getHeapMemoryUsage().getCommitted());
            healthVO.setJvmUsedMemory(memoryMXBean.getHeapMemoryUsage().getUsed());
            healthVO.setJvmFreeMemory(healthVO.getJvmTotalMemory() - healthVO.getJvmUsedMemory());
            
            // 获取运行时信息
            RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
            healthVO.setJavaVersion(System.getProperty("java.version"));
            healthVO.setUpTime(runtimeMXBean.getUptime());
            
            // 设置服务状态
            healthVO.setStatus("UP");
            Map<String, Object> details = new HashMap<>();
            Map<String, Object> components = new HashMap<>();
            
            Map<String, Object> dbStatus = new HashMap<>();
            dbStatus.put("status", "UP");
            
            Map<String, Object> diskStatus = new HashMap<>();
            diskStatus.put("status", "UP");
            diskStatus.put("total", "unknown");
            diskStatus.put("free", "unknown");
            
            components.put("db", dbStatus);
            components.put("diskSpace", diskStatus);
            
            healthVO.setComponents(components);
            healthVO.setDetails(details);
            
        } catch (Exception e) {
            healthVO.setStatus("DOWN");
        }
        
        return healthVO;
    }
} 