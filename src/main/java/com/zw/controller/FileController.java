package com.zw.controller;

import com.zw.common.Result;
import com.zw.entity.User;
import com.zw.service.FileService;
import com.zw.service.UserService;
import com.zw.utils.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 文件上传控制器
 */
@Tag(name = "文件上传", description = "文件上传相关接口")
@RestController
@RequestMapping("/api/file")
@RequiredArgsConstructor
@Slf4j
public class FileController {

    private final FileService fileService;
    private final UserService userService;

    @Operation(summary = "上传头像", description = "上传用户头像并更新用户信息")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "上传成功"),
        @ApiResponse(responseCode = "400", description = "请求参数错误"),
        @ApiResponse(responseCode = "401", description = "未登录")
    })
    @PostMapping("/avatar")
    public Result<Map<String, String>> uploadAvatar(
            @Parameter(description = "头像文件", required = true)
            @RequestParam("file") MultipartFile file) {
        // 获取当前登录用户
        String username = SecurityUtils.getCurrentUsername();
        if (username == null) {
            return Result.error("未登录");
        }
        
        User user = userService.getByUsername(username);
        // 上传头像
        String avatarUrl = fileService.uploadAvatar(file, user.getId());
        
        // 更新用户头像URL
        if (user.getAvatar() != null && !user.getAvatar().isEmpty()) {
            // 如果用户已有头像，尝试删除旧头像
            fileService.deleteFile(user.getAvatar());
        }
        
        user.setAvatar(avatarUrl);
        userService.updateUserInfo(user);
        
        // 返回头像URL
        Map<String, String> result = new HashMap<>();
        result.put("avatarUrl", avatarUrl);
        return Result.success(result);
    }
    
    @Operation(summary = "上传文件", description = "上传通用文件")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "上传成功"),
        @ApiResponse(responseCode = "400", description = "请求参数错误"),
        @ApiResponse(responseCode = "401", description = "未登录"),
        @ApiResponse(responseCode = "403", description = "无权限")
    })
    @PostMapping("/upload")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Map<String, String>> uploadFile(
            @Parameter(description = "文件", required = true)
            @RequestParam("file") MultipartFile file,
            @Parameter(description = "目录", required = true)
            @RequestParam("directory") String directory) {
        // 上传文件
        String fileUrl = fileService.uploadFile(file, directory);
        
        // 返回文件URL
        Map<String, String> result = new HashMap<>();
        result.put("fileUrl", fileUrl);
        return Result.success(result);
    }
    
    @Operation(summary = "上传文档文件", description = "上传Excel或Word文档（用于题目上传或试题导入）")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "上传成功"),
        @ApiResponse(responseCode = "400", description = "请求参数错误"),
        @ApiResponse(responseCode = "401", description = "未登录"),
        @ApiResponse(responseCode = "403", description = "无权限")
    })
    @PostMapping("/document")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public Result<Map<String, String>> uploadDocument(
            @Parameter(description = "文档文件(Excel或Word)", required = true)
            @RequestParam("file") MultipartFile file,
            @Parameter(description = "保存目录", required = false, example = "question")
            @RequestParam(value = "directory", defaultValue = "question") String directory) {
        // 上传文档文件到本地存储
        String filePath = fileService.uploadDocument(file, directory);
        
        // 返回文件路径
        Map<String, String> result = new HashMap<>();
        result.put("filePath", filePath);
        result.put("fileName", file.getOriginalFilename());
        result.put("fullPath", "D:" + File.separator + "file" + File.separator + filePath);
        return Result.success(result);
    }
    
    @Operation(summary = "导出Excel文件", description = "导出Excel格式的数据")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "导出成功"),
        @ApiResponse(responseCode = "400", description = "请求参数错误"),
        @ApiResponse(responseCode = "401", description = "未登录"),
        @ApiResponse(responseCode = "403", description = "无权限")
    })
    @GetMapping("/excel/export")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public void exportExcel(HttpServletResponse response,
                           @Parameter(description = "文件名", required = true)
                           @RequestParam("fileName") String fileName) {
        // 示例数据
        String[] headers = {"姓名", "年龄", "性别", "学号", "成绩"};
        List<Map<String, Object>> dataList = new ArrayList<>();
        
        // 示例数据1
        Map<String, Object> data1 = new HashMap<>();
        data1.put("姓名", "张三");
        data1.put("年龄", 20);
        data1.put("性别", "男");
        data1.put("学号", "2021001");
        data1.put("成绩", 85);
        dataList.add(data1);
        
        // 示例数据2
        Map<String, Object> data2 = new HashMap<>();
        data2.put("姓名", "李四");
        data2.put("年龄", 21);
        data2.put("性别", "女");
        data2.put("学号", "2021002");
        data2.put("成绩", 92);
        dataList.add(data2);
        
        // 示例数据3
        Map<String, Object> data3 = new HashMap<>();
        data3.put("姓名", "王五");
        data3.put("年龄", 22);
        data3.put("性别", "男");
        data3.put("学号", "2021003");
        data3.put("成绩", 78);
        dataList.add(data3);
        
        // 执行导出
        fileService.exportExcel(response, fileName, "学生信息", headers, dataList);
    }
    
    @Operation(summary = "导出Word文件", description = "导出Word格式的文档")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "导出成功"),
        @ApiResponse(responseCode = "400", description = "请求参数错误"),
        @ApiResponse(responseCode = "401", description = "未登录"),
        @ApiResponse(responseCode = "403", description = "无权限")
    })
    @GetMapping("/word/export")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public void exportWord(HttpServletResponse response,
                         @Parameter(description = "文件名", required = true)
                         @RequestParam("fileName") String fileName,
                         @Parameter(description = "文档标题", required = true)
                         @RequestParam("title") String title,
                         @Parameter(description = "文档内容", required = true)
                         @RequestParam("content") String content) {
        fileService.exportWord(response, fileName, title, content);
    }
    
    @Operation(summary = "删除文件", description = "根据URL删除文件")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "删除成功"),
        @ApiResponse(responseCode = "400", description = "请求参数错误"),
        @ApiResponse(responseCode = "401", description = "未登录"),
        @ApiResponse(responseCode = "403", description = "无权限")
    })
    @DeleteMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Boolean> deleteFile(
            @Parameter(description = "文件URL", required = true)
            @RequestParam("fileUrl") String fileUrl) {
        boolean result = fileService.deleteFile(fileUrl);
        return Result.success(result);
    }
} 