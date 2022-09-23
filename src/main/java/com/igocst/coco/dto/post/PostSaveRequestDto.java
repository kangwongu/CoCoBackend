package com.igocst.coco.dto.post;

import com.igocst.coco.domain.MeetingType;
import lombok.Getter;

@Getter
public class PostSaveRequestDto {
    private String title;
    private String content;
    private MeetingType meetingType;
    private String contact;
    private String period;
}
