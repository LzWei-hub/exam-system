package com.zw.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zw.common.Result;
import com.zw.dto.PasswordDTO;
import com.zw.dto.UserQueryDTO;
import com.zw.dto.UserUpdateDTO;
import com.zw.entity.User;
import com.zw.service.UserService;
import com.zw.utils.SecurityUtils;
import com.zw.vo.UserInfoVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "用户管理", description = "用户信息管理相关接口")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "获取当前用户信息", description = "获取当前登录用户的详细信息")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "获取成功"),
        @ApiResponse(responseCode = "401", description = "未登录")
    })
    @GetMapping("/info")
    public Result<UserInfoVO> getUserInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        UserInfoVO userInfo = userService.getUserInfo(username);
        return Result.success(userInfo);
    }
    
    @Operation(summary = "更新用户信息", description = "更新当前登录用户的个人信息")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "更新成功"),
        @ApiResponse(responseCode = "400", description = "请求参数错误"),
        @ApiResponse(responseCode = "401", description = "未登录")
    })
    @PutMapping("/profile")
    public Result<UserInfoVO> updateUserInfo(
            @Parameter(description = "用户信息更新参数", required = true)
            @Valid @RequestBody UserUpdateDTO userUpdateDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        UserInfoVO userInfo = userService.updateUserInfo(username, userUpdateDTO);
        return Result.success(userInfo);
    }

    /**
     * 注意：用户头像上传请使用 /api/file/avatar 接口
     * @see com.zw.controller.FileController#uploadAvatar(org.springframework.web.multipart.MultipartFile)
     */
    @Operation(summary = "获取当前用户", description = "获取当前登录用户的基本信息")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "获取成功"),
        @ApiResponse(responseCode = "401", description = "未登录")
    })
    @GetMapping("/current")
    public Result<User> getCurrentUser() {
        String username = SecurityUtils.getCurrentUsername();
        if (username == null) {
            return Result.error("未登录");
        }
        User user = userService.getByUsername(username);
        user.setPassword(null); // 不返回密码
        return Result.success(user);
    }

    @Operation(summary = "根据ID获取用户", description = "管理员根据用户ID获取用户信息")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "获取成功"),
        @ApiResponse(responseCode = "403", description = "无权限"),
        @ApiResponse(responseCode = "404", description = "用户不存在")
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<User> getUserById(
            @Parameter(description = "用户ID", required = true)
            @PathVariable Long id) {
        User user = userService.getById(id);
        if (user == null) {
            return Result.error("用户不存在");
        }
        user.setPassword(null); // 不返回密码
        return Result.success(user);
    }

    @Operation(summary = "获取用户列表", description = "管理员获取用户分页列表")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "获取成功"),
        @ApiResponse(responseCode = "403", description = "无权限")
    })
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Result<IPage<User>> getUserList(
            @Parameter(description = "查询参数")
            UserQueryDTO queryDTO) {
        IPage<User> page = userService.page(
            new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(
                queryDTO.getPageNum(), 
                queryDTO.getPageSize()
            )
        );
        // 移除密码信息
        page.getRecords().forEach(user -> user.setPassword(null));
        return Result.success(page);
    }

    @Operation(summary = "更新用户状态", description = "管理员更新用户账号状态")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "更新成功"),
        @ApiResponse(responseCode = "400", description = "状态值不正确"),
        @ApiResponse(responseCode = "403", description = "无权限")
    })
    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> updateUserStatus(
            @Parameter(description = "用户ID", required = true)
            @PathVariable Long id, 
            @Parameter(description = "状态值：0-禁用，1-启用", required = true)
            @RequestParam Integer status) {
        if (status != 0 && status != 1) {
            return Result.error("状态值不正确");
        }
        userService.updateStatus(id, status);
        return Result.success();
    }

    @Operation(summary = "修改密码", description = "用户修改自己的登录密码")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "修改成功"),
        @ApiResponse(responseCode = "400", description = "请求参数错误"),
        @ApiResponse(responseCode = "401", description = "未登录")
    })
    @PutMapping("/password")
    public Result<Void> updatePassword(
            @Parameter(description = "密码修改参数", required = true)
            @Valid @RequestBody PasswordDTO passwordDTO) {
        String username = SecurityUtils.getCurrentUsername();
        if (username == null) {
            return Result.error("未登录");
        }
        User user = userService.getByUsername(username);
        userService.updatePassword(user.getId(), passwordDTO.getOldPassword(), passwordDTO.getNewPassword());
        return Result.success();
    }

    @Operation(summary = "重置密码", description = "管理员重置用户密码")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "重置成功"),
        @ApiResponse(responseCode = "403", description = "无权限")
    })
    @PutMapping("/{id}/resetPassword")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> resetPassword(
            @Parameter(description = "用户ID", required = true)
            @PathVariable Long id) {
        userService.resetPassword(id);
        return Result.success();
    }

    @Operation(summary = "更新用户信息", description = "更新用户基本信息")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "更新成功"),
        @ApiResponse(responseCode = "401", description = "未登录")
    })
    @PutMapping("/info")
    public Result<User> updateUserInfo(
            @Parameter(description = "用户信息", required = true)
            @RequestBody User user) {
        String username = SecurityUtils.getCurrentUsername();
        if (username == null) {
            return Result.error("未登录");
        }
        
        User currentUser = userService.getByUsername(username);
        user.setId(currentUser.getId());
        userService.updateUserInfo(user);
        
        User updatedUser = userService.getById(currentUser.getId());
        updatedUser.setPassword(null);
        return Result.success(updatedUser);
    }

    @Operation(summary = "删除用户", description = "管理员删除指定用户")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "删除成功"),
        @ApiResponse(responseCode = "403", description = "无权限")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> deleteUser(
            @Parameter(description = "用户ID", required = true)
            @PathVariable Long id) {
        userService.removeById(id);
        return Result.success();
    }
} 