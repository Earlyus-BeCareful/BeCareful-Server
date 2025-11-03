package com.becareful.becarefulserver.domain.association.controller;

import com.becareful.becarefulserver.domain.association.dto.request.*;
import com.becareful.becarefulserver.domain.association.dto.response.*;
import com.becareful.becarefulserver.domain.association.service.*;
import com.becareful.becarefulserver.domain.common.dto.request.*;
import com.becareful.becarefulserver.domain.common.dto.response.*;
import io.swagger.v3.oas.annotations.*;
import io.swagger.v3.oas.annotations.tags.*;
import jakarta.servlet.http.*;
import jakarta.validation.*;
import java.net.*;
import lombok.*;
import org.springframework.data.crossstore.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.*;

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
        Long id = associationService.createAssociation(associationCreateRequest);
        return ResponseEntity.created(URI.create("association/" + id)).build();
    }

    @Operation(summary = "협회 가입 전: 서비스에 등록된 협회 검색", description = "협회 가입 단계에서 협회 검색 API")
    @GetMapping("/search")
    public ResponseEntity<AssociationSearchListResponse> searchAssociation(
            @RequestParam(required = false) String associationName) {
        var response = associationService.searchAssociationByName(associationName);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "협회 가입 전: 서비스에 등록된 협회 리스트 반환", description = "협회 가입 단계에서 협회 검색 API")
    @GetMapping("/list")
    public ResponseEntity<AssociationSearchListResponse> getAssociationList() {
        var response = associationService.getAssociationList();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "협회 가입 신청", description = "협회 임원진, 회원 전용 API")
    @PostMapping("/join-requests")
    public ResponseEntity<Void> joinAssociation(@Valid @RequestBody AssociationJoinRequest request) {
        associationService.joinAssociation(request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "협회 가입 신청 취소", description = "본인의 협회 가입 신청을 취소하는 API")
    @DeleteMapping("/join-requests")
    public ResponseEntity<Void> cancelMyJoinRequest() {
        associationService.cancelMyJoinRequest();
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "협회 회원 수, 가입 신청서 개수 반환")
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

    @Operation(summary = "회원 등급 변경", description = "협회장&임원진 권한" + "임원진과 회원 등급만 변경 가능" + "임원진은 최소 한 명 이상이어야함.")
    @PutMapping("/members/rank")
    public ResponseEntity<Void> updateAssociationRank(@Valid @RequestBody UpdateAssociationRankRequest request) {
        associationService.updateAssociationRank(request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "협회장 위임", description = "협회장 권한 API")
    @PutMapping("/chairman/delegate")
    public ResponseEntity<Void> updateAssociationChairman(
            @Valid @RequestBody UpdateAssociationChairmanRequest request, HttpServletResponse response)
            throws ChangeSetPersister.NotFoundException {
        associationService.updateAssociationChairman(request, response);
        return ResponseEntity.ok().build();
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

    @Operation(summary = "협회 정보 조회", description = "홈화면에서 협회이름 클릭하면 반환하는 페이지")
    @GetMapping("/info")
    public ResponseEntity<AssociationInfoResponse> getAssociationInfo() {
        AssociationInfoResponse response = associationService.getAssociationInfo();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "협회 정보 수정")
    @PutMapping("/info")
    public ResponseEntity<Void> updateMyBasicInfo(@Valid @RequestBody UpdateAssociationInfoRequest request) {
        associationService.updateAssociationInfo(request);
        return ResponseEntity.ok().build();
    }

    // TODO: 삭제
    @Deprecated
    @Operation(summary = "(구버전. 삭제예정)협회 프로필 사진 업로드", description = "협회 등록 전 프로필 이미지 저장 API")
    @PostMapping(value = "/upload-profile-img", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AssociationProfileImageUploadResponse> uploadProfileImg(@RequestPart MultipartFile file) {
        var response = associationService.uploadProfileImage(file);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "이미지 업로드 (신버전): 협회 프로필 이미지 업로드용 Presigned URL 발급",
            description =
                    """
    프론트엔드에서 사용자가 로컬 파일을 선택할 때마다 이 API를 호출해 Presigned URL을 발급받습니다.
    발급받은 URL로 S3에 이미지를 직접 업로드합니다.
    이후 협회 등록 또는 정보 수정 시, S3에 업로드한 파일의 tempKey를 백엔드로 전달해야 합니다.
    """)
    @PostMapping("/profile-img/presigned-url")
    public ResponseEntity<PresignedUrlResponse> createPresignedUrl(ProfileImagePresignedUrlRequest request) {
        PresignedUrlResponse response = associationService.getPresignedUrl(request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "협회 탈퇴", description = "마이페이지-센터장, 대표")
    @PutMapping("/leave")
    public ResponseEntity<Void> leaveAssociation(HttpServletResponse response) {
        associationService.leaveAssociation(response);
        return ResponseEntity.ok().build();
    }
}
