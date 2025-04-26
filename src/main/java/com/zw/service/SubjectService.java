package com.zw.service;

import com.zw.entity.Subject;

import java.util.List;

public interface SubjectService extends BaseService<Subject> {
    
    /**
     * 获取科目树形结构
     */
    List<Subject> getSubjectTree();
    
    /**
     * 获取子科目列表
     */
    List<Subject> getChildren(Long parentId);
    
    /**
     * 获取科目的所有上级科目ID列表
     */
    List<Long> getParentIds(Long subjectId);
    
    /**
     * 检查科目名称是否已存在
     */
    boolean checkNameExists(String name, Long parentId);
    
    /**
     * 删除科目（包括子科目）
     */
    void deleteWithChildren(Long id);
} 