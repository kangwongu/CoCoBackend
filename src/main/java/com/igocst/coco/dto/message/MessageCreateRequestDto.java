package com.igocst.coco.dto.message;

import lombok.Getter;

@Getter
public class MessageCreateRequestDto {
    private String receiver;
    private String title;
    private String content;
}