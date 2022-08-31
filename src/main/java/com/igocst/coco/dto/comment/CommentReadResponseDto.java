package com.igocst.coco.dto.comment;

import com.igocst.coco.domain.MemberRole;
import lombok.*;
import java.time.LocalDateTime;

@Getter
public class CommentReadResponseDto {
    private Long id;
    private Long postId;
    private String comments;
    private String nickname;
    private String status;
    private LocalDateTime createDate;
    private LocalDateTime modifyDate;
    // 댓글을 삭제 할 수 있는지 여부 체크
    private boolean enableDelete;
    private boolean enableUpdate;
    // 관리자 여부 체크
    private MemberRole memberRole;
    // 멤버 정보 읽기
    private String profileImageUrl;
    private String githubUrl;
    private String portfolioUrl;
    private String introduction;

    @Builder
    public CommentReadResponseDto(Long id, Long postId, String comments, String nickname, String status,
                                  LocalDateTime createDate, LocalDateTime modifyDate, boolean enableDelete,
                                  boolean enableUpdate, MemberRole memberRole, String profileImageUrl, String githubUrl,
                                  String portfolioUrl, String introduction) {
        this.id = id;
        this.postId = postId;
        this.comments = comments;
        this.nickname = nickname;
        this.status = status;
        this.createDate = createDate;
        this.modifyDate = modifyDate;
        this.enableDelete = enableDelete;
        this.enableUpdate = enableUpdate;
        this.memberRole = memberRole;
        this.profileImageUrl = profileImageUrl;
        this.githubUrl = githubUrl;
        this.portfolioUrl = portfolioUrl;
        this.introduction = introduction;
    }
}