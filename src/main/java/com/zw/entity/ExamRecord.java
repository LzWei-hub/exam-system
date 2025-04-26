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
@TableName("exam_record")
public class ExamRecord extends BaseEntity {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long userId;
    
    private Long paperId;
    
    private LocalDateTime startTime;
    
    private LocalDateTime submitTime;
    
    private String answerSnapshot;
    
    private BigDecimal autoScore;
    
    private BigDecimal manualScore;
    
    private String status;
    
    @TableField(exist = false)
    private User user;
    
    @TableField(exist = false)
    private Paper paper;
    
    @TableField(exist = false)
    private BigDecimal finalScore;
    
    public BigDecimal getFinalScore() {
        if (autoScore == null) {
            autoScore = new BigDecimal("0.0");
        }
        if (manualScore == null) {
            manualScore = new BigDecimal("0.0");
        }
        return autoScore.add(manualScore);
    }
} 