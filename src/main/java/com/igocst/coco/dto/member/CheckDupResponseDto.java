package com.igocst.coco.dto.member;

import lombok.Builder;
import lombok.Getter;

@Getter
public class CheckDupResponseDto {
    private String status;
    private boolean isDup;

    @Builder
    public CheckDupResponseDto(String status, boolean isDup) {
        this.status = status;
        this.isDup = isDup;
    }
}
