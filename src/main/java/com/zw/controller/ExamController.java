package com.zw.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zw.common.Result;
import com.zw.entity.ExamRecord;
import com.zw.service.ExamRecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Tag(name = "考试管理", description = "考试相关接口")
@RestController
@RequestMapping("/api/exams")
@RequiredArgsConstructor
public class ExamController {

    private final ExamRecordService examRecordService;

    @Operation(summary = "开始考试", description = "学生开始一场新的考试")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "考试开始成功"),
        @ApiResponse(responseCode = "400", description = "已参加过该考试"),
        @ApiResponse(responseCode = "403", description = "无权限")
    })
    @PostMapping("/start")
    @PreAuthorize("hasRole('STUDENT')")
    public Result<ExamRecord> startExam(
            @Parameter(description = "用户ID") @RequestParam Long userId,
            @Parameter(description = "试卷ID") @RequestParam Long paperId) {
        
        // 检查用户是否已参加过该考试
        if (examRecordService.hasUserTakenExam(userId, paperId)) {
            return Result.error("您已参加过该考试");
        }
        
        ExamRecord record = examRecordService.startExam(userId, paperId);
        return Result.success(record);
    }

    @Operation(summary = "提交考试", description = "学生提交考试答案")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "提交成功"),
        @ApiResponse(responseCode = "403", description = "无权限")
    })
    @PostMapping("/submit")
    @PreAuthorize("hasRole('STUDENT')")
    public Result<ExamRecord> submitExam(
            @Parameter(description = "考试记录ID") @RequestParam Long recordId,
            @Parameter(description = "答案快照") @RequestBody String answerSnapshot) {
        
        ExamRecord record = examRecordService.submitExam(recordId, answerSnapshot);
        return Result.success(record);
    }

    @Operation(summary = "自动提交考试", description = "管理员或教师强制提交学生的考试")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "提交成功"),
        @ApiResponse(responseCode = "403", description = "无权限")
    })
    @PostMapping("/auto-submit")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public Result<Void> autoSubmitExam(
            @Parameter(description = "考试记录ID") @RequestParam Long recordId) {
        examRecordService.autoSubmitExam(recordId);
        return Result.success();
    }

    @Operation(summary = "人工评分", description = "管理员或教师对考试进行人工评分")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "评分成功"),
        @ApiResponse(responseCode = "403", description = "无权限")
    })
    @PostMapping("/score")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public Result<Void> manualScore(
            @Parameter(description = "考试记录ID") @RequestParam Long recordId,
            @Parameter(description = "分数") @RequestParam BigDecimal score) {
        
        examRecordService.manualScore(recordId, score);
        return Result.success();
    }

    @Operation(summary = "获取考试记录详情")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "获取成功"),
        @ApiResponse(responseCode = "404", description = "考试记录不存在")
    })
    @GetMapping("/records/{id}")
    public Result<ExamRecord> getRecordDetail(
            @Parameter(description = "考试记录ID") @PathVariable Long id) {
        ExamRecord record = examRecordService.getRecordDetail(id);
        if (record == null) {
            return Result.error("考试记录不存在");
        }
        return Result.success(record);
    }

    @Operation(summary = "获取用户的考试记录列表")
    @ApiResponse(responseCode = "200", description = "获取成功")
    @GetMapping("/records/user/{userId}")
    public Result<List<ExamRecord>> getUserRecords(
            @Parameter(description = "用户ID") @PathVariable Long userId) {
        List<ExamRecord> records = examRecordService.getUserRecords(userId);
        return Result.success(records);
    }

    @Operation(summary = "分页查询考试记录", description = "管理员或教师查询考试记录")
    @ApiResponse(responseCode = "200", description = "查询成功")
    @GetMapping("/records")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public Result<IPage<ExamRecord>> getRecordPage(
            @Parameter(description = "用户ID（可选）") @RequestParam(required = false) Long userId,
            @Parameter(description = "试卷ID（可选）") @RequestParam(required = false) Long paperId,
            @Parameter(description = "考试状态（可选）") @RequestParam(required = false) String status,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer pageSize) {
        
        IPage<ExamRecord> page = examRecordService.getRecordPage(
                userId, paperId, status, pageNum, pageSize);
        return Result.success(page);
    }

    @Operation(summary = "获取考试统计数据", description = "管理员或教师获取某场考试的统计数据")
    @ApiResponse(responseCode = "200", description = "获取成功")
    @GetMapping("/statistics/{paperId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public Result<Map<String, Object>> getExamStatistics(
            @Parameter(description = "试卷ID") @PathVariable Long paperId) {
        Map<String, Object> statistics = examRecordService.getExamStatistics(paperId);
        return Result.success(statistics);
    }
} 