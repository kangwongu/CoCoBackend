package com.igocst.coco.dto.message;

import lombok.Builder;
import lombok.Getter;

@Getter
public class MessageCreateResponseDto {
    private String status;

    @Builder
    public MessageCreateResponseDto(String status) {
        this.status = status;
    }
}
