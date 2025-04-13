package com.becareful.becarefulserver.domain.community.controller;

import com.becareful.becarefulserver.domain.community.dto.request.CommentCreateRequest;
import com.becareful.becarefulserver.domain.community.dto.response.CommentResponse;
import com.becareful.becarefulserver.domain.community.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/community/board/{boardId}/post/{postId}/comment")
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<Void> createComment(@PathVariable Long boardId, @PathVariable Long postId, @RequestBody CommentCreateRequest request) {
        Long commentId = commentService.createComment(boardId, postId, request);
        return ResponseEntity.created(URI.create("/community/board/" + boardId + "/post/" + postId + "/comment/" + commentId)).build();
    }

    @GetMapping
    public ResponseEntity<List<CommentResponse>> getComments(@PathVariable Long boardId, @PathVariable Long postId) {
        var response = commentService.getComments(boardId, postId);
        return ResponseEntity.ok(response);
    }

    // TODO : 댓글 수정 / 삭제
}
