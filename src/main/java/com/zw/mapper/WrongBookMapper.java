package com.zw.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zw.entity.WrongBook;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface WrongBookMapper extends BaseMapper<WrongBook> {
    
    @Select("SELECT * FROM exam_wrong_book WHERE user_id = #{userId} ORDER BY last_wrong_time DESC")
    List<WrongBook> selectByUserId(@Param("userId") Long userId);
    
    @Select("SELECT * FROM exam_wrong_book WHERE user_id = #{userId} AND question_id = #{questionId}")
    WrongBook selectByUserIdAndQuestionId(@Param("userId") Long userId, @Param("questionId") Long questionId);
} 