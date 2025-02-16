package com.becareful.becarefulserver.domain.caregiver.controller;

import jakarta.validation.Valid;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.becareful.becarefulserver.domain.caregiver.dto.request.CaregiverCreateRequest;
import com.becareful.becarefulserver.domain.caregiver.dto.request.WorkApplicationUpdateRequest;
import com.becareful.becarefulserver.domain.caregiver.dto.response.CaregiverHomeResponse;
import com.becareful.becarefulserver.domain.caregiver.dto.response.CaregiverProfileUploadResponse;
import com.becareful.becarefulserver.domain.caregiver.dto.response.WorkApplicationResponse;
import com.becareful.becarefulserver.domain.caregiver.service.CaregiverService;
import com.becareful.becarefulserver.domain.caregiver.service.WorkApplicationService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.net.URI;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/caregiver")
@Tag(name = "Caregiver", description = "요양보호사 관련 API 입니다.")
public class CaregiverController {

    private final CaregiverService caregiverService;
    private final WorkApplicationService workApplicationService;

    @Operation(summary = "요양보호사 회원가입", description = "사회복지사 (social worker), 간호조무사 (nursing care), 프로필 이미지 필드는 생략할 수 있습니다.")
    @PostMapping("/signup")
    public ResponseEntity<Void> createCaregiver(@Valid @RequestBody CaregiverCreateRequest request) {
        Long id = caregiverService.saveCaregiver(request);
        return ResponseEntity.created(URI.create("/caregiver/" + id)).build();
    }

    @Operation(summary = "요양보호사 프로필 사진 신규 업로드", description = "요양보호사 회원가입 과정에서만 사용하는 프로필 이미지 업로드 API 입니다.")
    @PostMapping(value = "/upload-profile-img", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CaregiverProfileUploadResponse> uploadProfileImg(
            @RequestPart MultipartFile file,
            @RequestPart String phoneNumber) {
        var response = caregiverService.uploadProfileImage(file, phoneNumber);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "요양보호사 홈 화면 구성 데이터 조회")
    @GetMapping("/home")
    public ResponseEntity<CaregiverHomeResponse> getCaregiverHomeData() {
        var response = caregiverService.getHomeData();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "일자리 신청 정보 등록/수정")
    @PutMapping("/work-application")
    public ResponseEntity<Void> updateWorkApplication(@Valid @RequestBody WorkApplicationUpdateRequest request) {
        workApplicationService.updateWorkApplication(request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "일자리 신청 정보 조회", description = "신청 정보를 등록한 적이 없다면 빈 값을 반환합니다.")
    @GetMapping("/work-application")
    public ResponseEntity<WorkApplicationResponse> getWorkApplication() {
        WorkApplicationResponse response = workApplicationService.getWorkApplication();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "일자리 신청 활성화", description = "등록한 일자리 신청 내용을 기반으로 매칭을 활성화 합니다.")
    @PostMapping("/work-applicatioin/active")
    public ResponseEntity<Void> updateWorkApplicationActive() {
        workApplicationService.updateWorkApplicationActive();
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "일자리 신청 비활성화", description = "일자리 매칭을 비활성화 합니다.")
    @PostMapping("/work-applicatioin/inactive")
    public ResponseEntity<Void> updateWorkApplicationInactive() {
        workApplicationService.updateWorkApplicationInactive();
        return ResponseEntity.ok().build();
    }
}
