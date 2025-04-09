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
}
