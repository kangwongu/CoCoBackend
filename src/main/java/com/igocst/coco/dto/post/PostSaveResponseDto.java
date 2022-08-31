package com.igocst.coco.dto.post;

import lombok.Builder;
import lombok.Getter;

@Getter
public class PostSaveResponseDto {
    private String status;

    @Builder
    public PostSaveResponseDto(String status) {
        this.status = status;
    }
}
