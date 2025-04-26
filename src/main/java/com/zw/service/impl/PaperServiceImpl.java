package com.zw.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zw.entity.Paper;
import com.zw.mapper.PaperMapper;
import com.zw.service.PaperService;
import com.zw.service.SubjectService;
import com.zw.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PaperServiceImpl extends ServiceImpl<PaperMapper, Paper> implements PaperService {

    @Autowired
    private SubjectService subjectService;
    
    @Autowired
    private UserService userService;
    
    @Override
    public IPage<Paper> getPaperPage(Long subjectId, String title, Integer status, Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<Paper> wrapper = new LambdaQueryWrapper<>();
        
        // 构建查询条件
        if (subjectId != null) {
            wrapper.eq(Paper::getSubjectId, subjectId);
        }
        if (StringUtils.hasText(title)) {
            wrapper.like(Paper::getTitle, title);
        }
        if (status != null) {
            wrapper.eq(Paper::getStatus, status);
        }
        
        // 按创建时间降序排序
        wrapper.orderByDesc(Paper::getCreateTime);
        
        // 执行分页查询
        Page<Paper> page = new Page<>(pageNum, pageSize);
        IPage<Paper> paperPage = page(page, wrapper);
        
        // 设置关联信息
        paperPage.getRecords().forEach(this::setPaperAssociations);
        
        return paperPage;
    }
    
    @Override
    public Paper getPaperDetail(Long id) {
        Paper paper = getById(id);
        if (paper != null) {
            setPaperAssociations(paper);
        }
        return paper;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createPaper(Paper paper) {
        // 设置初始状态为草稿
        paper.setStatus(0);
        save(paper);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePaper(Paper paper) {
        updateById(paper);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void publishPaper(Long id) {
        Paper paper = new Paper();
        paper.setId(id);
        paper.setStatus(1);
        updateById(paper);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void archivePaper(Long id) {
        Paper paper = new Paper();
        paper.setId(id);
        paper.setStatus(2);
        updateById(paper);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deletePaper(Long id) {
        removeById(id);
    }
    
    @Override
    public List<Paper> getAvailableExams(Long userId) {
        LocalDateTime now = LocalDateTime.now();
        List<Paper> papers = baseMapper.selectAvailableExams(userId, now);
        papers.forEach(this::setPaperAssociations);
        return papers;
    }
    
    @Override
    public List<Paper> getUserExams(Long userId) {
        List<Paper> papers = baseMapper.selectUserExams(userId);
        papers.forEach(this::setPaperAssociations);
        return papers;
    }
    
    /**
     * 设置试卷关联信息（科目和创建人）
     */
    private void setPaperAssociations(Paper paper) {
        paper.setSubject(subjectService.getById(paper.getSubjectId()));
        paper.setCreator(userService.getById(paper.getCreatorId()));
    }
} 