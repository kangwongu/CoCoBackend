package com.igocst.coco.dto.comment;

import lombok.Builder;
import lombok.Getter;

@Getter
public class CommentDeleteResponseDto {
    private String status;

    @Builder
    public CommentDeleteResponseDto(String status) {
        this.status = status;
    }
}
