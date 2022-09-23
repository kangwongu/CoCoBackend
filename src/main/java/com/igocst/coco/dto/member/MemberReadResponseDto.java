package com.igocst.coco.dto.member;

import lombok.Builder;
import lombok.Getter;

@Getter
public class MemberReadResponseDto {
    private String email;
    private String nickname;
    private String githubUrl;
    private String portfolioUrl;
    private String introduction;
    private String status;
    // 프로필 페이지 이미지
    private String profileImageUrl;

    @Builder
    public MemberReadResponseDto(String email, String nickname, String githubUrl, String portfolioUrl,
                                 String introduction, String status, String profileImageUrl) {
        this.email = email;
        this.nickname = nickname;
        this.githubUrl = githubUrl;
        this.portfolioUrl = portfolioUrl;
        this.introduction = introduction;
        this.status = status;
        this.profileImageUrl = profileImageUrl;
    }
}
