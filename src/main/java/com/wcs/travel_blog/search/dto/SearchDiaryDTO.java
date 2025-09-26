package com.wcs.travel_blog.search.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchDiaryDTO {

    private Long id;
    private String title;
    private String description;
    private String coverUrl;
}
