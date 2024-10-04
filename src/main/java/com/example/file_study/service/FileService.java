package com.example.file_study.service;

import com.example.file_study.dto.response.FileResponseDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileService {
    void uploadFile(MultipartFile file, String comment) throws IOException;
    FileResponseDto downloadFile(Long fileId) throws IOException;
}
