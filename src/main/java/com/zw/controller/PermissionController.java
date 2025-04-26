package com.zw.controller;

import com.zw.common.Result;
import com.zw.entity.Permission;
import com.zw.service.PermissionService;
import com.zw.vo.PermissionTreeVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "权限管理", description = "系统权限相关接口")
@RestController
@RequestMapping("/api/admin/permissions")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class PermissionController {
    
    private final PermissionService permissionService;
    
    @Operation(summary = "获取权限树", description = "获取系统权限的树形结构")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "获取成功"),
        @ApiResponse(responseCode = "403", description = "无权限")
    })
    @GetMapping("/tree")
    public Result<List<PermissionTreeVO>> getPermissionTree() {
        List<PermissionTreeVO> permissionTree = permissionService.getPermissionTree();
        return Result.success(permissionTree);
    }
    
    @Operation(summary = "获取用户权限", description = "获取指定用户的权限列表")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "获取成功"),
        @ApiResponse(responseCode = "403", description = "无权限"),
        @ApiResponse(responseCode = "404", description = "用户不存在")
    })
    @GetMapping("/user/{userId}")
    public Result<List<Permission>> getUserPermissions(
            @Parameter(description = "用户ID", required = true)
            @PathVariable("userId") Long userId) {
        List<Permission> permissions = permissionService.getUserPermissions(userId);
        return Result.success(permissions);
    }
} 