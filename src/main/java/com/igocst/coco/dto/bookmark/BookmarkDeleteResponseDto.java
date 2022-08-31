package com.igocst.coco.dto.bookmark;

import lombok.Builder;
import lombok.Getter;
@Getter
public class BookmarkDeleteResponseDto {
    private String status;

    @Builder
    public BookmarkDeleteResponseDto(String status) {
        this.status = status;
    }
}
