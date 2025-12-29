package com.becareful.becarefulserver.domain.community.controller;

import com.becareful.becarefulserver.domain.common.dto.response.*;
import com.becareful.becarefulserver.domain.community.dto.*;
import com.becareful.becarefulserver.domain.community.dto.request.*;
import com.becareful.becarefulserver.domain.community.dto.response.*;
import com.becareful.becarefulserver.domain.community.service.*;
import com.becareful.becarefulserver.domain.report.dto.request.ReportCreateRequest;
import com.becareful.becarefulserver.domain.report.service.ReportService;
import io.swagger.v3.oas.annotations.*;
import io.swagger.v3.oas.annotations.tags.*;
import jakarta.validation.Valid;
import java.net.*;
import java.util.*;
import lombok.*;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/community")
@Tag(name = "Community - Post", description = "커뮤니티 탭 게시글 관련 API 입니다.")
public class PostController {

    private final PostService postService;
    private final PostMediaService postMediaService;
    private final ReportService reportService;

    @Operation(summary = "게시글 작성", description = "original url 필드의 경우, 협회 공지 게시판 이외에는 비워둡니다.")
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

    @Operation(summary = "게시글 신고")
    @PostMapping("/board/{boardType}/post/{postId}/report")
    public ResponseEntity<Void> deletePost(
            @PathVariable String boardType,
            @PathVariable Long postId,
            @Valid @RequestBody ReportCreateRequest request) {
        reportService.reportPost(boardType, postId, request);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "모든 게시판의 필독 게시글 모아보기", description = "읽기 권한이 없는 게시판의 필독 게시글은 조회되지 않습니다.")
    @GetMapping("/post/important")
    public ResponseEntity<List<PostSimpleDto>> getImportantPosts(Pageable pageable) {
        var response = postService.getImportantPosts(pageable);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "게시글 이미지 업로드 (신버전): 게시글 파일 업로드용 Presigned URL 발급",
            description =
                    """
    프론트엔드에서 사용자가 로컬 파일을 선택할 때마다 이 API를 호출해 Presigned URL을 발급받습니다.
    발급받은 URL로 S3에 이미지를 직접 업로드합니다.
    이후 게시글 등록/수정 시, S3에 업로드한 파일의 tempKey를 백엔드로 전달해야 합니다.
    - 이미지: 1개당 30MB 이하, 최대 100개\\n
    - 동영상: 1개당 1GB 이하, 최대 3개, 각 15분 이내\\n
    - 파일: 1개당 10MB 이하, 최대 5개, 게시글당 총 30MB 이하\\n
    \\n[videoDuration] 파라미터는 fileType이 VIDEO일 때만 필수이며, 그 외 타입(IMAGE, FILE)일 때는 비워서 보내도 됩니다.)
    """)
    @PostMapping("/post/media/presigned-url")
    public ResponseEntity<PresignedUrlResponse> createPresignedUrl(PostMediaPresignedUrlRequest request) {
        PresignedUrlResponse response = postMediaService.getPresignedUrl(request);
        return ResponseEntity.ok(response);
    }
}
