package com.becareful.becarefulserver.domain.association.controller;

import com.becareful.becarefulserver.domain.association.dto.request.AssociationCreateRequest;
import com.becareful.becarefulserver.domain.association.dto.request.AssociationJoinRequest;
import com.becareful.becarefulserver.domain.association.dto.response.AssociationProfileImageUploadResponse;
import com.becareful.becarefulserver.domain.association.service.AssociationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/association")
@Tag(name = "association", description = "협회 관련 API 입니다.")
public class AssociationController {

    private final AssociationService associationService;


    //가입-검색(이름, 설립일, 회원수, 사진 반환/ 이름으로 검색)
    //가입(임원진, 회원 선택 -> 대기명단에 등록)
    //TODO(role 확인)
    @Operation(summary = "협회 가입 신청", description = "협회 임원진, 회원 전용 API")
    @PostMapping("/join")
    public ResponseEntity<Void> joinAssociation(@Valid @RequestBody AssociationJoinRequest request) {
        associationService.joinAssociation(request);
        return ResponseEntity.ok().build();
    }

    //TODO(role 확인)
    @Operation(summary = "협회 등록", description = "협회 회장으로 승인 된 사용자만 협회 등록 가능")
    @PostMapping("/register")
    public ResponseEntity<Long> createAssociation(@Valid @RequestBody AssociationCreateRequest associationCreateRequest) {
        Long id = associationService.saveAssociation(associationCreateRequest);
        return ResponseEntity.ok(id);
    }

    //TODO(role 확인)
    @Operation(summary = "협회 가입 신청 반려", description = "협회장만 접근 가능한 API")
    @PutMapping("/reject/join/{requestId}")
    public ResponseEntity<Void> rejectAssociationJoinRequest(@PathVariable Long requestId) {
        associationService.rejectJoinAssociation(requestId);
        return ResponseEntity.ok().build();
    }

    //가입 신청 목록에서 승인하기 - 승인이 완료되면 신청한 회원의 role 수정
    @Operation(summary = "협회 가입 신청 승인", description = "협회장만 접근 가능한 API")
    @PutMapping("/accept/join/{requestId}")
    public ResponseEntity<Void> acceptAssociation(@PathVariable Long requestId) {
        associationService.accpetJoinAssociation(requestId);
        return ResponseEntity.ok().build();
    }


    //사진 등록
    @Operation(summary = "협회 프로필 사진 업로드", description = "협회 등록 전 프로필 이미지 저장 API")
    @PostMapping(value = "/upload-profile-img", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AssociationProfileImageUploadResponse> uploadProfileImg(
            @RequestPart MultipartFile file) {

        var response = associationService.uploadProfileImage(file);
        return ResponseEntity.ok(response);
    }

    //TODO(협회 가입 전 검색)
}
