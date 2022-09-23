package com.igocst.coco.dto.post;

import lombok.Builder;
import lombok.Getter;

@Getter
public class PostDeleteResponseDto {
    private String status;

    @Builder
    public PostDeleteResponseDto(String status) {
        this.status = status;
    }
}
