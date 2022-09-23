package com.igocst.coco.dto.member;

import lombok.Builder;
import lombok.Getter;

@Getter
public class MemberUpdateResponseDto {
    private String status;

    @Builder
    public MemberUpdateResponseDto(String status) {
        this.status = status;
    }
}
