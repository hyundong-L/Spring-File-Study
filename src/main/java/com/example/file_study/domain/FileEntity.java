package com.example.file_study.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@NoArgsConstructor
@Getter
public class FileEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long fileId;

    @Column(name = "file_path")
    private String filePath;

    //파일 설명
    @Column(name = "file_comment")
    private String fileComment;

    @Column(name = "update_date")
    private LocalDate updateDate;

    @Builder
    public FileEntity(String filePath, String fileComment, LocalDate updateDate) {
        this.filePath = filePath;
        this.fileComment = fileComment;
        this.updateDate = LocalDate.now();
    }
}
