package com.myCar.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import java.util.stream.Stream;

@Service
public class FileUploadService {

    @Value("${flask.api.url}")
    private String flaskApiUrl;

    private Path uploadPath;

    @Value("${upload.path}")
    private String storagePath;

    private final RestTemplate restTemplate;

    @Autowired
    public FileUploadService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @PostConstruct
    public void init() {
        uploadPath = Paths.get(storagePath);
        try {
            Files.createDirectories(uploadPath);
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize storage location", e);
        }
    }

    public String storeFile(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        String uniqueFilename = generateUniqueFilename(originalFilename);
        Path filePath = uploadPath.resolve(uniqueFilename);
        try {
            Files.copy(file.getInputStream(), filePath);
            processImageWithFlask(filePath.toString());
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file " + originalFilename, e);
        }
        return uniqueFilename;
    }

    private void processImageWithFlask(String imagePath) {
        try {
            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
            body.add("file_path", imagePath);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(body, headers);

            ResponseEntity<String> responseEntity = restTemplate.postForEntity(flaskApiUrl, requestEntity, String.class);
            if (responseEntity.getStatusCode() == HttpStatus.OK) {
                System.out.println("Flask API Response: " + responseEntity.getBody());
            } else {
                System.out.println("No license plate detected in image: " + imagePath);
            }
        } catch (Exception e) {
            System.out.println("Warning: Could not process image with Flask API: " + imagePath);
            System.out.println("Reason: " + e.getMessage());
        }
    }

    public Resource loadFileAsResource(String filename) {
        try {
            Path file = uploadPath.resolve(filename).normalize();
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists()) {
                return resource;
            } else {
                throw new RuntimeException("File not found " + filename);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to load file " + filename, e);
        }
    }

    public void deleteAllFiles() {
        FileSystemUtils.deleteRecursively(uploadPath.toFile());
    }

    public Stream<Path> loadAllFiles() {
        try {
            return Files.walk(uploadPath, 1)
                    .filter(path -> !path.equals(uploadPath))
                    .map(uploadPath::relativize);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read stored files", e);
        }
    }

    private String generateUniqueFilename(String originalFilename) {
        String extension = "";
        int dotIndex = originalFilename.lastIndexOf('.');
        if (dotIndex > 0) {
            extension = originalFilename.substring(dotIndex);
        }
        return UUID.randomUUID().toString() + extension;
    }
}
