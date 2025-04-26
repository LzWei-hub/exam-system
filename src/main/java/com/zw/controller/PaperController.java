package com.zw.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zw.common.Result;
import com.zw.entity.Paper;
import com.zw.service.PaperService;
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

@Tag(name = "试卷管理", description = "试卷相关接口")
@RestController
@RequestMapping("/api/papers")
@RequiredArgsConstructor
public class PaperController {

    private final PaperService paperService;

    @Operation(summary = "分页查询试卷", description = "根据条件分页查询试卷列表")
    @ApiResponse(responseCode = "200", description = "查询成功")
    @GetMapping
    public Result<IPage<Paper>> getPaperPage(
            @Parameter(description = "科目ID（可选）") @RequestParam(required = false) Long subjectId,
            @Parameter(description = "试卷标题（可选）") @RequestParam(required = false) String title,
            @Parameter(description = "试卷状态（可选）：0-草稿，1-已发布，2-已归档") 
            @RequestParam(required = false) Integer status,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer pageSize) {
        
        IPage<Paper> page = paperService.getPaperPage(subjectId, title, status, pageNum, pageSize);
        return Result.success(page);
    }

    @Operation(summary = "获取试卷详情", description = "根据ID获取试卷详细信息")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "获取成功"),
        @ApiResponse(responseCode = "404", description = "试卷不存在")
    })
    @GetMapping("/{id}")
    public Result<Paper> getPaperDetail(
            @Parameter(description = "试卷ID", required = true)
            @PathVariable Long id) {
        Paper paper = paperService.getPaperDetail(id);
        if (paper == null) {
            return Result.error("试卷不存在");
        }
        return Result.success(paper);
    }

    @Operation(summary = "创建试卷", description = "创建新的试卷")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "创建成功"),
        @ApiResponse(responseCode = "400", description = "请求参数错误"),
        @ApiResponse(responseCode = "401", description = "未登录"),
        @ApiResponse(responseCode = "403", description = "无权限")
    })
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public Result<Paper> createPaper(
            @Parameter(description = "试卷信息", required = true)
            @RequestBody Paper paper) {
        // 设置创建人
        String username = SecurityUtils.getCurrentUsername();
        if (username == null) {
            return Result.error("未登录");
        }
        
        paperService.createPaper(paper);
        return Result.success(paper);
    }

    @Operation(summary = "更新试卷", description = "更新试卷信息")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "更新成功"),
        @ApiResponse(responseCode = "403", description = "无权限"),
        @ApiResponse(responseCode = "404", description = "试卷不存在")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public Result<Paper> updatePaper(
            @Parameter(description = "试卷ID", required = true)
            @PathVariable Long id,
            @Parameter(description = "试卷信息", required = true)
            @RequestBody Paper paper) {
        Paper existingPaper = paperService.getById(id);
        if (existingPaper == null) {
            return Result.error("试卷不存在");
        }
        
        paper.setId(id);
        paperService.updatePaper(paper);
        return Result.success(paper);
    }

    @Operation(summary = "发布试卷", description = "将试卷状态改为已发布")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "发布成功"),
        @ApiResponse(responseCode = "403", description = "无权限"),
        @ApiResponse(responseCode = "404", description = "试卷不存在")
    })
    @PutMapping("/{id}/publish")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public Result<Void> publishPaper(
            @Parameter(description = "试卷ID", required = true)
            @PathVariable Long id) {
        Paper paper = paperService.getById(id);
        if (paper == null) {
            return Result.error("试卷不存在");
        }
        
        paperService.publishPaper(id);
        return Result.success();
    }

    @Operation(summary = "归档试卷", description = "将试卷状态改为已归档")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "归档成功"),
        @ApiResponse(responseCode = "403", description = "无权限"),
        @ApiResponse(responseCode = "404", description = "试卷不存在")
    })
    @PutMapping("/{id}/archive")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public Result<Void> archivePaper(
            @Parameter(description = "试卷ID", required = true)
            @PathVariable Long id) {
        Paper paper = paperService.getById(id);
        if (paper == null) {
            return Result.error("试卷不存在");
        }
        
        paperService.archivePaper(id);
        return Result.success();
    }

    @Operation(summary = "删除试卷", description = "删除指定试卷")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "删除成功"),
        @ApiResponse(responseCode = "403", description = "无权限"),
        @ApiResponse(responseCode = "404", description = "试卷不存在")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public Result<Void> deletePaper(
            @Parameter(description = "试卷ID", required = true)
            @PathVariable Long id) {
        Paper paper = paperService.getById(id);
        if (paper == null) {
            return Result.error("试卷不存在");
        }
        
        paperService.deletePaper(id);
        return Result.success();
    }

    @Operation(summary = "获取可参加的考试", description = "获取学生可以参加的考试列表")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "获取成功"),
        @ApiResponse(responseCode = "401", description = "未登录"),
        @ApiResponse(responseCode = "403", description = "无权限")
    })
    @GetMapping("/available")
    @PreAuthorize("hasRole('STUDENT')")
    public Result<List<Paper>> getAvailableExams() {
        // 获取当前用户ID
        String username = SecurityUtils.getCurrentUsername();
        if (username == null) {
            return Result.error("未登录");
        }
        
        Long userId = 1L; // 临时使用固定值，实际应该从SecurityUtils获取
        
        List<Paper> papers = paperService.getAvailableExams(userId);
        return Result.success(papers);
    }

    @Operation(summary = "获取我的考试", description = "获取学生已参加的考试列表")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "获取成功"),
        @ApiResponse(responseCode = "401", description = "未登录"),
        @ApiResponse(responseCode = "403", description = "无权限")
    })
    @GetMapping("/my")
    @PreAuthorize("hasRole('STUDENT')")
    public Result<List<Paper>> getUserExams() {
        // 获取当前用户ID
        String username = SecurityUtils.getCurrentUsername();
        if (username == null) {
            return Result.error("未登录");
        }
        
        Long userId = 1L; // 临时使用固定值，实际应该从SecurityUtils获取
        
        List<Paper> papers = paperService.getUserExams(userId);
        return Result.success(papers);
    }
} 