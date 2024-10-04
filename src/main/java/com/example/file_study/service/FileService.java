package com.example.file_study.service;

import com.example.file_study.dto.response.FileResponseDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileService {
    void uploadFile(MultipartFile file, String comment) throws IOException;

    default void uploadFiles(MultipartFile[] files, String[] comment) throws IOException {
        for (int i = 0; i < files.length; i++) {
            uploadFile(files[i], comment[i]);
        }
    }

    FileResponseDto downloadFile(Long fileId) throws IOException;
    void deleteFile(Long fileId) throws IOException;
}
