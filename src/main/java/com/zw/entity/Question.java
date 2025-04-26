package com.zw.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("exam_question")
public class Question extends BaseEntity {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long subjectId;
    
    private String questionType;
    
    private String content;
    
    private String options;
    
    private String answer;
    
    private String analysis;
    
    private Integer difficulty;
    
    private BigDecimal score;
    
    private Long creatorId;
    
    private Integer reviewStatus;
    
    @TableField(exist = false)
    private Subject subject;
    
    @TableField(exist = false)
    private User creator;
} 