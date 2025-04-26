package com.zw.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zw.common.Result;
import com.zw.entity.Role;
import com.zw.service.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "角色管理", description = "角色权限相关接口")
@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class RoleController {
    
    private final RoleService roleService;
    
    @Operation(summary = "创建角色", description = "创建新的角色")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "创建成功"),
        @ApiResponse(responseCode = "400", description = "请求参数错误"),
        @ApiResponse(responseCode = "403", description = "无权限")
    })
    @PostMapping
    public Result<Role> createRole(
            @Parameter(description = "角色信息", required = true)
            @RequestBody Role role) {
        roleService.save(role);
        return Result.success(role);
    }
    
    @Operation(summary = "更新角色", description = "更新角色信息")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "更新成功"),
        @ApiResponse(responseCode = "403", description = "无权限"),
        @ApiResponse(responseCode = "404", description = "角色不存在")
    })
    @PutMapping("/{id}")
    public Result<Role> updateRole(
            @Parameter(description = "角色ID", required = true)
            @PathVariable Long id,
            @Parameter(description = "角色信息", required = true)
            @RequestBody Role role) {
        Role existingRole = roleService.getById(id);
        if (existingRole == null) {
            return Result.error("角色不存在");
        }
        role.setId(id);
        roleService.updateById(role);
        return Result.success(role);
    }
    
    @Operation(summary = "删除角色", description = "删除指定角色")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "删除成功"),
        @ApiResponse(responseCode = "403", description = "无权限"),
        @ApiResponse(responseCode = "404", description = "角色不存在")
    })
    @DeleteMapping("/{id}")
    public Result<Void> deleteRole(
            @Parameter(description = "角色ID", required = true)
            @PathVariable Long id) {
        Role role = roleService.getById(id);
        if (role == null) {
            return Result.error("角色不存在");
        }
        roleService.removeById(id);
        return Result.success();
    }
    
    @Operation(summary = "分页查询角色", description = "根据条件分页查询角色列表")
    @ApiResponse(responseCode = "200", description = "查询成功")
    @GetMapping
    public Result<IPage<Role>> getRolePage(
            @Parameter(description = "页码")
            @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小")
            @RequestParam(defaultValue = "10") Integer pageSize,
            @Parameter(description = "角色名称（可选）")
            @RequestParam(required = false) String name) {
        IPage<Role> page = roleService.page(
            new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(pageNum, pageSize)
        );
        return Result.success(page);
    }
    
    @Operation(summary = "获取所有角色", description = "获取系统中所有角色列表")
    @ApiResponse(responseCode = "200", description = "获取成功")
    @GetMapping("/list")
    public Result<List<Role>> getRoleList() {
        List<Role> list = roleService.list();
        return Result.success(list);
    }
    
    @Operation(summary = "获取角色详情", description = "根据ID获取角色详细信息")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "获取成功"),
        @ApiResponse(responseCode = "404", description = "角色不存在")
    })
    @GetMapping("/{id}")
    public Result<Role> getRoleById(
            @Parameter(description = "角色ID", required = true)
            @PathVariable Long id) {
        Role role = roleService.getById(id);
        if (role == null) {
            return Result.error("角色不存在");
        }
        return Result.success(role);
    }
    
    @Operation(summary = "分配角色权限", description = "为角色分配权限")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "分配成功"),
        @ApiResponse(responseCode = "403", description = "无权限"),
        @ApiResponse(responseCode = "404", description = "角色不存在")
    })
    @PostMapping("/{roleId}/permissions")
    public Result<Void> assignPermissions(
            @Parameter(description = "角色ID", required = true)
            @PathVariable Long roleId,
            @Parameter(description = "权限ID列表", required = true)
            @RequestBody List<Long> permissionIds) {
        roleService.assignPermissionsToRole(roleId, permissionIds);
        return Result.success();
    }
    
    @Operation(summary = "获取角色权限", description = "获取角色拥有的权限ID列表")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "获取成功"),
        @ApiResponse(responseCode = "404", description = "角色不存在")
    })
    @GetMapping("/{roleId}/permissions")
    public Result<List<Long>> getRolePermissions(
            @Parameter(description = "角色ID", required = true)
            @PathVariable Long roleId) {
        List<Long> permissionIds = roleService.getRolePermissionIds(roleId);
        return Result.success(permissionIds);
    }
    
    @Operation(summary = "分配用户角色", description = "为用户分配角色")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "分配成功"),
        @ApiResponse(responseCode = "403", description = "无权限"),
        @ApiResponse(responseCode = "404", description = "用户不存在")
    })
    @PostMapping("/users/{userId}")
    public Result<Void> assignRolesToUser(
            @Parameter(description = "用户ID", required = true)
            @PathVariable Long userId,
            @Parameter(description = "角色ID列表", required = true)
            @RequestBody List<Long> roleIds) {
        roleService.assignRolesToUser(userId, roleIds);
        return Result.success();
    }
} 