package com.igocst.coco.dto.message;

import lombok.Builder;
import lombok.Getter;

@Getter
public class MessageDeleteResponseDto {
    private String status;

    @Builder
    public MessageDeleteResponseDto(String status) {
        this.status = status;
    }
}

