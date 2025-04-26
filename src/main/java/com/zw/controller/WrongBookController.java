package com.zw.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zw.common.Result;
import com.zw.entity.WrongBook;
import com.zw.service.WrongBookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "错题本管理", description = "学生错题本相关接口")
@RestController
@RequestMapping("/api/wrong-books")
@RequiredArgsConstructor
@PreAuthorize("hasRole('STUDENT')")
public class WrongBookController {

    private final WrongBookService wrongBookService;

    @Operation(summary = "添加错题", description = "将题目添加到错题本")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "添加成功"),
        @ApiResponse(responseCode = "403", description = "无权限")
    })
    @PostMapping
    public Result<Void> addWrongQuestion(
            @Parameter(description = "用户ID", required = true)
            @RequestParam Long userId,
            @Parameter(description = "试题ID", required = true)
            @RequestParam Long questionId) {
        
        wrongBookService.addWrongQuestion(userId, questionId);
        return Result.success();
    }

    @Operation(summary = "获取用户错题本", description = "获取用户的所有错题")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "获取成功"),
        @ApiResponse(responseCode = "403", description = "无权限")
    })
    @GetMapping("/user/{userId}")
    public Result<List<WrongBook>> getUserWrongBooks(
            @Parameter(description = "用户ID", required = true)
            @PathVariable Long userId) {
        List<WrongBook> wrongBooks = wrongBookService.getUserWrongBooks(userId);
        return Result.success(wrongBooks);
    }

    @Operation(summary = "分页查询错题", description = "根据条件分页查询错题列表")
    @ApiResponse(responseCode = "200", description = "查询成功")
    @GetMapping
    public Result<IPage<WrongBook>> getWrongBookPage(
            @Parameter(description = "用户ID", required = true)
            @RequestParam Long userId,
            @Parameter(description = "科目ID（可选）")
            @RequestParam(required = false) Long subjectId,
            @Parameter(description = "关键词（可选）")
            @RequestParam(required = false) String keyword,
            @Parameter(description = "页码")
            @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小")
            @RequestParam(defaultValue = "10") Integer pageSize) {
        
        IPage<WrongBook> page = wrongBookService.getWrongBookPage(
                userId, subjectId, keyword, pageNum, pageSize);
        return Result.success(page);
    }

    @Operation(summary = "删除错题", description = "从错题本中删除指定题目")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "删除成功"),
        @ApiResponse(responseCode = "403", description = "无权限")
    })
    @DeleteMapping
    public Result<Void> deleteWrongQuestion(
            @Parameter(description = "用户ID", required = true)
            @RequestParam Long userId,
            @Parameter(description = "试题ID", required = true)
            @RequestParam Long questionId) {
        
        wrongBookService.deleteWrongQuestion(userId, questionId);
        return Result.success();
    }

    @Operation(summary = "清空错题本", description = "清空用户的所有错题")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "清空成功"),
        @ApiResponse(responseCode = "403", description = "无权限")
    })
    @DeleteMapping("/clear/{userId}")
    public Result<Void> clearUserWrongBook(
            @Parameter(description = "用户ID", required = true)
            @PathVariable Long userId) {
        wrongBookService.clearUserWrongBook(userId);
        return Result.success();
    }

    @Operation(summary = "检查题目是否在错题本中", description = "检查指定题目是否在用户的错题本中")
    @ApiResponse(responseCode = "200", description = "检查成功")
    @GetMapping("/check")
    public Result<Boolean> isQuestionInWrongBook(
            @Parameter(description = "用户ID", required = true)
            @RequestParam Long userId,
            @Parameter(description = "试题ID", required = true)
            @RequestParam Long questionId) {
        
        boolean result = wrongBookService.isQuestionInWrongBook(userId, questionId);
        return Result.success(result);
    }
} 