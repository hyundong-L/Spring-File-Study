package com.example.file_study.service.serviceImpl;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Slf4j
public class S3FileServiceImpl implements FileService {
    private final AmazonS3 s3Client;
    private final FileRepository fileRepository;
    private final FileUtils fileUtils;

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucket;
    @Value("${spring.cloud.aws.s3.base-url-format}")
    private String S3_BASE_URL_FORMAT;


    @Override
    public void uploadFile(MultipartFile file, String comment) throws IOException {
        if (!fileUtils.validateFile(file)) {
            log.info("Image validation failed for file: {}", file.getOriginalFilename());
            throw new FileStorageException("File validation failed: " + file.getOriginalFilename());
        }

        String fileName = fileUtils.generateFileName(file.getOriginalFilename());
        String imageUrl = uploadToS3(file, fileName);

        FileEntity fileEntity = FileEntity.builder()
                .filePath(imageUrl)
                .fileComment(comment)
                .build();

        fileRepository.save(fileEntity);
    }

    @Override
    public FileResponseDto downloadFile(Long fileId) throws IOException {
        byte[] fileContent;

        FileEntity findFile = fileRepository.findById(fileId).orElseThrow(() -> new NoSuchElementException("File Not Found"));
        S3Object s3Object = s3Client.getObject(bucket, findFile.getFilePath());
        S3ObjectInputStream inputStream = s3Object.getObjectContent();

        try {
            fileContent = inputStream.readAllBytes();
            s3Object.close();
        } catch (IOException e) {
            throw new FileStorageException("Error occurred while downloading file", e);
        }

        return FileResponseDto.builder()
                .fileId(findFile.getFileId())
                .file(fileContent)
                .comment(findFile.getFileComment())
                .build();
    }


    private String uploadToS3(MultipartFile file, String fileName) throws IOException {
        String imageUrl = String.format(S3_BASE_URL_FORMAT, bucket) + fileName;
        s3Client.putObject(new PutObjectRequest(bucket, fileName, file.getInputStream(), getObjectMetadata(file)));
        return imageUrl;
    }

    //S3에 업로드 시 객체 필수 속성을 정의하기 위해 사용
    private ObjectMetadata getObjectMetadata(MultipartFile file) {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(file.getContentType());
        metadata.setContentLength(file.getSize());

        return metadata;
    }

    //파일 이름만 분리 - 삭제 시 사용
    private String extractFileNameFromUrl(String imageUrl) {
        return imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
    }
}
