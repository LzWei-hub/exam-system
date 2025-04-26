package com.zw.vo;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class AccessStatsVO {
    // 总访问量
    private Long totalVisits;
    
    // 当天访问量
    private Long todayVisits;
    
    // 每小时访问统计
    private Map<Integer, Long> hourlyStats;
    
    // 最近一周每天访问统计
    private Map<String, Long> dailyStats;
    
    // 访问量最高的10个页面
    private List<PageAccessVO> topPages;
    
    // 访问量最高的10个IP
    private List<IpAccessVO> topIps;
    
    // 平均响应时间
    private Double avgResponseTime;
    
    @Data
    public static class PageAccessVO {
        private String uri;
        private Long count;
    }
    
    @Data
    public static class IpAccessVO {
        private String ipAddress;
        private Long count;
    }
} 