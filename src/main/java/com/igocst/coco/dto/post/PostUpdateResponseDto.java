package com.igocst.coco.dto.post;

import lombok.Builder;
import lombok.Getter;

@Getter
public class PostUpdateResponseDto {
    private String status;

    @Builder
    public PostUpdateResponseDto(String status) {
        this.status = status;
    }
}
