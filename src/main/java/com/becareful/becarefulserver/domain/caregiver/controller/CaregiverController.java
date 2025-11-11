package com.becareful.becarefulserver.domain.caregiver.controller;

import com.becareful.becarefulserver.domain.auth.service.AuthService;
import com.becareful.becarefulserver.domain.caregiver.dto.request.*;
import com.becareful.becarefulserver.domain.caregiver.dto.response.*;
import com.becareful.becarefulserver.domain.caregiver.service.*;
import com.becareful.becarefulserver.domain.common.dto.request.*;
import com.becareful.becarefulserver.domain.common.dto.response.*;
import com.becareful.becarefulserver.domain.matching.dto.request.*;
import com.becareful.becarefulserver.domain.matching.dto.response.*;
import com.becareful.becarefulserver.domain.matching.service.*;
import io.swagger.v3.oas.annotations.*;
import io.swagger.v3.oas.annotations.tags.*;
import jakarta.servlet.http.*;
import jakarta.validation.*;
import java.net.*;
import java.util.*;
import lombok.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/caregiver")
@Tag(name = "Caregiver", description = "요양보호사 관련 API 입니다.")
public class CaregiverController {

    private final CaregiverService caregiverService;
    private final CareerService careerService;
    private final CompletedMatchingService completedMatchingService;
    private final AuthService authService;

    @Operation(
            summary = "요양보호사 회원가입",
            description = "사회복지사 (social worker), 간호조무사 (nursing care), 프로필 이미지 필드는 생략할 수 있습니다.")
    @PostMapping("/signup")
    public ResponseEntity<Void> createCaregiver(@Valid @RequestBody CaregiverCreateRequest request) {
        Long id = caregiverService.saveCaregiver(request);
        return ResponseEntity.created(URI.create("/caregiver/" + id)).build();
    }

    @Operation(summary = "요양보호사 로그아웃")
    @PutMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletResponse httpServletResponse) {
        authService.logout(httpServletResponse);
        return ResponseEntity.ok().build();
    }

    // TODO : 특별한 상황이 아니면 url 에 동사는 사용하지 않음. delete caregiver 의미로 회원 탈퇴는 충분하므로 DELETE /caregiver 만 사용
    @Operation(summary = "요양보호사 탈퇴")
    @DeleteMapping("/leave")
    public ResponseEntity<Void> deleteCaregiver(HttpServletResponse httpServletResponse) {
        caregiverService.deleteCaregiver(httpServletResponse);
        return ResponseEntity.noContent().build();
    }

    // Todo: 삭제
    @Deprecated
    @Operation(summary = "이미지 업로드 구버전(삭제 예정): 요양보호사 프로필 사진 신규 업로드", description = "요양보호사 프로필 이미지 업로드 API 입니다.")
    @PostMapping(value = "/upload-profile-img", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CaregiverProfileUploadResponse> uploadProfileImg(@RequestPart MultipartFile file) {
        var response = caregiverService.uploadProfileImage(file);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "이미지 업로드 (신버전): 프로필 이미지 업로드용 Presigned URL 발급",
            description =
                    """
    프론트엔드에서 사용자가 로컬 파일을 선택할 때마다 이 API를 호출해 Presigned URL을 발급받습니다.
    발급받은 URL로 S3에 이미지를 직접 업로드합니다.
    이후 회원가입 또는 정보 수정 시, S3에 업로드한 파일의 tempKey를 백엔드로 전달해야 합니다.
    """)
    @PostMapping("/profile-img/presigned-url")
    public ResponseEntity<PresignedUrlResponse> createPresignedUrl(ProfileImagePresignedUrlRequest request) {
        PresignedUrlResponse response = caregiverService.getPresignedUrl(request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "요양보호사 홈 화면 구성 데이터 조회")
    @GetMapping("/home")
    public ResponseEntity<CaregiverHomeResponse> getCaregiverHomeData() {
        var response = caregiverService.getHomeData();
        return ResponseEntity.ok(response);
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
        var response = caregiverService.getCaregiverMyPageHomeData();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "요양보호사 마이페이지 수정")
    @PutMapping("/my")
    public ResponseEntity<Void> updateMyPageInfo(@Valid @RequestBody MyPageUpdateRequest request) {
        caregiverService.updateCaregiverInfo(request);
        return ResponseEntity.ok().build();
    }

    // TODO : url 에 list 는 적지 않도록 삭제
    @Operation(summary = "확정된 일자리의 리스트가 반환됩니다.")
    @GetMapping("/my/completed-matching-list")
    public ResponseEntity<List<CompletedMatchingInfoResponse>> getCompletedMatchingsByCaregiverId() {
        List<CompletedMatchingInfoResponse> responseList = completedMatchingService.getCompletedMatchings();
        return ResponseEntity.ok(responseList);
    }

    // TODO : url 에 list 는 적지 않도록 삭제
    @Operation(summary = "나의 일자리 화면에서 메모 수정")
    @PutMapping("/my/complete-matching-list/{completedMatchingId}")
    public ResponseEntity<Void> editCompletedMatchingMemo(
            @PathVariable Long completedMatchingId, @RequestBody EditCompletedMatchingNoteRequest request) {
        completedMatchingService.editNote(completedMatchingId, request);
        return ResponseEntity.ok().build();
    }
}
