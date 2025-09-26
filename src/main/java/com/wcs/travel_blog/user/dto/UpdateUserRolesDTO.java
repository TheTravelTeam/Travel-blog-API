package com.wcs.travel_blog.user.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateUserRolesDTO {

    @NotNull(message = "La valeur admin est requise")
    private Boolean admin;
}
