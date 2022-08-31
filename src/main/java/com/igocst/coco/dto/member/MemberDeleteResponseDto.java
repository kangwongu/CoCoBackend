package com.igocst.coco.dto.member;

import lombok.Builder;
import lombok.Getter;
@Getter
public class MemberDeleteResponseDto {
    private String status;

    @Builder
    public MemberDeleteResponseDto(String status) {
        this.status = status;
    }
}
