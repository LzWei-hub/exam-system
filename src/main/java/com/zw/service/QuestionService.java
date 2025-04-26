package com.zw.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zw.entity.Question;

import java.util.List;

public interface QuestionService extends BaseService<Question> {
    
    /**
     * 分页查询题目列表
     */
    IPage<Question> getQuestionPage(Long subjectId, String questionType, String content, 
                                  Integer difficulty, Integer reviewStatus, Integer pageNum, Integer pageSize);
    
    /**
     * 获取题目详情
     */
    Question getQuestionDetail(Long id);
    
    /**
     * 创建题目
     */
    void createQuestion(Question question);
    
    /**
     * 更新题目
     */
    void updateQuestion(Question question);
    
    /**
     * 审核题目
     */
    void reviewQuestion(Long id, Integer reviewStatus);
    
    /**
     * 批量删除题目
     */
    void deleteQuestions(List<Long> ids);
    
    /**
     * 获取科目下的题目数量
     */
    long getQuestionCountBySubject(Long subjectId);
    
    /**
     * 随机获取指定数量的题目
     */
    List<Question> getRandomQuestions(Long subjectId, String questionType, Integer count);
} 