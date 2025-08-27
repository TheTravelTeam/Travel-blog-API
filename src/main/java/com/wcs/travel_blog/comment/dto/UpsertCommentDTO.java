package com.wcs.travel_blog.comment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpsertCommentDTO {

    @NotBlank
    private String content;

    @NotNull
    private Long stepId;
}
