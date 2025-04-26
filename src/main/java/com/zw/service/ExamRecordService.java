package com.zw.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zw.entity.ExamRecord;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface ExamRecordService extends BaseService<ExamRecord> {
    
    /**
     * 开始考试
     */
    ExamRecord startExam(Long userId, Long paperId);
    
    /**
     * 提交考试
     */
    ExamRecord submitExam(Long recordId, String answerSnapshot);
    
    /**
     * 超时自动提交
     */
    void autoSubmitExam(Long recordId);
    
    /**
     * 人工评分
     */
    void manualScore(Long recordId, BigDecimal score);
    
    /**
     * 获取考试记录详情
     */
    ExamRecord getRecordDetail(Long recordId);
    
    /**
     * 获取用户所有考试记录
     */
    List<ExamRecord> getUserRecords(Long userId);
    
    /**
     * 分页查询考试记录
     */
    IPage<ExamRecord> getRecordPage(Long userId, Long paperId, String status, Integer pageNum, Integer pageSize);
    
    /**
     * 获取考试结果统计
     */
    Map<String, Object> getExamStatistics(Long paperId);
    
    /**
     * 判断用户是否已参加考试
     */
    boolean hasUserTakenExam(Long userId, Long paperId);
    
    /**
     * 计算自动评分
     */
    BigDecimal calculateAutoScore(Long paperId, String answerSnapshot);
} 