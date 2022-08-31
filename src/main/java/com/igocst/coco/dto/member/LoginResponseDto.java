package com.igocst.coco.dto.member;

import lombok.Builder;
import lombok.Getter;

@Getter
public class LoginResponseDto {
    private String status;
    private String token; // 토큰

    @Builder
    public LoginResponseDto(String status, String token) {
        this.status = status;
        this.token = token;
    }
}
