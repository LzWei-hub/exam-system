package com.zw.service.impl;

import com.aliyun.oss.OSS;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyun.oss.model.PutObjectRequest;
import com.zw.config.OssConfig;
import com.zw.exception.ApiException;
import com.zw.service.FileService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.usermodel.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 文件服务实现类
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class FileServiceImpl implements FileService {

    private final OSS ossClient;
    private final OssConfig ossConfig;
    
    @Value("${aliyun.oss.bucketName}")
    private String bucketName;
    
    @Value("${aliyun.oss.domain}")
    private String domain;
    
    @Value("${file.upload.local-path:D:/file}")
    private String localUploadPath;
    
    @Override
    public String uploadFile(MultipartFile file, String directory) {
        String originalFilename = file.getOriginalFilename();
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        String objectName = directory + "/" + UUID.randomUUID().toString().replace("-", "") + suffix;
        
        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(getContentType(suffix));
            metadata.setContentLength(file.getSize());
            
            PutObjectRequest putObjectRequest = new PutObjectRequest(
                    bucketName, 
                    objectName, 
                    file.getInputStream(),
                    metadata);
            
            ossClient.putObject(putObjectRequest);
            return domain + "/" + objectName;
        } catch (IOException e) {
            log.error("Upload file to OSS error", e);
            throw new ApiException("上传文件失败");
        }
    }
    
    @Override
    public String uploadAvatar(MultipartFile file, Long userId) {
        // 检查文件大小，头像不超过2MB
        if (file.getSize() > 2 * 1024 * 1024) {
            throw new ApiException("头像文件大小不能超过2MB");
        }
        
        // 检查文件类型，只允许常见图片格式
        String originalFilename = file.getOriginalFilename();
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        if (!isImageFile(suffix)) {
            throw new ApiException("头像文件类型不支持，请上传jpg、jpeg、png、gif格式的图片");
        }
        
        // 使用avatars目录存储头像文件
        String directory = "avatars";
        // 使用用户ID作为文件名的一部分，便于管理
        String objectName = directory + "/" + userId + "_" + UUID.randomUUID().toString().replace("-", "") + suffix;
        
        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(getContentType(suffix));
            metadata.setContentLength(file.getSize());
            
            PutObjectRequest putObjectRequest = new PutObjectRequest(
                    bucketName, 
                    objectName, 
                    file.getInputStream(),
                    metadata);
            
            ossClient.putObject(putObjectRequest);
            return domain + "/" + objectName;
        } catch (IOException e) {
            log.error("Upload avatar to OSS error", e);
            throw new ApiException("上传头像失败");
        }
    }
    
    @Override
    public boolean deleteFile(String fileUrl) {
        try {
            // 从完整URL中提取objectName
            String objectName = fileUrl.replace(domain + "/", "");
            ossClient.deleteObject(bucketName, objectName);
            return true;
        } catch (Exception e) {
            log.error("Delete file from OSS error", e);
            return false;
        }
    }
    
    @Override
    public String uploadDocument(MultipartFile file, String directory) {
        // 检查文件大小，文档不超过10MB
        if (file.getSize() > 10 * 1024 * 1024) {
            throw new ApiException("文档文件大小不能超过10MB");
        }
        
        // 检查文件类型，只允许Excel和Word文件
        String originalFilename = file.getOriginalFilename();
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        if (!isDocumentFile(suffix)) {
            throw new ApiException("文件类型不支持，请上传Excel(.xls, .xlsx)或Word(.doc, .docx)格式的文档");
        }
        
        try {
            // 确保目录存在
            Path dirPath = Paths.get(localUploadPath, directory);
            if (!Files.exists(dirPath)) {
                Files.createDirectories(dirPath);
            }
            
            // 生成唯一文件名
            String newFilename = UUID.randomUUID().toString().replace("-", "") + suffix;
            Path targetPath = dirPath.resolve(newFilename);
            
            // 保存文件
            file.transferTo(targetPath.toFile());
            
            // 返回文件相对路径
            return directory + File.separator + newFilename;
        } catch (IOException e) {
            log.error("Upload document to local storage error", e);
            throw new ApiException("上传文档失败");
        }
    }
    
    @Override
    public void exportExcel(HttpServletResponse response, String fileName, String sheetName, 
                           String[] headers, List<Map<String, Object>> dataList) {
        try (Workbook workbook = new XSSFWorkbook()) {
            // 创建工作表
            Sheet sheet = workbook.createSheet(sheetName);
            
            // 设置列宽
            for (int i = 0; i < headers.length; i++) {
                sheet.setColumnWidth(i, 20 * 256);
            }
            
            // 创建标题样式
            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            
            // 创建数据样式
            CellStyle dataStyle = workbook.createCellStyle();
            dataStyle.setAlignment(HorizontalAlignment.CENTER);
            dataStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            
            // 填充表头
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }
            
            // 填充数据
            if (dataList != null && !dataList.isEmpty()) {
                for (int i = 0; i < dataList.size(); i++) {
                    Row dataRow = sheet.createRow(i + 1);
                    Map<String, Object> rowData = dataList.get(i);
                    
                    int cellIndex = 0;
                    for (String key : rowData.keySet()) {
                        if (cellIndex < headers.length) {
                            Cell cell = dataRow.createCell(cellIndex++);
                            Object value = rowData.get(key);
                            if (value != null) {
                                cell.setCellValue(value.toString());
                            } else {
                                cell.setCellValue("");
                            }
                            cell.setCellStyle(dataStyle);
                        }
                    }
                }
            }
            
            // 配置响应头
            setExportResponseHeader(response, fileName + ".xlsx");
            
            // 写入响应流
            try (OutputStream os = response.getOutputStream()) {
                workbook.write(os);
                os.flush();
            }
        } catch (IOException e) {
            log.error("Export Excel error", e);
            throw new ApiException("导出Excel文件失败");
        }
    }
    
    @Override
    public void exportWord(HttpServletResponse response, String fileName, String title, String content) {
        try (XWPFDocument document = new XWPFDocument()) {
            // 创建标题
            XWPFParagraph titleParagraph = document.createParagraph();
            titleParagraph.setAlignment(ParagraphAlignment.CENTER);
            
            XWPFRun titleRun = titleParagraph.createRun();
            titleRun.setText(title);
            titleRun.setBold(true);
            titleRun.setFontSize(16);
            titleRun.setFontFamily("宋体");
            
            // 创建段落
            XWPFParagraph contentParagraph = document.createParagraph();
            contentParagraph.setAlignment(ParagraphAlignment.LEFT);
            
            XWPFRun contentRun = contentParagraph.createRun();
            contentRun.setText(content);
            contentRun.setFontSize(12);
            contentRun.setFontFamily("宋体");
            
            // 配置响应头
            setExportResponseHeader(response, fileName + ".docx");
            
            // 写入响应流
            try (OutputStream os = response.getOutputStream()) {
                document.write(os);
                os.flush();
            }
        } catch (IOException e) {
            log.error("Export Word error", e);
            throw new ApiException("导出Word文件失败");
        }
    }
    
    /**
     * 设置文件导出响应头
     */
    private void setExportResponseHeader(HttpServletResponse response, String fileName) throws IOException {
        response.setContentType("application/octet-stream");
        response.setCharacterEncoding("UTF-8");
        String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8);
        response.setHeader("Content-Disposition", "attachment;filename=" + encodedFileName);
        response.setHeader("Access-Control-Expose-Headers", "Content-Disposition");
    }
    
    /**
     * 判断是否为图片文件
     */
    private boolean isImageFile(String suffix) {
        suffix = suffix.toLowerCase();
        return suffix.equals(".jpg") || suffix.equals(".jpeg") || 
               suffix.equals(".png") || suffix.equals(".gif");
    }
    
    /**
     * 判断是否为文档文件(Excel或Word)
     */
    private boolean isDocumentFile(String suffix) {
        suffix = suffix.toLowerCase();
        return suffix.equals(".xls") || suffix.equals(".xlsx") || 
               suffix.equals(".doc") || suffix.equals(".docx");
    }
    
    /**
     * 获取文件ContentType
     */
    private String getContentType(String suffix) {
        suffix = suffix.toLowerCase();
        switch (suffix) {
            case ".jpg":
            case ".jpeg":
                return "image/jpeg";
            case ".png":
                return "image/png";
            case ".gif":
                return "image/gif";
            case ".xls":
                return "application/vnd.ms-excel";
            case ".xlsx":
                return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            case ".doc":
                return "application/msword";
            case ".docx":
                return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            default:
                return "application/octet-stream";
        }
    }
} 