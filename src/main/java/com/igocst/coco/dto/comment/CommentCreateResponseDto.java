package com.igocst.coco.dto.comment;

import lombok.Builder;
import lombok.Getter;

@Getter
public class CommentCreateResponseDto {
    private String status;

    @Builder
    public CommentCreateResponseDto(String status) {
        this.status = status;
    }
}