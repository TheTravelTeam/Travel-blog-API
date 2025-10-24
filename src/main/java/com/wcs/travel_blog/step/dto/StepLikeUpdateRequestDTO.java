package com.wcs.travel_blog.step.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StepLikeUpdateRequestDTO {

    @NotNull(message = "Le champ increment est requis")
    private Boolean increment;
}
