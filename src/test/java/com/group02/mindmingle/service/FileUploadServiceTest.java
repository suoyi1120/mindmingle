package com.group02.mindmingle.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class FileUploadServiceTest {

    @Mock
    private AzureBlobStorageService azureBlobStorageService;

    @InjectMocks
    private FileUploadService fileUploadService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testUploadFile_Success() throws IOException {
        // Preparing test data
        String content = "test content";
        String fileName = "test.txt";
        String path = "test/path";
        String expectedUrl = "https://test.blob.core.windows.net/test-container/test/path/123.txt";

        MultipartFile file = new MockMultipartFile(
            fileName,
            fileName,
            "text/plain",
            content.getBytes()
        );

        // Simulating dependent behaviors
        when(azureBlobStorageService.uploadFileWithContentType(
            anyString(),
            any(InputStream.class),
            anyLong(),
            anyString()
        )).thenReturn(expectedUrl);

        // Executing the test
        String result = fileUploadService.uploadFile(file, path);

        // Verify the results
        assertNotNull(result);
        assertEquals(expectedUrl, result);

        // Verify method call
        verify(azureBlobStorageService).uploadFileWithContentType(
            anyString(),
            any(InputStream.class),
            anyLong(),
            anyString()
        );
    }

    @Test
    void testUploadFile_EmptyFile() throws IOException {
        // Preparing test data
        MultipartFile emptyFile = new MockMultipartFile(
            "empty.txt",
            "empty.txt",
            "text/plain",
            new byte[0]
        );

        // Execute the test and verify the exception
        assertThrows(IllegalArgumentException.class, () -> {
            fileUploadService.uploadFile(emptyFile, "test/path");
        });

        // Verify method call
        verify(azureBlobStorageService, never()).uploadFileWithContentType(
            anyString(),
            any(InputStream.class),
            anyLong(),
            anyString()
        );
    }

    @Test
    void testDeleteFile_Success() {
        // Preparing test data
        String fileUrl = "https://test.blob.core.windows.net/test-container/test/path/test.txt";

        // Simulating dependent behaviors
        when(azureBlobStorageService.deleteFile(anyString())).thenReturn(true);

        // Executing the test
        boolean result = fileUploadService.deleteFile(fileUrl);

        // Verify the results
        assertTrue(result);

        // Verify method call
        verify(azureBlobStorageService).deleteFile(anyString());
    }

    @Test
    void testDeleteFile_EmptyUrl() {
        // Execute the test and verify the exception
        assertThrows(IllegalArgumentException.class, () -> {
            fileUploadService.deleteFile("");
        });

        // Verify method call
        verify(azureBlobStorageService, never()).deleteFile(anyString());
    }
} 