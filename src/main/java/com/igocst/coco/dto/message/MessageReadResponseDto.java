package com.igocst.coco.dto.message;

import lombok.Builder;
import lombok.Getter;

@Getter
public class MessageReadResponseDto {
    private Long id;
    private String member;
    private String sender;
    private String title;
    private String content;
    private String status;
    private boolean readState;

    @Builder
    public MessageReadResponseDto(Long id, String member, String sender, String title, String content,
                                  String status, boolean readState) {
        this.id = id;
        this.member = member;
        this.sender = sender;
        this.title = title;
        this.content = content;
        this.status = status;
        this.readState = readState;
    }
}