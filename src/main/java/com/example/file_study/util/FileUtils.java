package com.example.file_study.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Slf4j
@Component
public class FileUtils {
    // 파일 저장 시 랜덤 값을 파일 이름에 추가 후 반환
    public String generateFileName(String fileName) {
        return UUID.randomUUID().toString() + "-" + fileName;
    }

    //파일 유효성 검사
    public boolean validateFile(MultipartFile file) {
        return file != null && !file.isEmpty();
    }

    //날짜에 따른 폴더를 생성하여 파일 저장 시 사용
    public String makeFolder(String parentPath) throws IOException {
        String dateForDir = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        //OS에 따라 경로를 나타내는 기호가 다르기 때문
        //ex) Window -> Data\\example.txt, Linux -> Data/example.txt
        //In Java -> "Data" + File.separator + "example.txt"
        String dirPath = dateForDir.replace("/", File.separator);

        File createDir = new File(parentPath, dirPath);

        if (!createDir.exists()) {
            //mkdir() -> 디렉토리의 상위 디렉토리가 없을 때 생성 불가능
            //mkdirs() -> 디렉토리의 상위 디렉토리가 없다면 생성 가능
            if (!createDir.mkdirs()) {
                throw new IOException("Failed to create directory: " + dirPath);
            }
        }

        return parentPath + File.separator + dirPath + File.separator;
    }
}
