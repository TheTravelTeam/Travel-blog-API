package com.wcs.travel_blog.mapper;

import com.wcs.travel_blog.dto.CommentDto;
import com.wcs.travel_blog.model.Comment;

public class CommentMapper {
    public static CommentDto mapCommentToDto(Comment comment) {

        CommentDto dto = new CommentDto();
        dto.setId(comment.getId());
        dto.setStatus(comment.getStatus().name());
        dto.setCreatedAt(comment.getCreatedAt());
        dto.setUpdatedAt(comment.getUpdatedAt());
        dto.setUser(UserMapper.mapUserToDto(comment.getUser()));
        return dto;
    }
}
