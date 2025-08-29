package com.wcs.travel_blog.comment.service;

import com.wcs.travel_blog.comment.dto.CommentDTO;
import com.wcs.travel_blog.comment.dto.ModerateCommentDTO;
import com.wcs.travel_blog.comment.dto.UpsertCommentDTO;
import com.wcs.travel_blog.comment.mapper.CommentMapper;
import com.wcs.travel_blog.comment.model.Comment;
import com.wcs.travel_blog.comment.model.CommentStatus;
import com.wcs.travel_blog.comment.repository.CommentRepository;
import com.wcs.travel_blog.exception.ForbiddenOperationException;
import com.wcs.travel_blog.exception.ResourceNotFoundException;
import com.wcs.travel_blog.step.model.Step;
import com.wcs.travel_blog.step.repository.StepRepository;
import com.wcs.travel_blog.user.model.User;
import com.wcs.travel_blog.user.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final StepRepository stepRepository;
    private final UserRepository userRepository;
    private final CommentMapper commentMapper;


    public CommentService(CommentRepository commentRepository,
                          StepRepository stepRepository,
                          UserRepository userRepository,
                          CommentMapper commentMapper) {
        this.commentRepository = commentRepository;
        this.stepRepository = stepRepository;
        this.userRepository = userRepository;
        this.commentMapper = commentMapper;
    }

    public List<CommentDTO> getAllComments() {
        List<Comment> comments = commentRepository.findAll();
        return comments.stream().map(commentMapper::toDto).collect(Collectors.toList());
    }

    public CommentDTO getCommentById(Long id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Commentaire introuvable : " + id));
        return commentMapper.toDto(comment);
    }

    public List<CommentDTO> getCommentByStepId(Long stepId) {
        return commentRepository.findByStep_Id(stepId).stream().map(commentMapper::toDto).toList();
    }

    public List<CommentDTO> getCommentByUserId(Long userId) {
        return commentRepository.findByUser_Id(userId).stream().map(commentMapper::toDto).toList();
    }

    public CommentDTO createComment(Long authorId, UpsertCommentDTO upsertCommentDTO) {
        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable : " + authorId));
        Step step = stepRepository.findById(upsertCommentDTO.getStepId())
                .orElseThrow(() -> new ResourceNotFoundException("Étape introuvable : " + upsertCommentDTO.getStepId()));

        Comment comment = commentMapper.toEntity(upsertCommentDTO, author, step);
        comment.setCreatedAt(LocalDateTime.now());
        comment.setUpdatedAt(LocalDateTime.now());
        Comment savedComment = commentRepository.save(comment);
        return commentMapper.toDto(savedComment);
    }

    public CommentDTO updateCommentById(Long commentId, Long requesterId, UpsertCommentDTO upsertCommentDTO) {
        Comment existingComment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Commentaire introuvable : " + commentId));

        if (existingComment.getUser() == null || !existingComment.getUser().getId().equals(requesterId)) {
            throw new ForbiddenOperationException("Non autorisé à modifier ce commentaire.");
        }
        existingComment.setContent(upsertCommentDTO.getContent());
        existingComment.setUpdatedAt(LocalDateTime.now());
        return commentMapper.toDto(commentRepository.save(existingComment));
    }

    public void deleteCommentById(Long commentId, Long requesterId, boolean isAdmin) {
        Comment existing = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Commentaire introuvable : " + commentId));

        if (!isAdmin && (existing.getUser() == null || !existing.getUser().getId().equals(requesterId))) {
            throw new ForbiddenOperationException("Non autorisé à supprimer ce commentaire.");
        }
        commentRepository.delete(existing);
    }

    public CommentDTO moderateCommentById(Long commentId, ModerateCommentDTO moderateCommentDTO) {
        Comment existing = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Commentaire introuvable : " + commentId));
        existing.setStatus(CommentStatus.valueOf(moderateCommentDTO.getStatus()));
        existing.setUpdatedAt(LocalDateTime.now());
        return commentMapper.toDto(commentRepository.save(existing));
    }

}
