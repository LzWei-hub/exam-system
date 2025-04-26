package com.zw.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zw.entity.Question;
import com.zw.mapper.QuestionMapper;
import com.zw.service.QuestionService;
import com.zw.service.SubjectService;
import com.zw.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class QuestionServiceImpl extends ServiceImpl<QuestionMapper, Question> implements QuestionService {
    
    @Autowired
    private SubjectService subjectService;
    
    @Autowired
    private UserService userService;
    
    @Override
    public IPage<Question> getQuestionPage(Long subjectId, String questionType, String content,
                                         Integer difficulty, Integer reviewStatus, Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<Question> wrapper = new LambdaQueryWrapper<>();
        
        // 构建查询条件
        if (subjectId != null) {
            wrapper.eq(Question::getSubjectId, subjectId);
        }
        if (StringUtils.hasText(questionType)) {
            wrapper.eq(Question::getQuestionType, questionType);
        }
        if (StringUtils.hasText(content)) {
            wrapper.like(Question::getContent, content);
        }
        if (difficulty != null) {
            wrapper.eq(Question::getDifficulty, difficulty);
        }
        if (reviewStatus != null) {
            wrapper.eq(Question::getReviewStatus, reviewStatus);
        }
        
        // 按创建时间降序排序
        wrapper.orderByDesc(Question::getCreateTime);
        
        // 执行分页查询
        Page<Question> page = new Page<>(pageNum, pageSize);
        IPage<Question> questionPage = page(page, wrapper);
        
        // 设置关联信息
        questionPage.getRecords().forEach(this::setQuestionAssociations);
        
        return questionPage;
    }
    
    @Override
    public Question getQuestionDetail(Long id) {
        Question question = getById(id);
        if (question != null) {
            setQuestionAssociations(question);
        }
        return question;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createQuestion(Question question) {
        // 设置初始审核状态
        question.setReviewStatus(0);
        save(question);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateQuestion(Question question) {
        // 重置审核状态
        question.setReviewStatus(0);
        updateById(question);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void reviewQuestion(Long id, Integer reviewStatus) {
        Question question = new Question();
        question.setId(id);
        question.setReviewStatus(reviewStatus);
        updateById(question);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteQuestions(List<Long> ids) {
        removeBatchByIds(ids);
    }
    
    @Override
    public long getQuestionCountBySubject(Long subjectId) {
        LambdaQueryWrapper<Question> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Question::getSubjectId, subjectId);
        return count(wrapper);
    }
    
    @Override
    public List<Question> getRandomQuestions(Long subjectId, String questionType, Integer count) {
        return baseMapper.selectRandomQuestions(subjectId, questionType, count);
    }
    
    /**
     * 设置题目关联信息（科目和创建人）
     */
    private void setQuestionAssociations(Question question) {
        question.setSubject(subjectService.getById(question.getSubjectId()));
        question.setCreator(userService.getById(question.getCreatorId()));
    }
} 