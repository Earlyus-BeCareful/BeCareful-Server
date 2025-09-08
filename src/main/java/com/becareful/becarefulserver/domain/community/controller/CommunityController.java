package com.becareful.becarefulserver.domain.community.controller;

import com.becareful.becarefulserver.domain.association.service.*;
import com.becareful.becarefulserver.domain.community.dto.response.*;
import com.becareful.becarefulserver.domain.community.service.*;
import io.swagger.v3.oas.annotations.*;
import io.swagger.v3.oas.annotations.tags.*;
import jakarta.servlet.http.*;
import lombok.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/community")
@Tag(name = "Community", description = "커뮤니티 관련 API 입니다.")
public class CommunityController {

    private final AssociationService associationService;
    private final CommunityService communityService;

    @Operation(summary = "커뮤니티 탭 접근 권한 검증 및 각 화면 별 데이터 조회", description = "협회 가입 여부에 따라 필요한 첫 화면 데이터를 응답합니다.")
    @GetMapping("/access")
    public ResponseEntity<CommunityAccessResponse> getAccessStatus(HttpServletResponse httpServletResponse) {
        CommunityAccessResponse response = communityService.getCommunityAccess(httpServletResponse);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "커뮤니티 탭 협회 정보 조회", description = "현재 로그인한 사용자가 속한 협회의 정보를 조회합니다.")
    @GetMapping("/home")
    public ResponseEntity<CommunityHomeBasicInfoResponse> getAssociationInfo() {
        var response = communityService.getCommunityHomeInfo();
        return ResponseEntity.ok(response);
    }
}
