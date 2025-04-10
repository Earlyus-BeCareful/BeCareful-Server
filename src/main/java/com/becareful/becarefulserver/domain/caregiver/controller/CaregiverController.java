package com.becareful.becarefulserver.domain.caregiver.controller;

import com.becareful.becarefulserver.domain.caregiver.dto.request.CareerUpdateRequest;
import com.becareful.becarefulserver.domain.caregiver.dto.request.CaregiverCreateRequest;
import com.becareful.becarefulserver.domain.caregiver.dto.request.WorkApplicationUpdateRequest;
import com.becareful.becarefulserver.domain.caregiver.dto.response.*;
import com.becareful.becarefulserver.domain.caregiver.service.CareerService;
import com.becareful.becarefulserver.domain.caregiver.service.CaregiverService;
import com.becareful.becarefulserver.domain.caregiver.service.WorkApplicationService;
import com.becareful.becarefulserver.domain.recruitment.dto.request.EditCompletedMatchingNoteRequest;
import com.becareful.becarefulserver.domain.recruitment.dto.response.CompletedMatchingInfoResponse;
import com.becareful.becarefulserver.domain.recruitment.service.CompletedMatchingService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.util.List;

@Hidden
@RestController
@RequiredArgsConstructor
@RequestMapping("/caregiver")
@Tag(name = "Caregiver", description = "요양보호사 관련 API 입니다.")
public class CaregiverController {

    private final CaregiverService caregiverService;
    private final WorkApplicationService workApplicationService;
    private final CareerService careerService;
    private final CompletedMatchingService completedMatchingService;

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

    @Operation(summary = "경력서 등록/수정")
    @PutMapping("/career")
    public ResponseEntity<Void> updateCareer(@Valid @RequestBody CareerUpdateRequest request) {
        careerService.updateCareer(request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "경력서 조회", description = "경력서가 없는 경우에는 null 과 빈 리스트를 반환합니다.")
    @GetMapping("/career")
    public ResponseEntity<CareerResponse> getCareer() {
        CareerResponse response = careerService.getCareer();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "요양보호사 마이페이지 홈 화면 데이터 조회")
    @GetMapping("/my")
    public ResponseEntity<CaregiverMyPageHomeResponse> getMyPageHomeData() {
        CaregiverMyPageHomeResponse response = caregiverService.getMyPageHomeData();
        return ResponseEntity.ok(response);
    }

    //TODO
    @Operation(summary = "확정된 일자리의 리스트가 반환됩니다.")
    @GetMapping("/my/completed-matching-list")
    public ResponseEntity<List<CompletedMatchingInfoResponse>> getCompletedMatchingsByCaregiverId() {
            List<CompletedMatchingInfoResponse> responseList = completedMatchingService.getCompletedMatchings();
            return ResponseEntity.ok(responseList);
        }

    //TODO
    @Operation(summary = "나의 일자리 화면에서 메모 수정")
    @PutMapping("/my/complete-matching-list/{completedMatchingId}")
    public ResponseEntity<Void> editCompletedMatchingMemo(@PathVariable Long completedMatchingId, @RequestBody EditCompletedMatchingNoteRequest request){
        completedMatchingService.editNote(completedMatchingId, request);
        return ResponseEntity.ok().build();
    }


    @Operation(summary = "요양보호사 채팅 목록")
    @GetMapping("/chat/list")
    public ResponseEntity<ChatList> getChatInfoList(){
        ChatList response = caregiverService.getChatList();
        return ResponseEntity.ok(response);
    }

}
