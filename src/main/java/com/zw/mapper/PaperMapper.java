package com.zw.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zw.entity.Paper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface PaperMapper extends BaseMapper<Paper> {

    @Select("SELECT p.* FROM exam_paper p " +
            "LEFT JOIN exam_record r ON p.id = r.paper_id AND r.user_id = #{userId} " +
            "WHERE p.status = 1 AND p.exam_start <= #{now} AND p.exam_end >= #{now} " +
            "AND r.id IS NULL")
    List<Paper> selectAvailableExams(@Param("userId") Long userId, @Param("now") LocalDateTime now);
    
    @Select("SELECT p.* FROM exam_paper p " +
            "INNER JOIN exam_record r ON p.id = r.paper_id " +
            "WHERE r.user_id = #{userId}")
    List<Paper> selectUserExams(@Param("userId") Long userId);
} 