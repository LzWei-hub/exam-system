package com.zw.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zw.entity.ExamRecord;
import com.zw.entity.Paper;
import com.zw.mapper.ExamRecordMapper;
import com.zw.service.ExamRecordService;
import com.zw.service.PaperService;
import com.zw.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ExamRecordServiceImpl extends ServiceImpl<ExamRecordMapper, ExamRecord> implements ExamRecordService {

    @Autowired
    private PaperService paperService;
    
    @Autowired
    private UserService userService;
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ExamRecord startExam(Long userId, Long paperId) {
        // 检查用户是否已参加该考试
        if (hasUserTakenExam(userId, paperId)) {
            throw new RuntimeException("您已经参加过该考试");
        }
        
        // 检查考试是否在有效期内
        Paper paper = paperService.getById(paperId);
        if (paper == null) {
            throw new RuntimeException("考试不存在");
        }
        
        LocalDateTime now = LocalDateTime.now();
        if (paper.getExamStart() != null && now.isBefore(paper.getExamStart())) {
            throw new RuntimeException("考试尚未开始");
        }
        if (paper.getExamEnd() != null && now.isAfter(paper.getExamEnd())) {
            throw new RuntimeException("考试已结束");
        }
        
        // 创建考试记录
        ExamRecord record = new ExamRecord();
        record.setUserId(userId);
        record.setPaperId(paperId);
        record.setStartTime(now);
        record.setStatus("PROGRESS");
        record.setAutoScore(new BigDecimal("0"));
        record.setManualScore(new BigDecimal("0"));
        
        save(record);
        
        return record;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ExamRecord submitExam(Long recordId, String answerSnapshot) {
        ExamRecord record = getById(recordId);
        if (record == null) {
            throw new RuntimeException("考试记录不存在");
        }
        
        if (!"PROGRESS".equals(record.getStatus())) {
            throw new RuntimeException("该考试已结束，不能重复提交");
        }
        
        LocalDateTime now = LocalDateTime.now();
        record.setSubmitTime(now);
        record.setAnswerSnapshot(answerSnapshot);
        record.setStatus("SUBMITTED");
        
        // 计算自动评分
        BigDecimal autoScore = calculateAutoScore(record.getPaperId(), answerSnapshot);
        record.setAutoScore(autoScore);
        
        // 更新记录
        updateById(record);
        
        return record;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void autoSubmitExam(Long recordId) {
        ExamRecord record = getById(recordId);
        if (record == null || !"PROGRESS".equals(record.getStatus())) {
            return;
        }
        
        record.setSubmitTime(LocalDateTime.now());
        record.setStatus("TIMEOUT");
        updateById(record);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void manualScore(Long recordId, BigDecimal score) {
        ExamRecord record = getById(recordId);
        if (record == null) {
            throw new RuntimeException("考试记录不存在");
        }
        
        if (!"SUBMITTED".equals(record.getStatus()) && !"TIMEOUT".equals(record.getStatus())) {
            throw new RuntimeException("当前状态不允许评分");
        }
        
        record.setManualScore(score);
        record.setStatus("REVIEWING");
        updateById(record);
    }
    
    @Override
    public ExamRecord getRecordDetail(Long recordId) {
        ExamRecord record = getById(recordId);
        if (record != null) {
            record.setUser(userService.getById(record.getUserId()));
            record.setPaper(paperService.getById(record.getPaperId()));
        }
        return record;
    }
    
    @Override
    public List<ExamRecord> getUserRecords(Long userId) {
        List<ExamRecord> records = baseMapper.selectByUserId(userId);
        for (ExamRecord record : records) {
            record.setPaper(paperService.getById(record.getPaperId()));
        }
        return records;
    }
    
    @Override
    public IPage<ExamRecord> getRecordPage(Long userId, Long paperId, String status, Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<ExamRecord> wrapper = new LambdaQueryWrapper<>();
        
        if (userId != null) {
            wrapper.eq(ExamRecord::getUserId, userId);
        }
        if (paperId != null) {
            wrapper.eq(ExamRecord::getPaperId, paperId);
        }
        if (status != null && !status.isEmpty()) {
            wrapper.eq(ExamRecord::getStatus, status);
        }
        
        wrapper.orderByDesc(ExamRecord::getCreateTime);
        
        Page<ExamRecord> page = new Page<>(pageNum, pageSize);
        IPage<ExamRecord> recordPage = page(page, wrapper);
        
        // 设置关联信息
        for (ExamRecord record : recordPage.getRecords()) {
            record.setUser(userService.getById(record.getUserId()));
            record.setPaper(paperService.getById(record.getPaperId()));
        }
        
        return recordPage;
    }
    
    @Override
    public Map<String, Object> getExamStatistics(Long paperId) {
        Map<String, Object> statistics = new HashMap<>();
        
        // 参考人数
        Long totalCount = baseMapper.countByPaperId(paperId);
        statistics.put("totalCount", totalCount);
        
        // 平均分
        BigDecimal avgScore = baseMapper.getAverageScore(paperId);
        statistics.put("averageScore", avgScore != null ? avgScore : new BigDecimal("0"));
        
        // 最高分
        BigDecimal highestScore = baseMapper.getHighestScore(paperId);
        statistics.put("highestScore", highestScore != null ? highestScore : new BigDecimal("0"));
        
        // 最低分
        BigDecimal lowestScore = baseMapper.getLowestScore(paperId);
        statistics.put("lowestScore", lowestScore != null ? lowestScore : new BigDecimal("0"));
        
        return statistics;
    }
    
    @Override
    public boolean hasUserTakenExam(Long userId, Long paperId) {
        Integer count = baseMapper.countByUserIdAndPaperId(userId, paperId);
        return count != null && count > 0;
    }
    
    @Override
    public BigDecimal calculateAutoScore(Long paperId, String answerSnapshot) {
        // TODO: 实现自动评分逻辑
        // 这需要解析答题快照和试卷信息，比对答案并计算分数
        // 简单起见，这里先返回0分，实际实现需要根据具体业务逻辑
        return new BigDecimal("0");
    }
} 