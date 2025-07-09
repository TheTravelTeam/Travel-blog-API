package com.wcs.travel_blog.mapper;

import com.wcs.travel_blog.dto.MediaDto;
import com.wcs.travel_blog.model.Media;

public class MediaMapper {
    public static MediaDto mapToMediaDto(Media media) {

        MediaDto mediaDto = new MediaDto();
        mediaDto.setId(media.getId());
        mediaDto.setFileUrl(media.getFileUrl());
        mediaDto.setMediaType(media.getMediaType().name());
        mediaDto.setStatus(media.getStatus().name());
        mediaDto.setCreatedAt(media.getCreatedAt());
        mediaDto.setUpdatedAt(media.getUpdatedAt());
        return mediaDto;
    }
}
