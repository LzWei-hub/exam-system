package com.zw.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("exam_paper")
public class Paper extends BaseEntity {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String title;
    
    private Long subjectId;
    
    private BigDecimal totalScore;
    
    private Integer timeLimit;
    
    private String questionData;
    
    private Long creatorId;
    
    private LocalDateTime examStart;
    
    private LocalDateTime examEnd;
    
    private Integer status;
    
    @TableField(exist = false)
    private Subject subject;
    
    @TableField(exist = false)
    private User creator;
} 