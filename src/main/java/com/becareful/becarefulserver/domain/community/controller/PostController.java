package com.becareful.becarefulserver.domain.community.controller;

import com.becareful.becarefulserver.domain.community.domain.FileType;
import com.becareful.becarefulserver.domain.community.dto.MediaInfoDto;
import com.becareful.becarefulserver.domain.community.dto.PostSimpleDto;
import com.becareful.becarefulserver.domain.community.dto.request.PostCreateOrUpdateRequest;
import com.becareful.becarefulserver.domain.community.dto.response.PostDetailResponse;
import com.becareful.becarefulserver.domain.community.service.PostMediaService;
import com.becareful.becarefulserver.domain.community.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/community")
@Tag(name = "Post", description = "커뮤니티 탭 게시글 관련 API 입니다.")
public class PostController {

    private final PostService postService;
    private final PostMediaService postMediaService;

    @Operation(summary = "게시글 작성")
    @PostMapping("/board/{boardType}/post")
    public ResponseEntity<Void> createPost(
            @PathVariable String boardType, @RequestBody PostCreateOrUpdateRequest request) {
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
            @PathVariable String boardType, @PathVariable Long postId, @RequestBody PostCreateOrUpdateRequest request) {
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

    @Operation(
            summary = "미디어 파일 업로드",
            description = "게시글 작성/수정 전에 미디어 파일을 먼저 업로드합니다.\n" + "- 이미지: 1개당 30MB 이하, 최대 100개\n"
                    + "- 동영상: 1개당 1GB 이하, 최대 3개, 각 15분 이내\n"
                    + "- 파일: 1개당 10MB 이하, 최대 5개, 게시글당 총 30MB 이하\n"
                    + "\n[videoDuration] 파라미터는 fileType이 VIDEO일 때만 필수이며, 그 외 타입(IMAGE, FILE)일 때는 비워서 보내도 됩니다.")
    @PostMapping(value = "/media/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MediaInfoDto> uploadMedia(
            @RequestParam("file") MultipartFile file,
            @RequestParam("fileType") FileType fileType,
            @RequestParam(value = "videoDuration", required = false) Integer videoDuration) {
        var response = postMediaService.uploadPostMedia(file, fileType, videoDuration);
        return ResponseEntity.ok(response);
    }
}
