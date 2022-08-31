package com.igocst.coco.dto.message;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class MessageListReadResponseDto {
    private Long id;
    private String title;
    private String receiver;
    private String sender;
    private Boolean readState;
    private LocalDateTime createDate;
    private String status;

    @Builder
    public MessageListReadResponseDto(Long id, String title, String receiver, String sender, Boolean readState,
                                      LocalDateTime createDate, String status) {
        this.id = id;
        this.title = title;
        this.receiver = receiver;
        this.sender = sender;
        this.readState = readState;
        this.createDate = createDate;
        this.status = status;
    }
}