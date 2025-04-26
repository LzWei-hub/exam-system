package com.zw.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zw.entity.Subject;
import com.zw.mapper.SubjectMapper;
import com.zw.service.SubjectService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class SubjectServiceImpl extends ServiceImpl<SubjectMapper, Subject> implements SubjectService {

    @Override
    public List<Subject> getSubjectTree() {
        // 获取所有科目
        List<Subject> allSubjects = list();
        
        // 构建父子关系
        Map<Long, List<Subject>> parentMap = allSubjects.stream()
                .collect(Collectors.groupingBy(Subject::getParentId));
        
        // 设置子节点
        allSubjects.forEach(subject -> 
            subject.setChildren(parentMap.getOrDefault(subject.getId(), new ArrayList<>()))
        );
        
        // 返回顶层节点
        return allSubjects.stream()
                .filter(subject -> subject.getParentId() == 0)
                .collect(Collectors.toList());
    }

    @Override
    public List<Subject> getChildren(Long parentId) {
        LambdaQueryWrapper<Subject> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Subject::getParentId, parentId);
        wrapper.orderByAsc(Subject::getSort);
        return list(wrapper);
    }

    @Override
    public List<Long> getParentIds(Long subjectId) {
        List<Long> parentIds = new ArrayList<>();
        Subject subject = getById(subjectId);
        
        while (subject != null && subject.getParentId() != 0) {
            parentIds.add(subject.getParentId());
            subject = getById(subject.getParentId());
        }
        
        return parentIds;
    }

    @Override
    public boolean checkNameExists(String name, Long parentId) {
        LambdaQueryWrapper<Subject> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Subject::getName, name);
        wrapper.eq(Subject::getParentId, parentId);
        return count(wrapper) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteWithChildren(Long id) {
        // 获取所有子科目ID
        Set<Long> ids = new HashSet<>();
        ids.add(id);
        collectChildrenIds(id, ids);
        
        // 批量删除
        removeBatchByIds(ids);
    }
    
    private void collectChildrenIds(Long parentId, Set<Long> ids) {
        List<Subject> children = getChildren(parentId);
        for (Subject child : children) {
            ids.add(child.getId());
            collectChildrenIds(child.getId(), ids);
        }
    }
} 