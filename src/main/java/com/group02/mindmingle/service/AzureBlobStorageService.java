package com.group02.mindmingle.service;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.io.InputStream;
import java.io.IOException;

@Service
public class AzureBlobStorageService {

    private static final Logger logger = LoggerFactory.getLogger(AzureBlobStorageService.class);

    private final BlobServiceClient blobServiceClient;
    private final String containerName;
    private BlobContainerClient containerClient; // 可以缓存 ContainerClient

    @Autowired
    public AzureBlobStorageService(BlobServiceClient blobServiceClient,
                                   @Value("${azure.storage.container-name}") String containerName) {
        this.blobServiceClient = blobServiceClient;
        this.containerName = containerName;
        // 初始化时获取 ContainerClient，并确保容器存在
        initializeContainerClient();
    }

    private void initializeContainerClient() {
        try {
            containerClient = blobServiceClient.getBlobContainerClient(containerName);
            if (!containerClient.exists()) {
                logger.info("Container '{}' does not exist. Creating...", containerName);
                containerClient.create();
                logger.info("Container '{}' created successfully.", containerName);
                // 注意: 如果容器设置为 Private 访问，新创建后可能需要设置权限才能公开访问
                // 如果需要公开访问且容器是 Private, 可能需要在这里设置公共访问级别
                // 但通常我们在创建容器时就设置好了访问级别 (Blob)
            } else {
                logger.info("Container '{}' already exists.", containerName);
            }
        } catch (Exception e) {
            logger.error("Error initializing container client for container '{}': {}", containerName, e.getMessage(), e);
            // 根据需要处理异常，例如抛出自定义异常
            throw new RuntimeException("Could not initialize container client", e);
        }
    }

    /**
     * 上传文件到 Azure Blob Storage
     *
     * @param blobName    文件在 Blob 存储中的名称 (可以包含路径，例如 "games/mygame/index.html")
     * @param inputStream 文件内容的输入流
     * @param length      文件内容的长度 (byte)
     * @return 上传后文件的公共访问 URL
     * @throws IOException 如果发生 IO 错误
     * @throws RuntimeException 如果上传失败
     */
    public String uploadFile(String blobName, InputStream inputStream, long length) throws IOException {
        if (containerClient == null) {
            // 尝试再次初始化，或直接抛出错误
            initializeContainerClient();
            if(containerClient == null) {
                throw new RuntimeException("Container client is not available.");
            }
        }
        try {
            BlobClient blobClient = containerClient.getBlobClient(blobName);
            logger.info("Uploading file to Azure Blob Storage. Container: '{}', Blob: '{}'", containerName, blobName);

            // 上传文件，true 表示如果已存在则覆盖
            blobClient.upload(inputStream, length, true);

            String url = blobClient.getBlobUrl();
            logger.info("File uploaded successfully. URL: {}", url);
            return url;
        } catch (Exception e) {
            logger.error("Error uploading file '{}' to container '{}': {}", blobName, containerName, e.getMessage(), e);
            throw new RuntimeException("Error uploading file", e);
        }
    }

    // 你可以根据需要添加其他方法，例如：
    // public InputStream downloadFile(String blobName) { ... }
    // public void deleteFile(String blobName) { ... }
    // public List<String> listFiles() { ... }
}
