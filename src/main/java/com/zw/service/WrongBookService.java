package com.zw.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zw.entity.WrongBook;

import java.util.List;

public interface WrongBookService extends BaseService<WrongBook> {
    
    /**
     * 添加错题
     */
    void addWrongQuestion(Long userId, Long questionId);
    
    /**
     * 获取用户的错题本列表
     */
    List<WrongBook> getUserWrongBooks(Long userId);
    
    /**
     * 分页查询用户的错题本
     */
    IPage<WrongBook> getWrongBookPage(Long userId, Long subjectId, String keyword, Integer pageNum, Integer pageSize);
    
    /**
     * 删除错题记录
     */
    void deleteWrongQuestion(Long userId, Long questionId);
    
    /**
     * 清空用户错题本
     */
    void clearUserWrongBook(Long userId);
    
    /**
     * 判断题目是否在用户的错题本中
     */
    boolean isQuestionInWrongBook(Long userId, Long questionId);
} 