package com.zw.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zw.common.Result;
import com.zw.dto.UserDTO;
import com.zw.dto.UserQueryDTO;
import com.zw.dto.UserRoleDTO;
import com.zw.dto.UserStatusDTO;
import com.zw.service.UserService;
import com.zw.vo.UserInfoVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "管理员功能", description = "系统管理员相关接口")
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final UserService userService;

    @Operation(summary = "更新用户状态", description = "管理员更新用户账号状态")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "更新成功"),
        @ApiResponse(responseCode = "400", description = "请求参数错误"),
        @ApiResponse(responseCode = "403", description = "无权限"),
        @ApiResponse(responseCode = "404", description = "用户不存在")
    })
    @PutMapping("/users/{id}/status")
    public Result<UserInfoVO> updateUserStatus(
            @Parameter(description = "用户ID", required = true)
            @PathVariable("id") Long id, 
            @Parameter(description = "状态信息", required = true)
            @Valid @RequestBody UserStatusDTO statusDTO) {
        UserInfoVO userInfo = userService.updateUserStatus(id, statusDTO);
        return Result.success(userInfo);
    }
    
    @Operation(summary = "查询用户列表", description = "分页查询用户列表")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "查询成功"),
        @ApiResponse(responseCode = "403", description = "无权限")
    })
    @GetMapping("/users")
    public Result<IPage<UserInfoVO>> getUserList(
            @Parameter(description = "查询参数", required = true)
            UserQueryDTO queryDTO) {
        IPage<UserInfoVO> userList = userService.getUserList(queryDTO);
        return Result.success(userList);
    }
    
    @Operation(summary = "更新用户角色", description = "修改用户的角色信息")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "更新成功"),
        @ApiResponse(responseCode = "400", description = "请求参数错误"),
        @ApiResponse(responseCode = "403", description = "无权限"),
        @ApiResponse(responseCode = "404", description = "用户不存在")
    })
    @PutMapping("/users/{id}/role")
    public Result<UserInfoVO> updateUserRole(
            @Parameter(description = "用户ID", required = true)
            @PathVariable("id") Long id,
            @Parameter(description = "角色信息", required = true)
            @Valid @RequestBody UserRoleDTO roleDTO) {
        UserInfoVO userInfo = userService.updateUserRole(id, roleDTO);
        return Result.success(userInfo);
    }
    
    @Operation(summary = "删除用户", description = "删除指定用户")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "删除成功"),
        @ApiResponse(responseCode = "403", description = "无权限"),
        @ApiResponse(responseCode = "404", description = "用户不存在")
    })
    @DeleteMapping("/users/{id}")
    public Result<Boolean> deleteUser(
            @Parameter(description = "用户ID", required = true)
            @PathVariable("id") Long id) {
        boolean result = userService.deleteUser(id);
        return Result.success(result);
    }
    
    @Operation(summary = "创建用户", description = "创建新用户")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "创建成功"),
        @ApiResponse(responseCode = "400", description = "请求参数错误"),
        @ApiResponse(responseCode = "403", description = "无权限"),
        @ApiResponse(responseCode = "409", description = "用户名已存在")
    })
    @PostMapping("/users")
    public Result<UserInfoVO> createUser(
            @Parameter(description = "用户信息", required = true)
            @Valid @RequestBody UserDTO userDTO) {
        UserInfoVO userInfo = userService.createUser(userDTO);
        return Result.success(userInfo);
    }
} 