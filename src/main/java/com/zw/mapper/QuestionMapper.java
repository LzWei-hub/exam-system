package com.zw.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zw.entity.Question;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface QuestionMapper extends BaseMapper<Question> {
    
    @Select("SELECT * FROM exam_question WHERE subject_id = #{subjectId} " +
            "AND question_type = #{questionType} AND review_status = 1 " +
            "ORDER BY RAND() LIMIT #{count}")
    List<Question> selectRandomQuestions(@Param("subjectId") Long subjectId,
                                       @Param("questionType") String questionType,
                                       @Param("count") Integer count);
} 