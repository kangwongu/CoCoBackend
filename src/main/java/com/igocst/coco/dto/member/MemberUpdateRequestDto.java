package com.igocst.coco.dto.member;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class MemberUpdateRequestDto {
    private String nickname;
    private String githubUrl;
    private String portfolioUrl;
    private String introduction;
    //프로필 페이지 이미지
    private MultipartFile file;
}