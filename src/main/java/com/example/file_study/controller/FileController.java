package com.example.file_study.controller;

import com.example.file_study.dto.response.FileResponseDto;
import com.example.file_study.service.FileService;
import com.example.file_study.template.ResponseTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/file")
public class FileController {
    private FileService fileService;

    /**
     * @Autowired 필드명 매칭 사용
     * 타입으로 조회 시도 후 동일한 타입의 빈이 2개 이상 존재하면 파라미터, 필드 이름으로 빈 조회
     * <p>
     * localFileServiceImpl -> 로컬 서버 파일 서비스
     * s3FileServiceImpl -> AWS S3 파일 서비스
     */

    public FileController(FileService localFileServiceImpl) {
        this.fileService = localFileServiceImpl;
    }

    @PostMapping("/upload")
    public ResponseTemplate<?> uploadFile(
            @RequestPart(name = "file") MultipartFile file,
            @RequestPart(name = "comment") String comment
    ) throws IOException {
        fileService.uploadFile(file, comment);
        return new ResponseTemplate<>(HttpStatus.CREATED, "File Upload Success");
    }

    @PostMapping("/upload/multiple")
    public ResponseTemplate<?> uploadMultipleFiles(
            @RequestParam(name = "files") MultipartFile[] files,
            @RequestParam(name = "comments") String[] comments
    ) throws IOException {
        fileService.uploadFiles(files, comments);
        return new ResponseTemplate<>(HttpStatus.CREATED, "Files Upload Success");
    }

    @GetMapping("/download/{fileId}")
    public ResponseTemplate<FileResponseDto> downloadFile(@PathVariable Long fileId) throws IOException {
        return new ResponseTemplate<>(HttpStatus.OK, "File Download Success", fileService.downloadFile(fileId));
    }

    @DeleteMapping("/delete/{fileId}")
    public ResponseTemplate<?> deleteFile(@PathVariable Long fileId) throws IOException {
        fileService.deleteFile(fileId);
        return new ResponseTemplate<>(HttpStatus.OK, "File Delete Success");
    }
}
