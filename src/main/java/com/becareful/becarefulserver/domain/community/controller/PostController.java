package com.becareful.becarefulserver.domain.community.controller;

import com.becareful.becarefulserver.domain.community.dto.PostSimpleDto;
import com.becareful.becarefulserver.domain.community.dto.request.PostCreateRequest;
import com.becareful.becarefulserver.domain.community.dto.request.PostUpdateRequest;
import com.becareful.becarefulserver.domain.community.dto.response.PostDetailResponse;
import com.becareful.becarefulserver.domain.community.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/community")
@Tag(name = "Post", description = "커뮤니티 탭 게시글 관련 API 입니다.")
public class PostController {

    private final PostService postService;

    @Operation(summary = "게시글 작성")
    @PostMapping("/board/{boardType}/post")
    public ResponseEntity<Void> createPost(@PathVariable String boardType, @RequestBody PostCreateRequest request) {
        Long postId = postService.createPost(boardType, request);
        return ResponseEntity.created(URI.create("/board/" + boardType + "/post/" + postId))
                .build();
    }

    @Operation(summary = "특정 게시판의 모든 게시글 리스트 조회")
    @GetMapping("/board/{boardType}/post")
    public ResponseEntity<List<PostSimpleDto>> getAllBoardPosts(@PathVariable String boardType, Pageable pageable) {
        var response = postService.getPosts(boardType, pageable);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "특정 게시글 상세 조회", description = "특정 게시글의 상세 내용을 조회합니다.")
    @GetMapping("/board/{boardType}/post/{postId}")
    public ResponseEntity<PostDetailResponse> getPost(@PathVariable String boardType, @PathVariable Long postId) {
        var response = postService.getPost(boardType, postId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "게시글 수정")
    @PutMapping("/board/{boardType}/post/{postId}")
    public ResponseEntity<Void> updatePost(
            @PathVariable String boardType, @PathVariable Long postId, @RequestBody PostUpdateRequest request) {
        postService.updatePost(boardType, postId, request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "게시글 삭제")
    @DeleteMapping("/board/{boardType}/post/{postId}")
    public ResponseEntity<Void> deletePost(@PathVariable String boardType, @PathVariable Long postId) {
        postService.deletePost(boardType, postId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "모든 게시판의 필독 게시글 모아보기", description = "읽기 권한이 없는 게시판의 필독 게시글은 조회되지 않습니다.")
    @GetMapping("/post/important")
    public ResponseEntity<List<PostSimpleDto>> getImportantPosts(Pageable pageable) {
        var response = postService.getImportantPosts(pageable);
        return ResponseEntity.ok(response);
    }
}
