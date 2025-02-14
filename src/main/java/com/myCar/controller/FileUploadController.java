package com.myCar.controller;

import com.myCar.service.FileUploadService;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/files")
public class FileUploadController {

    private final FileUploadService fileUploadService;

    @Autowired
    public FileUploadController(FileUploadService fileUploadService) {
        this.fileUploadService = fileUploadService;
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            String uniqueFilename = fileUploadService.storeFile(file);
            return ResponseEntity.status(HttpStatus.OK).body("File uploaded successfully: " + uniqueFilename);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload file: " + file.getOriginalFilename());
        }
    }

    @GetMapping("/download/{filename:.+}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String filename) {
        Resource file = fileUploadService.loadFileAsResource(filename);
        if (file == null) {
            return ResponseEntity.notFound().build();
        }

        // Determine the content type dynamically based on the file's MIME type
        MediaType contentType = MediaType.IMAGE_JPEG; // default to JPEG
        try {
            contentType = MediaType.parseMediaType(file.getURL().openConnection().getContentType());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
                .contentType(contentType)
                .body(file);
    }


    @DeleteMapping("/deleteAll")
    public ResponseEntity<String> deleteAllFiles() {
        fileUploadService.deleteAllFiles();
        return ResponseEntity.ok("All files have been deleted");
    }

    @GetMapping("/files")
    public ResponseEntity<Object> loadAllFiles() {
        return ResponseEntity.ok().body(fileUploadService.loadAllFiles());
    }
}
