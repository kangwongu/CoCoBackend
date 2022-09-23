package com.igocst.coco.dto.comment;

import lombok.*;
@Getter

public class CommentUpdateResponseDto {
    private String status;

    @Builder
    public CommentUpdateResponseDto(String status) {
        this.status = status;
    }
}
