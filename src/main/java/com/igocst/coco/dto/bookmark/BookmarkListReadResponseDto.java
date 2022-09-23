package com.igocst.coco.dto.bookmark;

import com.igocst.coco.domain.MeetingType;
import lombok.Builder;
import lombok.Getter;

@Getter
public class BookmarkListReadResponseDto {
    private String status;
    private Long id;
    private Long postId;
    private String title;
    private MeetingType meetingType;
    private String period;
    private boolean recruitmentState;
    private int hits;
    private boolean bookmarkState;

    @Builder
    public BookmarkListReadResponseDto(String status, Long id, Long postId, String title, MeetingType meetingType,
                                       String period, boolean recruitmentState, int hits, boolean bookmarkState) {
        this.status = status;
        this.id = id;
        this.postId = postId;
        this.title = title;
        this.meetingType = meetingType;
        this.period = period;
        this.recruitmentState = recruitmentState;
        this.hits = hits;
        this.bookmarkState = bookmarkState;
    }
}
