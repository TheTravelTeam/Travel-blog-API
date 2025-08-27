package com.wcs.travel_blog.comment.controller;

import com.wcs.travel_blog.comment.dto.CommentDTO;
import com.wcs.travel_blog.comment.dto.ModerateCommentDTO;
import com.wcs.travel_blog.comment.dto.UpsertCommentDTO;
import com.wcs.travel_blog.comment.service.CommentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comments")
public class CommentController {

    private final CommentService commentService;
    public CommentController(CommentService commentService) { this.commentService = commentService; }

    @GetMapping
    public ResponseEntity<List<CommentDTO>> getAllComments(
            @RequestParam(required = false) Long stepId,
            @RequestParam(required = false) Long userId) {
        List<CommentDTO> commentDTOS =
                stepId != null ? commentService.getCommentByStepId(stepId)
                        : userId != null ? commentService.getCommentByUserId(userId)
                        : commentService.getAllComments();
        return commentDTOS.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(commentDTOS);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommentDTO> getCommentById(@PathVariable Long id) {
        CommentDTO commentDTO = commentService.getCommentById(id);
        if(commentDTO == null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(commentDTO);
    }

    @PostMapping
    public ResponseEntity<CommentDTO> createComment(@AuthenticationPrincipal Long currentUserId,
                                             @Valid @RequestBody UpsertCommentDTO upsertCommentDTO) {
        CommentDTO commentDTO = commentService.createComment(currentUserId, upsertCommentDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(commentDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CommentDTO> updateCommentById(@PathVariable Long id,
                                             @AuthenticationPrincipal(expression = "id") Long currentUserId,
                                             @AuthenticationPrincipal(expression = "admin") Boolean isAdmin,
                                             @Valid @RequestBody UpsertCommentDTO upsertCommentDTO) {
        boolean admin = isAdmin != null && isAdmin;
        CommentDTO commentDTO = commentService.updateCommentById(id, currentUserId, admin, upsertCommentDTO);
        return ResponseEntity.ok(commentDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCommentById(@PathVariable Long id,
                                       @AuthenticationPrincipal(expression = "id") Long currentUserId,
                                       @AuthenticationPrincipal(expression = "admin") Boolean isAdmin) {
        boolean admin = isAdmin != null && isAdmin;
        commentService.deleteCommentById(id, currentUserId, admin);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PatchMapping("/{id}/status")
    public ResponseEntity<CommentDTO> moderateCommentById(@PathVariable Long id,
                                               @Valid @RequestBody ModerateCommentDTO moderateCommentDTO) {
        return ResponseEntity.ok(commentService.moderateCommentById(id, moderateCommentDTO));
    }
}