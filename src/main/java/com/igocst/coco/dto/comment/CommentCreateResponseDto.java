package com.igocst.coco.dto.comment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class CommentCreateResponseDto {
    private String status;
}