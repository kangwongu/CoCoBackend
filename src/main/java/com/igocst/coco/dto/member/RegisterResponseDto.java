package com.igocst.coco.dto.member;

import lombok.Builder;
import lombok.Getter;

@Getter
public class RegisterResponseDto {
    private String status;

    @Builder
    public RegisterResponseDto(String status) {
        this.status = status;
    }
}
