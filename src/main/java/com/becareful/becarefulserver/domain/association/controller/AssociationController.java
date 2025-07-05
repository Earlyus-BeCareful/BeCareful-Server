package com.becareful.becarefulserver.domain.association.controller;

import com.becareful.becarefulserver.domain.association.dto.request.AssociationCreateRequest;
import com.becareful.becarefulserver.domain.association.dto.request.AssociationJoinRequest;
import com.becareful.becarefulserver.domain.association.dto.response.*;
import com.becareful.becarefulserver.domain.association.service.AssociationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/association")
@Tag(name = "Community - Association", description = "커뮤니티 협회 관련 API 입니다.")
public class AssociationController {

    private final AssociationService associationService;

    @Operation(summary = "협회 생성", description = "협회 회장으로 승인 된 사용자만 협회 등록 가능")
    @PostMapping("/create")
    public ResponseEntity<Void> createAssociation(
            @Valid @RequestBody AssociationCreateRequest associationCreateRequest) {
        Long id = associationService.saveAssociation(associationCreateRequest);
        return ResponseEntity.created(URI.create("association/" + id)).build();
    }

    @Operation(summary = "협회 가입 전: 서비스에 등록된 협회 검색", description = "협회 가입 단계에서 협회 검색 API")
    @GetMapping("/search")
    public ResponseEntity<AssociationSearchListResponse> searchAssociation(
            @RequestParam(required = false) String associationName) {
        AssociationSearchListResponse response = associationService.searchAssociationByName(associationName);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "협회 가입 전: 서비스에 등록된 협회 리스트 반환", description = "협회 가입 단계에서 협회 검색 API")
    @GetMapping("/list")
    public ResponseEntity<AssociationSearchListResponse> getAssociationList() {
        AssociationSearchListResponse response = associationService.getAssociationList();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "협회 가입 신청", description = "협회 임원진, 회원 전용 API")
    @PostMapping("/join-requests")
    public ResponseEntity<Void> joinAssociation(@Valid @RequestBody AssociationJoinRequest request) {
        associationService.joinAssociation(request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "협회 회원 + 가입 신청자 요약", description = "커뮤니티 메인화면에서 회원 목록 요청")
    @GetMapping("/members/overview")
    public ResponseEntity<AssociationMemberOverviewResponse> getAssociationMemberOverview() {
        AssociationMemberOverviewResponse response = associationService.getAssociationMemberOverview();
        return ResponseEntity.ok().body(response);
    }

    @Operation(summary = "협회 회원 목록 조회", description = "협회장, 임원진, 회원 모두 접근 가능한 API")
    @GetMapping("/members")
    public ResponseEntity<AssociationMemberListResponse> getAssociationMembers() {
        AssociationMemberListResponse response = associationService.getAssociationMemberList();
        return ResponseEntity.ok().body(response);
    }

    @Operation(summary = "협회 가입 신청 목록 보기")
    @GetMapping("/join-requests")
    public ResponseEntity<AssociationJoinApplicationListResponse> getPendingJoinApplications() {
        AssociationJoinApplicationListResponse response = associationService.getAssociationJoinApplicationList();
        return ResponseEntity.ok().body(response);
    }

    @Operation(summary = "협회 가입 신청 승인", description = "협회장만 접근 가능한 API")
    @PutMapping("/join-requests/{id}/accept")
    public ResponseEntity<Void> acceptAssociationJoinRequest(@PathVariable Long id) {
        associationService.acceptJoinAssociation(id);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "협회 가입 신청 반려", description = "협회장만 접근 가능한 API")
    @PutMapping("/join-requests/{id}/reject")
    public ResponseEntity<Void> rejectAssociationJoinRequest(@PathVariable Long id) {
        associationService.rejectJoinAssociation(id);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "협회 회원 상세 정보 조회")
    @GetMapping("/members/{memberId}")
    public ResponseEntity<AssociationMemberDetailInfoResponse> getMemberDetailInfo(@PathVariable Long memberId) {
        AssociationMemberDetailInfoResponse response = associationService.getAssociationMemberDetailInfo(memberId);
        return ResponseEntity.ok().body(response);
    }

    @Operation(summary = "협회 회원 강제 탈퇴", description = "협회장이 특정 회원을 협회에서 탈퇴시키는 API")
    @DeleteMapping("/members/{memberId}/expel") // api url 수정
    public ResponseEntity<Void> expelAssociationMember(@PathVariable Long memberId) {
        associationService.expelMember(memberId); // 메서드 이름 수정
        return ResponseEntity.ok().build();
    }

    // 사진 등록
    @Operation(summary = "협회 프로필 사진 업로드", description = "협회 등록 전 프로필 이미지 저장 API")
    @PostMapping(value = "/upload-profile-img", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AssociationProfileImageUploadResponse> uploadProfileImg(@RequestPart MultipartFile file) {
        var response = associationService.uploadProfileImage(file);
        return ResponseEntity.ok(response);
    }
}
