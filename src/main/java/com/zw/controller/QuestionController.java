package com.zw.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zw.common.Result;
import com.zw.entity.Question;
import com.zw.service.QuestionService;
import com.zw.utils.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "题库管理", description = "试题管理相关接口")
@RestController
@RequestMapping("/api/questions")
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionService questionService;

    @Operation(summary = "分页查询试题", description = "根据条件分页查询试题列表")
    @ApiResponse(responseCode = "200", description = "查询成功")
    @GetMapping
    public Result<IPage<Question>> getQuestionPage(
            @Parameter(description = "科目ID（可选）") @RequestParam(required = false) Long subjectId,
            @Parameter(description = "题目类型（可选）：SINGLE-单选题，MULTI-多选题，JUDGE-判断题，FILL-填空题") 
            @RequestParam(required = false) String questionType,
            @Parameter(description = "题目内容（可选）") @RequestParam(required = false) String content,
            @Parameter(description = "难度系数（可选）：1-5") @RequestParam(required = false) Integer difficulty,
            @Parameter(description = "审核状态（可选）：0-待审核，1-已通过，2-已驳回") 
            @RequestParam(required = false) Integer reviewStatus,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer pageSize) {
        
        IPage<Question> page = questionService.getQuestionPage(
                subjectId, questionType, content, difficulty, reviewStatus, pageNum, pageSize);
        return Result.success(page);
    }

    @Operation(summary = "获取试题详情", description = "根据ID获取试题详细信息")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "获取成功"),
        @ApiResponse(responseCode = "404", description = "试题不存在")
    })
    @GetMapping("/{id}")
    public Result<Question> getQuestionDetail(
            @Parameter(description = "试题ID", required = true)
            @PathVariable Long id) {
        Question question = questionService.getQuestionDetail(id);
        if (question == null) {
            return Result.error("题目不存在");
        }
        return Result.success(question);
    }

    @Operation(summary = "创建试题", description = "创建新的试题")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "创建成功"),
        @ApiResponse(responseCode = "400", description = "请求参数错误"),
        @ApiResponse(responseCode = "401", description = "未登录"),
        @ApiResponse(responseCode = "403", description = "无权限")
    })
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public Result<Question> createQuestion(
            @Parameter(description = "试题信息", required = true)
            @RequestBody Question question) {
        // 设置创建人
        String username = SecurityUtils.getCurrentUsername();
        if (username == null) {
            return Result.error("未登录");
        }
        
        questionService.createQuestion(question);
        return Result.success(question);
    }

    @Operation(summary = "更新试题", description = "更新试题信息")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "更新成功"),
        @ApiResponse(responseCode = "403", description = "无权限"),
        @ApiResponse(responseCode = "404", description = "试题不存在")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public Result<Question> updateQuestion(
            @Parameter(description = "试题ID", required = true)
            @PathVariable Long id,
            @Parameter(description = "试题信息", required = true)
            @RequestBody Question question) {
        Question existingQuestion = questionService.getById(id);
        if (existingQuestion == null) {
            return Result.error("题目不存在");
        }
        
        question.setId(id);
        questionService.updateQuestion(question);
        return Result.success(question);
    }

    @Operation(summary = "审核试题", description = "管理员审核试题")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "审核成功"),
        @ApiResponse(responseCode = "403", description = "无权限"),
        @ApiResponse(responseCode = "404", description = "试题不存在")
    })
    @PutMapping("/{id}/review")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> reviewQuestion(
            @Parameter(description = "试题ID", required = true)
            @PathVariable Long id, 
            @Parameter(description = "审核状态：0-待审核，1-已通过，2-已驳回", required = true)
            @RequestParam Integer reviewStatus) {
        Question existingQuestion = questionService.getById(id);
        if (existingQuestion == null) {
            return Result.error("题目不存在");
        }
        
        questionService.reviewQuestion(id, reviewStatus);
        return Result.success();
    }

    @Operation(summary = "删除试题", description = "删除单个试题")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "删除成功"),
        @ApiResponse(responseCode = "403", description = "无权限"),
        @ApiResponse(responseCode = "404", description = "试题不存在")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public Result<Void> deleteQuestion(
            @Parameter(description = "试题ID", required = true)
            @PathVariable Long id) {
        Question question = questionService.getById(id);
        if (question == null) {
            return Result.error("题目不存在");
        }
        
        questionService.removeById(id);
        return Result.success();
    }

    @Operation(summary = "批量删除试题", description = "批量删除多个试题")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "删除成功"),
        @ApiResponse(responseCode = "403", description = "无权限")
    })
    @DeleteMapping("/batch")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public Result<Void> batchDeleteQuestions(
            @Parameter(description = "试题ID列表", required = true)
            @RequestBody List<Long> ids) {
        questionService.deleteQuestions(ids);
        return Result.success();
    }

    @Operation(summary = "获取随机试题", description = "随机获取指定数量的试题")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "获取成功"),
        @ApiResponse(responseCode = "403", description = "无权限")
    })
    @GetMapping("/random")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public Result<List<Question>> getRandomQuestions(
            @Parameter(description = "科目ID", required = true)
            @RequestParam Long subjectId,
            @Parameter(description = "题目类型：SINGLE-单选题，MULTI-多选题，JUDGE-判断题，FILL-填空题", required = true)
            @RequestParam String questionType,
            @Parameter(description = "获取数量", required = true)
            @RequestParam Integer count) {
        List<Question> questions = questionService.getRandomQuestions(subjectId, questionType, count);
        return Result.success(questions);
    }

    @Operation(summary = "获取科目试题数量", description = "获取指定科目下的试题总数")
    @ApiResponse(responseCode = "200", description = "获取成功")
    @GetMapping("/subject/count")
    public Result<Long> getQuestionCountBySubject(
            @Parameter(description = "科目ID", required = true)
            @RequestParam Long subjectId) {
        long count = questionService.getQuestionCountBySubject(subjectId);
        return Result.success(count);
    }
} 