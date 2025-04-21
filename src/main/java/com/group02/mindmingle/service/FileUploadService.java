package com.group02.mindmingle.service;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.models.BlobHttpHeaders;
import org.apache.tika.Tika;
import org.apache.tika.mime.MimeTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Service
public class FileUploadService {

    private static final Logger logger = LoggerFactory.getLogger(FileUploadService.class);

    private final AzureBlobStorageService azureBlobStorageService;
    private final Tika tika;

    @Autowired
    public FileUploadService(AzureBlobStorageService azureBlobStorageService) {
        this.azureBlobStorageService = azureBlobStorageService;
        this.tika = new Tika();
    }

    /**
     * 上传文件到Azure Blob Storage
     *
     * @param file 要上传的文件
     * @param path 存储路径
     * @return 上传后文件的公共访问URL
     * @throws IOException 如果发生IO错误
     */
    public String uploadFile(MultipartFile file, String path) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("文件不能为空");
        }

        // 获取文件名和扩展名
        String originalFilename = file.getOriginalFilename();
        String fileExtension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        // 生成唯一文件名
        String uniqueFileName = path + "/" + UUID.randomUUID() + fileExtension;

        // 使用Tika检测文件的真实Content-Type
        String contentType;
        try (InputStream inputStream = file.getInputStream()) {
            // 首先尝试使用Tika检测Content-Type
            contentType = tika.detect(inputStream);
            // 如果检测不到或返回通用类型，尝试使用MultipartFile提供的Content-Type作为备用
            if (contentType == null || contentType.equals(MimeTypes.OCTET_STREAM)) {
                String multipartContentType = file.getContentType();
                if (multipartContentType != null && !multipartContentType.isEmpty()) {
                    contentType = multipartContentType;
                }
            }
        }

        logger.info("文件 {} 的检测到的Content-Type为: {}", originalFilename, contentType);

        // 使用Azure SDK上传文件并指定Content-Type
        return azureBlobStorageService.uploadFileWithContentType(
                uniqueFileName,
                file.getInputStream(),
                file.getSize(),
                contentType);
    }

    /**
     * 从Azure Blob Storage删除文件
     *
     * @param fileUrl 要删除的文件URL或Blob名称
     * @return 删除是否成功
     */
    public boolean deleteFile(String fileUrl) {
        if (fileUrl == null || fileUrl.trim().isEmpty()) {
            throw new IllegalArgumentException("文件URL不能为空");
        }

        // 从URL中提取Blob名称
        String blobName = extractBlobNameFromUrl(fileUrl);
        logger.info("从URL中提取的Blob名称: {}", blobName);

        // 委托给AzureBlobStorageService来执行实际的删除操作
        return azureBlobStorageService.deleteFile(blobName);
    }

    /**
     * 从URL中提取Azure Blob名称
     *
     * @param fileUrl 文件的URL
     * @return Blob名称
     */
    private String extractBlobNameFromUrl(String fileUrl) {
        // 检查是否已经是一个Blob名称（不包含完整URL）
        if (!fileUrl.startsWith("http")) {
            return fileUrl;
        }

        try {
            // 检查是否是Azure存储URL
            if (fileUrl.contains("blob.core.windows.net")) {
                // 提取容器名后的部分作为blobName
                String[] parts = fileUrl.split("\\.net/");
                if (parts.length > 1) {
                    String containerAndBlob = parts[1];
                    // 跳过容器名称和斜杠
                    int containerEndIndex = containerAndBlob.indexOf('/');
                    if (containerEndIndex >= 0) {
                        String blobName = containerAndBlob.substring(containerEndIndex + 1);
                        // 对URL编码的字符进行解码
                        blobName = java.net.URLDecoder.decode(blobName, "UTF-8");
                        return blobName;
                    }
                    // 对URL编码的字符进行解码
                    containerAndBlob = java.net.URLDecoder.decode(containerAndBlob, "UTF-8");
                    return containerAndBlob; // 如果没有斜杠，返回整个字符串
                }
            }

            // 如果不是标准Azure URL格式，则尝试从URL中提取文件名部分
            String[] pathParts = fileUrl.split("/");
            if (pathParts.length > 0) {
                String fileName = pathParts[pathParts.length - 1];
                // 对URL编码的字符进行解码
                fileName = java.net.URLDecoder.decode(fileName, "UTF-8");
                return fileName;
            }

            // 如果无法解析，返回原始URL并解码
            return java.net.URLDecoder.decode(fileUrl, "UTF-8");
        } catch (Exception e) {
            logger.warn("无法从URL中提取Blob名称，使用完整URL: {}, 错误: {}", fileUrl, e.getMessage());
            try {
                // 尝试至少解码URL
                return java.net.URLDecoder.decode(fileUrl, "UTF-8");
            } catch (Exception ex) {
                // 如果解码失败，返回原始URL
                return fileUrl;
            }
        }
    }
}
