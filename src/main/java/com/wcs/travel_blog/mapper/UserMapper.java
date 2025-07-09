package com.wcs.travel_blog.mapper;

import com.wcs.travel_blog.dto.UserSummaryDto;
import com.wcs.travel_blog.model.User;

public class UserMapper {
    public static UserSummaryDto mapUserToDto(User user) {
        if(user == null) return null;

        UserSummaryDto dto = new UserSummaryDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUserName());
        dto.setAvatar(user.getAvatar());

        return dto;
    }
}
