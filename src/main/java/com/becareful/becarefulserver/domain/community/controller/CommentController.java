package com.becareful.becarefulserver.domain.community.controller;

import com.becareful.becarefulserver.domain.community.dto.request.CommentCreateRequest;
import com.becareful.becarefulserver.domain.community.dto.request.CommentUpdateRequest;
import com.becareful.becarefulserver.domain.community.dto.response.CommentResponse;
import com.becareful.becarefulserver.domain.community.service.CommentService;
import com.becareful.becarefulserver.domain.report.dto.request.ReportCreateRequest;
import com.becareful.becarefulserver.domain.report.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/community/board/{boardType}/post/{postId}/comment")
@Tag(name = "Community - Comment", description = "커뮤니티 탭 댓글 관련 API 입니다.")
public class CommentController {

    private final CommentService commentService;
    private final ReportService reportService;

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

    @Operation(summary = "댓글 수정")
    @PutMapping("/{commentId}")
    public ResponseEntity<Void> updateComment(
            @PathVariable String boardType,
            @PathVariable Long postId,
            @PathVariable Long commentId,
            @RequestBody CommentUpdateRequest request) {
        commentService.updateComment(boardType, postId, commentId, request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "댓글 삭제")
    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable String boardType, @PathVariable Long postId, @PathVariable Long commentId) {
        commentService.deleteComment(boardType, postId, commentId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "댓글 신고")
    @PutMapping("/{commentId}/report")
    public ResponseEntity<Void> reportComment(
            @PathVariable String boardType,
            @PathVariable Long postId,
            @PathVariable Long commentId,
            @Valid @RequestBody ReportCreateRequest request) {
        reportService.reportComment(boardType, postId, commentId, request);
        return ResponseEntity.ok().build();
    }
}
