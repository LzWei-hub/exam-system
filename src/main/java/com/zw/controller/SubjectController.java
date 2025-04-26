package com.zw.controller;

import com.zw.common.Result;
import com.zw.entity.Subject;
import com.zw.service.SubjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "科目管理", description = "科目分类相关接口")
@RestController
@RequestMapping("/api/subjects")
@RequiredArgsConstructor
public class SubjectController {

    private final SubjectService subjectService;

    @Operation(summary = "获取科目树", description = "获取完整的科目分类树结构")
    @ApiResponse(responseCode = "200", description = "获取成功")
    @GetMapping("/tree")
    public Result<List<Subject>> getSubjectTree() {
        List<Subject> tree = subjectService.getSubjectTree();
        return Result.success(tree);
    }

    @Operation(summary = "获取科目详情", description = "根据ID获取科目信息")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "获取成功"),
        @ApiResponse(responseCode = "404", description = "科目不存在")
    })
    @GetMapping("/{id}")
    public Result<Subject> getSubjectById(
            @Parameter(description = "科目ID", required = true)
            @PathVariable Long id) {
        Subject subject = subjectService.getById(id);
        if (subject == null) {
            return Result.error("科目不存在");
        }
        return Result.success(subject);
    }

    @Operation(summary = "获取子科目", description = "获取指定科目的所有直接子科目")
    @ApiResponse(responseCode = "200", description = "获取成功")
    @GetMapping("/children")
    public Result<List<Subject>> getChildren(
            @Parameter(description = "父科目ID", required = true)
            @RequestParam Long parentId) {
        List<Subject> children = subjectService.getChildren(parentId);
        return Result.success(children);
    }

    @Operation(summary = "获取父科目ID列表", description = "获取指定科目的所有父级科目ID列表")
    @ApiResponse(responseCode = "200", description = "获取成功")
    @GetMapping("/parents")
    public Result<List<Long>> getParentIds(
            @Parameter(description = "科目ID", required = true)
            @RequestParam Long subjectId) {
        List<Long> parentIds = subjectService.getParentIds(subjectId);
        return Result.success(parentIds);
    }

    @Operation(summary = "创建科目", description = "创建新的科目分类")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "创建成功"),
        @ApiResponse(responseCode = "400", description = "科目名称已存在"),
        @ApiResponse(responseCode = "403", description = "无权限")
    })
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public Result<Subject> createSubject(
            @Parameter(description = "科目信息", required = true)
            @RequestBody Subject subject) {
        // 检查名称是否已存在
        if (subjectService.checkNameExists(subject.getName(), subject.getParentId())) {
            return Result.error("同级下已存在相同名称的科目");
        }
        
        subjectService.save(subject);
        return Result.success(subject);
    }

    @Operation(summary = "更新科目", description = "更新科目信息")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "更新成功"),
        @ApiResponse(responseCode = "400", description = "科目名称已存在"),
        @ApiResponse(responseCode = "403", description = "无权限"),
        @ApiResponse(responseCode = "404", description = "科目不存在")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public Result<Subject> updateSubject(
            @Parameter(description = "科目ID", required = true)
            @PathVariable Long id,
            @Parameter(description = "科目信息", required = true)
            @RequestBody Subject subject) {
        Subject existingSubject = subjectService.getById(id);
        if (existingSubject == null) {
            return Result.error("科目不存在");
        }
        
        // 检查名称是否已存在（排除自身）
        if (!existingSubject.getName().equals(subject.getName()) && 
                subjectService.checkNameExists(subject.getName(), subject.getParentId())) {
            return Result.error("同级下已存在相同名称的科目");
        }
        
        subject.setId(id);
        subjectService.updateById(subject);
        return Result.success(subject);
    }

    @Operation(summary = "删除科目", description = "删除科目及其所有子科目")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "删除成功"),
        @ApiResponse(responseCode = "403", description = "无权限"),
        @ApiResponse(responseCode = "404", description = "科目不存在")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public Result<Void> deleteSubject(
            @Parameter(description = "科目ID", required = true)
            @PathVariable Long id) {
        Subject subject = subjectService.getById(id);
        if (subject == null) {
            return Result.error("科目不存在");
        }
        
        // 删除科目及其子科目
        subjectService.deleteWithChildren(id);
        return Result.success();
    }
} 