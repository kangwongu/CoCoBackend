package com.igocst.coco.dto.bookmark;

import lombok.Builder;
import lombok.Getter;

@Getter
public class BookmarkSaveResponseDto {
    private String status;

    @Builder
    public BookmarkSaveResponseDto(String status) {
        this.status = status;
    }
}
