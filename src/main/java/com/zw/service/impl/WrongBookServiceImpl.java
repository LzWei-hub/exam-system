package com.zw.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zw.entity.Question;
import com.zw.entity.WrongBook;
import com.zw.mapper.WrongBookMapper;
import com.zw.service.QuestionService;
import com.zw.service.WrongBookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class WrongBookServiceImpl extends ServiceImpl<WrongBookMapper, WrongBook> implements WrongBookService {

    @Autowired
    private QuestionService questionService;
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addWrongQuestion(Long userId, Long questionId) {
        // 检查题目是否存在
        Question question = questionService.getById(questionId);
        if (question == null) {
            throw new RuntimeException("题目不存在");
        }
        
        // 查询是否已存在该错题记录
        WrongBook wrongBook = baseMapper.selectByUserIdAndQuestionId(userId, questionId);
        
        if (wrongBook == null) {
            // 创建新记录
            wrongBook = new WrongBook();
            wrongBook.setUserId(userId);
            wrongBook.setQuestionId(questionId);
            wrongBook.setWrongCount(1);
            wrongBook.setLastWrongTime(LocalDateTime.now());
            save(wrongBook);
        } else {
            // 更新错误次数和最后错误时间
            wrongBook.setWrongCount(wrongBook.getWrongCount() + 1);
            wrongBook.setLastWrongTime(LocalDateTime.now());
            updateById(wrongBook);
        }
    }
    
    @Override
    public List<WrongBook> getUserWrongBooks(Long userId) {
        List<WrongBook> wrongBooks = baseMapper.selectByUserId(userId);
        for (WrongBook wrongBook : wrongBooks) {
            wrongBook.setQuestion(questionService.getById(wrongBook.getQuestionId()));
        }
        return wrongBooks;
    }
    
    @Override
    public IPage<WrongBook> getWrongBookPage(Long userId, Long subjectId, String keyword, Integer pageNum, Integer pageSize) {
        // 构建查询条件
        LambdaQueryWrapper<WrongBook> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WrongBook::getUserId, userId);
        
        // 如果指定了科目或关键字，需要关联题目表进行查询
        if (subjectId != null || StringUtils.hasText(keyword)) {
            // 这种复杂查询应该在Mapper中实现，这里简化处理
            // 先获取所有错题，然后在内存中过滤
            Page<WrongBook> page = new Page<>(pageNum, pageSize);
            IPage<WrongBook> wrongBookPage = page(page, wrapper);
            
            // 设置关联信息并过滤
            List<WrongBook> records = wrongBookPage.getRecords();
            for (int i = records.size() - 1; i >= 0; i--) {
                WrongBook wrongBook = records.get(i);
                Question question = questionService.getById(wrongBook.getQuestionId());
                wrongBook.setQuestion(question);
                
                // 根据科目和关键字过滤
                boolean remove = false;
                
                if (subjectId != null && !subjectId.equals(question.getSubjectId())) {
                    remove = true;
                }
                
                if (StringUtils.hasText(keyword) && !question.getContent().contains(keyword)) {
                    remove = true;
                }
                
                if (remove) {
                    records.remove(i);
                }
            }
            
            return wrongBookPage;
        } else {
            // 不需要关联查询的情况
            wrapper.orderByDesc(WrongBook::getLastWrongTime);
            
            Page<WrongBook> page = new Page<>(pageNum, pageSize);
            IPage<WrongBook> wrongBookPage = page(page, wrapper);
            
            // 设置关联信息
            for (WrongBook wrongBook : wrongBookPage.getRecords()) {
                wrongBook.setQuestion(questionService.getById(wrongBook.getQuestionId()));
            }
            
            return wrongBookPage;
        }
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteWrongQuestion(Long userId, Long questionId) {
        LambdaQueryWrapper<WrongBook> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WrongBook::getUserId, userId);
        wrapper.eq(WrongBook::getQuestionId, questionId);
        remove(wrapper);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void clearUserWrongBook(Long userId) {
        LambdaQueryWrapper<WrongBook> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WrongBook::getUserId, userId);
        remove(wrapper);
    }
    
    @Override
    public boolean isQuestionInWrongBook(Long userId, Long questionId) {
        LambdaQueryWrapper<WrongBook> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WrongBook::getUserId, userId);
        wrapper.eq(WrongBook::getQuestionId, questionId);
        return count(wrapper) > 0;
    }
} 