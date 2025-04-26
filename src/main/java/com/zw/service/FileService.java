package com.zw.service;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * 文件服务接口
 */
public interface FileService {
    
    /**
     * 上传文件到OSS
     * @param file 文件
     * @param directory 目录
     * @return 文件访问URL
     */
    String uploadFile(MultipartFile file, String directory);
    
    /**
     * 上传用户头像
     * @param file 头像文件
     * @param userId 用户ID
     * @return 头像URL
     */
    String uploadAvatar(MultipartFile file, Long userId);
    
    /**
     * 删除OSS文件
     * @param fileUrl 文件URL
     * @return 是否删除成功
     */
    boolean deleteFile(String fileUrl);
    
    /**
     * 上传文档文件到本地存储
     * @param file 文件（Excel或Word）
     * @param directory 目录
     * @return 文件存储路径
     */
    String uploadDocument(MultipartFile file, String directory);
    
    /**
     * 导出Excel文件
     * @param response HTTP响应对象
     * @param fileName 导出的文件名
     * @param sheetName 工作表名称
     * @param headers 表头数据
     * @param dataList 表格数据
     */
    void exportExcel(HttpServletResponse response, String fileName, String sheetName, 
                    String[] headers, List<Map<String, Object>> dataList);
    
    /**
     * 导出Word文件
     * @param response HTTP响应对象
     * @param fileName 导出的文件名
     * @param title 文档标题
     * @param content 文档内容
     */
    void exportWord(HttpServletResponse response, String fileName, String title, String content);
} 