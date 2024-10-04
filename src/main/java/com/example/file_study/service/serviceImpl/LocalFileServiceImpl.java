package com.example.file_study.service.serviceImpl;

import com.example.file_study.domain.FileEntity;
import com.example.file_study.dto.response.FileResponseDto;
import com.example.file_study.exception.FileStorageException;
import com.example.file_study.repository.FileRepository;
import com.example.file_study.service.FileService;
import com.example.file_study.util.FileUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Slf4j
public class LocalFileServiceImpl implements FileService {
    private final FileUtils fileUtils;
    private final FileRepository fileRepository;

    @Value("${file.upload.path}")
    private String fileSavePath;

    @Override
    public void uploadFile(MultipartFile file, String comment) throws IOException {
        String savePathFileName = storeFile(file);

        FileEntity fileEntity = FileEntity.builder()
                .filePath(savePathFileName)
                .fileComment(comment)
                .build();

        fileRepository.save(fileEntity);
    }

    @Override
    public FileResponseDto downloadFile(Long fileId) throws IOException {
        FileEntity findFile = fileRepository.findById(fileId).orElseThrow(() -> new NoSuchElementException("File Not Found"));

        Path path = Paths.get(findFile.getFilePath());
        String fileName = findFile.getFilePath().substring(findFile.getFilePath().lastIndexOf("-") + 1);

        return FileResponseDto.builder()
                .fileId(findFile.getFileId())
                .file(Files.readAllBytes(path))
                .fileName(fileName)
                .comment(findFile.getFileComment())
                .build();
    }

    @Override
    public void deleteFile(Long fileId) throws IOException {
        FileEntity findFile = fileRepository.findById(fileId).orElseThrow(() -> new NoSuchElementException("File Not Found"));

        Path path = Paths.get(findFile.getFilePath());
        Files.delete(path);
    }

    //파일을 서버에 저장
    private String storeFile(MultipartFile file) throws IOException {
        if (!fileUtils.validateFile(file)) {
            log.info("Image validation failed for file: {}", file.getOriginalFilename());
            throw new FileStorageException("File validation failed: " + file.getOriginalFilename());
        }

        //상위 폴더 접근을 막기 위해
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());

        try {
            //상위 폴더 접근을 막기 위해
            if (fileName.contains("..")) {
                throw new FileStorageException("Invalid file name: " + fileName);
            }

            fileName = fileUtils.generateFileName(fileName);
            //날짜 별로 파일을 저장하게 만들기 위해 날짜 폴더 생성
            String stringTargetPath = fileUtils.makeFolder(fileSavePath) + fileName;
            //OS에 대한 이식성 강화 및 파일 작업 수행의 직관성을 높인다
            Path targetPath = Paths.get(stringTargetPath);

            file.transferTo(targetPath);

            return targetPath.toString();
        } catch (IOException e) {
            throw new FileStorageException("Failed to store file: " + fileName, e);
        }
    }
}
