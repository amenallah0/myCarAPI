package com.myCar.controller;

import com.myCar.service.FileUploadService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FileUploadController.class)
public class FileUploadControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FileUploadService fileUploadService;

    private MockMultipartFile mockFile;

    @BeforeEach
    public void setup() {
        mockFile = new MockMultipartFile(
                "file",
                "test.txt",
                MediaType.TEXT_PLAIN_VALUE,
                "Hello, World!".getBytes(StandardCharsets.UTF_8)
        );
    }

    @Test
    public void testUploadFile() throws Exception {
        Mockito.when(fileUploadService.storeFile(any())).thenReturn("test.txt");

        mockMvc.perform(multipart("/api/files/upload")
                        .file(mockFile))
                .andExpect(status().isOk())
                .andExpect(content().string("File uploaded successfully: test.txt"));
    }

    @Test
    public void testUploadFileFailure() throws Exception {
        Mockito.doThrow(new RuntimeException("Failed to store file")).when(fileUploadService).storeFile(any());

        mockMvc.perform(multipart("/api/files/upload")
                        .file(mockFile))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Failed to upload file: test.txt"));
    }

    @Test
    public void testDownloadFile() throws Exception {
        byte[] content = "Hello, World!".getBytes(StandardCharsets.UTF_8);
        Resource resource = new ByteArrayResource(content);

        Mockito.when(fileUploadService.loadFileAsResource(anyString())).thenReturn(resource);

        mockMvc.perform(get("/api/files/download/test.txt"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"test.txt\""))
                .andExpect(content().bytes(content));
    }

    @Test
    public void testDeleteAllFiles() throws Exception {
        mockMvc.perform(delete("/api/files/deleteAll"))
                .andExpect(status().isOk())
                .andExpect(content().string("All files have been deleted"));
    }
}
