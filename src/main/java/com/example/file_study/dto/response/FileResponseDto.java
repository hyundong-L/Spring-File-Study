package com.example.file_study.dto.response;

import lombok.Builder;

@Builder
public record FileResponseDto (
        Long fileId,
        byte[] file,
        String comment
) {
}
