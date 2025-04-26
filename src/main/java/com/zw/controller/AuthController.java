package com.zw.controller;

import com.zw.common.Result;
import com.zw.dto.LoginDTO;
import com.zw.dto.RegisterDTO;
import com.zw.entity.User;
import com.zw.service.UserService;
import com.zw.utils.JwtUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Tag(name = "认证管理", description = "包含登录、注册等认证相关接口")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtUtils jwtUtils;

    @Operation(summary = "用户登录", description = "用户登录接口，返回JWT令牌和用户信息")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "登录成功", 
            content = @Content(schema = @Schema(implementation = Map.class))),
        @ApiResponse(responseCode = "401", description = "用户名或密码错误"),
        @ApiResponse(responseCode = "403", description = "账号被禁用")
    })
    @PostMapping("/login")
    public Result<Map<String, Object>> login(
            @Parameter(description = "登录信息，包含用户名和密码", required = true)
            @Valid @RequestBody LoginDTO loginDTO) {
        // 进行身份验证
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginDTO.getUsername(),
                        loginDTO.getPassword()
                )
        );

        // 设置当前登录用户
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 生成JWT令牌
        String token = jwtUtils.generateToken((UserDetails) authentication.getPrincipal());

        // 查询用户信息
        User user = userService.getByUsername(loginDTO.getUsername());

        Map<String, Object> map = new HashMap<>();
        map.put("token", token);
        map.put("user", user);

        return Result.success(map);
    }

    @Operation(summary = "用户注册", description = "新用户注册接口")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "注册成功"),
        @ApiResponse(responseCode = "400", description = "注册信息不合法"),
        @ApiResponse(responseCode = "409", description = "用户名已存在")
    })
    @PostMapping("/register")
    public Result<Void> register(
            @Parameter(description = "注册信息，包含用户名、密码等", required = true)
            @Valid @RequestBody RegisterDTO registerDTO) {
        User user = new User();
        BeanUtils.copyProperties(registerDTO, user);
        userService.register(user);
        return Result.success();
    }
} 