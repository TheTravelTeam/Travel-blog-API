package com.wcs.travel_blog.comment.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ModerateCommentDTO {

    @NotNull
    private String status;
}
