package com.becareful.becarefulserver.domain.community.controller;

import com.becareful.becarefulserver.domain.community.dto.request.CommentCreateRequest;
import com.becareful.becarefulserver.domain.community.dto.response.CommentResponse;
import com.becareful.becarefulserver.domain.community.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/community/board/{boardType}/post/{postId}/comment")
@Tag(name = "Comment", description = "커뮤니티 탭 댓글 관련 API 입니다.")
public class CommentController {

    private final CommentService commentService;

    @Operation(summary = "댓글 작성")
    @PostMapping
    public ResponseEntity<Void> createComment(
            @PathVariable String boardType, @PathVariable Long postId, @RequestBody CommentCreateRequest request) {
        Long commentId = commentService.createComment(boardType, postId, request);
        return ResponseEntity.created(
                        URI.create("/community/board/" + boardType + "/post/" + postId + "/comment/" + commentId))
                .build();
    }

    @Operation(summary = "댓글 조회")
    @GetMapping
    public ResponseEntity<List<CommentResponse>> getComments(
            @PathVariable String boardType, @PathVariable Long postId) {
        var response = commentService.getComments(boardType, postId);
        return ResponseEntity.ok(response);
    }

    // TODO : 댓글 수정 / 삭제
}
