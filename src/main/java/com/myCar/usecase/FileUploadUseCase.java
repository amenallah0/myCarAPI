package com.myCar.usecase;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.stream.Stream;

public interface FileUploadUseCase {
    void init();
    public String save(MultipartFile file);
    Resource getFileByName(String fileName);
    void deleteAll();
    Stream<Path> loadAllFiles();
}
