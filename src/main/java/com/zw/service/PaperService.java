package com.zw.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zw.entity.Paper;

import java.util.List;

public interface PaperService extends BaseService<Paper> {
    
    /**
     * 分页查询试卷列表
     */
    IPage<Paper> getPaperPage(Long subjectId, String title, Integer status, Integer pageNum, Integer pageSize);
    
    /**
     * 获取试卷详情
     */
    Paper getPaperDetail(Long id);
    
    /**
     * 创建试卷
     */
    void createPaper(Paper paper);
    
    /**
     * 更新试卷
     */
    void updatePaper(Paper paper);
    
    /**
     * 发布试卷
     */
    void publishPaper(Long id);
    
    /**
     * 归档试卷
     */
    void archivePaper(Long id);
    
    /**
     * 删除试卷
     */
    void deletePaper(Long id);
    
    /**
     * 获取用户可参加的考试列表
     */
    List<Paper> getAvailableExams(Long userId);
    
    /**
     * 获取用户已参加的考试列表
     */
    List<Paper> getUserExams(Long userId);
} 