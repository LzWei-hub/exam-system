package com.zw.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zw.entity.ExamRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
import java.util.List;

@Mapper
public interface ExamRecordMapper extends BaseMapper<ExamRecord> {
    
    @Select("SELECT COUNT(*) FROM exam_record WHERE paper_id = #{paperId}")
    Long countByPaperId(@Param("paperId") Long paperId);
    
    @Select("SELECT AVG(auto_score + manual_score) FROM exam_record WHERE paper_id = #{paperId} AND status = 'REVIEWING'")
    BigDecimal getAverageScore(@Param("paperId") Long paperId);
    
    @Select("SELECT MAX(auto_score + manual_score) FROM exam_record WHERE paper_id = #{paperId} AND status = 'REVIEWING'")
    BigDecimal getHighestScore(@Param("paperId") Long paperId);
    
    @Select("SELECT MIN(auto_score + manual_score) FROM exam_record WHERE paper_id = #{paperId} AND status = 'REVIEWING'")
    BigDecimal getLowestScore(@Param("paperId") Long paperId);
    
    @Select("SELECT COUNT(*) FROM exam_record WHERE user_id = #{userId} AND paper_id = #{paperId}")
    Integer countByUserIdAndPaperId(@Param("userId") Long userId, @Param("paperId") Long paperId);
    
    @Select("SELECT * FROM exam_record WHERE user_id = #{userId} ORDER BY create_time DESC")
    List<ExamRecord> selectByUserId(@Param("userId") Long userId);
} 