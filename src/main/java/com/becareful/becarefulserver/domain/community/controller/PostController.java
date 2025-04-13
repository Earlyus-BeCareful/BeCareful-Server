package com.becareful.becarefulserver.domain.community.controller;

import com.becareful.becarefulserver.domain.community.dto.PostSimpleDto;
import com.becareful.becarefulserver.domain.community.dto.request.PostCreateRequest;
import com.becareful.becarefulserver.domain.community.dto.request.PostUpdateRequest;
import com.becareful.becarefulserver.domain.community.dto.response.PostDetailResponse;
import com.becareful.becarefulserver.domain.community.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/community/board/{boardId}/post")
@Tag(name = "Post", description = "커뮤니티 탭 게시글 관련 API 입니다.")
public class PostController {

    private final PostService postService;

    @Operation(summary = "게시글 작성")
    @PostMapping
    public ResponseEntity<Void> createPost(@PathVariable Long boardId, @RequestBody PostCreateRequest request) {
        Long postId = postService.createPost(boardId, request);
        return ResponseEntity.created(URI.create("/board/" + boardId + "/post/" + postId)).build();
    }

    @Operation(summary = "특정 게시판의 모든 게시글 리스트 조회")
    @GetMapping
    public ResponseEntity<List<PostSimpleDto>> getAllBoardPosts(@PathVariable Long boardId, Pageable pageable) {
        var response = postService.getPosts(boardId, pageable);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "특정 게시글 상세 조회", description = "특정 게시글의 상세 내용을 조회합니다.")
    @GetMapping("/{postId}")
    public ResponseEntity<PostDetailResponse> getPost(@PathVariable Long boardId, @PathVariable Long postId) {
        var response = postService.getPost(boardId, postId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "게시글 수정")
    @PutMapping("/{postId}")
    public ResponseEntity<Void> updatePost(@PathVariable Long boardId, @PathVariable Long postId, @RequestBody PostUpdateRequest request) {
        postService.updatePost(boardId, postId, request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "게시글 삭제")
    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(@PathVariable Long boardId, @PathVariable Long postId) {
        postService.deletePost(boardId, postId);
        return ResponseEntity.noContent().build();
    }
}
