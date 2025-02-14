package com.myCar.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringJUnitConfig
@SpringBootTest
public class FileUploadServiceTest {

    @Mock
    private MultipartFile mockFile;

    @Mock
    private FileUploadService fileUploadService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testStoreFile() {
        // Mock file data
        String originalFilename = "test.txt";
        byte[] fileContent = "Hello, World!".getBytes();
        when(mockFile.getOriginalFilename()).thenReturn(originalFilename);
        try {
            when(mockFile.getInputStream()).thenReturn(new ByteArrayResource(fileContent).getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Mock service behavior
        String uniqueFilename = "test-123.txt";
        doReturn(uniqueFilename).when(fileUploadService).storeFile(any(MultipartFile.class));

        // Test service method
        String resultFilename = fileUploadService.storeFile(mockFile);

        // Verify results
        assertNotNull(resultFilename);
        assertEquals(uniqueFilename, resultFilename);
        verify(fileUploadService, times(1)).storeFile(any(MultipartFile.class));
    }

    @Test
    public void testLoadFileAsResource() {
        String filename = "test.txt";
        byte[] fileContent = "Hello, World!".getBytes();

        // Mock service behavior
        Resource resource = new ByteArrayResource(fileContent);
        when(fileUploadService.loadFileAsResource(filename)).thenReturn(resource);

        // Test service method
        Resource loadedResource = fileUploadService.loadFileAsResource(filename);

        // Verify results
        assertNotNull(loadedResource);
        assertEquals(resource, loadedResource);
        verify(fileUploadService, times(1)).loadFileAsResource(filename);
    }

    @Test
    public void testDeleteAllFiles() throws IOException {
        // Mock service behavior
        doNothing().when(fileUploadService).deleteAllFiles();

        // Test service method
        fileUploadService.deleteAllFiles();

        // Verify results
        verify(fileUploadService, times(1)).deleteAllFiles();
    }

    @Test
    public void testLoadAllFiles() throws IOException {
        // Mock service behavior
        Path tempDir = Files.createTempDirectory("test");
        Stream<Path> fileStream = Files.walk(tempDir);
        when(fileUploadService.loadAllFiles()).thenReturn(fileStream);

        // Test service method
        Stream<Path> loadedFiles = fileUploadService.loadAllFiles();

        // Verify results
        assertNotNull(loadedFiles);
        assertEquals(fileStream, loadedFiles);
        verify(fileUploadService, times(1)).loadAllFiles();
    }
}
