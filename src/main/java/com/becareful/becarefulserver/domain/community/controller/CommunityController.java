package com.becareful.becarefulserver.domain.community.controller;

import com.becareful.becarefulserver.domain.association.dto.response.AssociationMyResponse;
import com.becareful.becarefulserver.domain.association.service.AssociationService;
import com.becareful.becarefulserver.domain.community.dto.response.CommunityAccessResponse;
import com.becareful.becarefulserver.domain.community.service.CommunityService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/community")
@Tag(name = "Community", description = "커뮤니티 관련 API 입니다.")
public class CommunityController {

    private final AssociationService associationService;
    private final CommunityService communityService;

    @Operation(summary = "커뮤니티 탭 첫 화면 데이터 조회", description = "협회 가입 여부에 따라 필요한 첫 화면 데이터를 응답합니다.")
    @GetMapping("/access")
    public ResponseEntity<CommunityAccessResponse> getAccessStatus(HttpServletResponse httpServletResponse) {
        CommunityAccessResponse response = communityService.getCommunityAccess(httpServletResponse);
        return ResponseEntity.ok(response);
    }

    @Hidden
    @Operation(summary = "커뮤니티 탭 협회 정보 조회", description = "현재 로그인한 사용자가 속한 협회의 정보를 조회합니다.")
    @GetMapping("/my/association")
    public ResponseEntity<AssociationMyResponse> getAssociationInfo() {
        var response = associationService.getMyAssociation();
        return ResponseEntity.ok(response);
    }
}
