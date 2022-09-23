package com.igocst.coco.dto.post;

import com.igocst.coco.domain.MeetingType;
import lombok.Getter;

@Getter
public class PostUpdateRequestDto {
    private String title;
    private boolean recruitmentState;
    private String content;
    private MeetingType meetingType;
    private String contact;
    private String period;
    private int hits;
}
