package com.becareful.becarefulserver.domain.community.controller;

import com.becareful.becarefulserver.domain.association.dto.response.AssociationMyResponse;
import com.becareful.becarefulserver.domain.association.service.AssociationService;
import com.becareful.becarefulserver.domain.community.dto.PostSimpleDto;
import com.becareful.becarefulserver.domain.community.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/community")
@Tag(name = "Community", description = "커뮤니티 관련 API 입니다.")
public class CommunityController {

    private final AssociationService associationService;
    private final PostService postService;

    @Operation(summary = "커뮤니티 탭 협회 정보 조회", description = "현재 로그인한 사용자가 속한 협회의 정보를 조회합니다.")
    @GetMapping("/my/association")
    public ResponseEntity<AssociationMyResponse> getAssociationInfo() {
        var response = associationService.getMyAssociation();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "모든 게시판의 필독 게시글 모아보기", description = "읽기 권한이 없는 게시판의 필독 게시글은 조회되지 않습니다.")
    @GetMapping("/post/important")
    public ResponseEntity<List<PostSimpleDto>> getImportantPosts(Pageable pageable) {
        var response = postService.getImportantPosts(pageable);
        return ResponseEntity.ok(response);
    }
}
