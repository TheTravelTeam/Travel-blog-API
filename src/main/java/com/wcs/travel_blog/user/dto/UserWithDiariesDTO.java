package com.wcs.travel_blog.user.dto;

import com.wcs.travel_blog.travel_diary.dto.SummaryTravelDiaryDTO;
import com.wcs.travel_blog.travel_diary.dto.TravelDiaryDTO;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Getter
@Setter
public class UserWithDiariesDTO {
    private Long id;
    private String username;
    private String avatar;
    private String email;
    private String biography;
    private String status;
    private Set<String> roles;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<SummaryTravelDiaryDTO> travelDiaries;
}
