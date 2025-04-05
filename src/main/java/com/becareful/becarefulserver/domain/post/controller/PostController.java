package com.becareful.becarefulserver.domain.post.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/post")
@Tag(name = "Post", description = "커뮤니티 탭 게시글 관련 API 입니다.")
public class PostController {

    @Operation(summary = "모든 게시글 리스트 조회", description = "모든 게시글 리스트를 조회합니다.")
    @GetMapping("/list")
    public ResponseEntity<Void> getPosts() {
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "특정 게시글 상세 조회", description = "특정 게시글의 상세 내용을 조회합니다.")
    @GetMapping("/{postId}")
    public ResponseEntity<Void> getPost(@PathVariable Long postId) {
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "게시글 작성")
    @PostMapping
    public ResponseEntity<Void> createPost() {
        return ResponseEntity.created(URI.create("/post/" + UUID.randomUUID().toString())).build();
    }

    @Operation(summary = "게시글 수정")
    @PutMapping("/{postId}")
    public ResponseEntity<Void> updatePost(@PathVariable Long postId) {
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "게시글 삭제")
    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(@PathVariable Long postId) {
        return ResponseEntity.noContent().build();
    }
}
