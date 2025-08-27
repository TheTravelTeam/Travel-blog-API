package com.wcs.travel_blog.comment.mapper;


import com.wcs.travel_blog.comment.dto.CommentDTO;
import com.wcs.travel_blog.comment.dto.UpsertCommentDTO;
import com.wcs.travel_blog.comment.model.Comment;
import com.wcs.travel_blog.comment.model.CommentStatus;
import com.wcs.travel_blog.step.model.Step;
import com.wcs.travel_blog.user.mapper.UserMapper;
import com.wcs.travel_blog.user.model.User;
import org.springframework.stereotype.Component;

@Component
public class CommentMapper {
    private final UserMapper userMapper;

    public CommentMapper(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    public CommentDTO toDto(Comment comment){
        CommentDTO commentDTO = new CommentDTO();
        commentDTO.setId(comment.getId());
        commentDTO.setContent(comment.getContent());
        commentDTO.setStatus(comment.getStatus());
        commentDTO.setUpdatedAt(comment.getUpdatedAt());
        commentDTO.setCreatedAt(comment.getCreatedAt());
        if(comment.getUser() != null){
            commentDTO.setUser(userMapper.converToDto(comment.getUser()));
        }

        return commentDTO;

    }

    /** Création : content + relations injectées par le service */
    public Comment toEntity(UpsertCommentDTO upsertData, User author, Step step) {
        Comment comment = new Comment();
        comment.setContent(upsertData.getContent());
        comment.setUser(author);
        comment.setStep(step);
        comment.setStatus(CommentStatus.PENDING); // défaut: en attente
        return comment;
    }

    /** Update auteur/admin : on autorise l’édition du contenu uniquement */
    public void applyUpdate(Comment existing, UpsertCommentDTO input) {
        existing.setContent(input.getContent());
    }
}
