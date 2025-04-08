package com.group02.mindmingle.controller;

import com.group02.mindmingle.service.AzureBlobStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID; // 用于生成唯一文件名

@RestController
@RequestMapping("/api/files") // 定义基础路径
public class FileUploadController {

    private static final Logger logger = LoggerFactory.getLogger(FileUploadController.class);

    private final AzureBlobStorageService storageService;

    @Autowired
    public FileUploadController(AzureBlobStorageService storageService) {
        this.storageService = storageService;
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file,
            @RequestParam(value = "path", required = false, defaultValue = "uploads") String path) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Please select a file to upload.");
        }

        try {
            // 生成一个唯一的文件名，保留原始扩展名
            String originalFilename = file.getOriginalFilename();
            String fileExtension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String uniqueFileName = path + "/" + UUID.randomUUID().toString() + fileExtension; // 包含路径

            // 调用 Service 上传文件
            String fileUrl = storageService.uploadFile(uniqueFileName, file.getInputStream(), file.getSize());

            // 返回上传后的文件 URL
            return ResponseEntity.ok(fileUrl);

        } catch (IOException e) {
            logger.error("IO Error during file upload: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body("IO Error during upload: " + e.getMessage());
        } catch (RuntimeException e) {
            logger.error("Runtime Error during file upload: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body("Error during upload: " + e.getMessage());
        }
    }

    // 可以添加其他接口，例如上传游戏 (可能需要处理多个文件或 zip)
    @PostMapping("/upload-game")
    public ResponseEntity<String> uploadGameFiles(/* ... 参数 ... */) {
        // ... 实现游戏文件上传逻辑 ...
        // 比如接收一个 zip 包，解压后按 gameIdentifier 存放到对应 "目录"
        // String gameIdentifier = ...;
        // storageService.uploadFile("generated-games/" + gameIdentifier +
        // "/index.html", ...);
        // storageService.uploadFile("generated-games/" + gameIdentifier + "/style.css",
        // ...);
        return ResponseEntity.ok("Game uploaded (logic needs implementation)");
    }
}
