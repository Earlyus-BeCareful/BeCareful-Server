package com.becareful.becarefulserver.domain.socialworker.controller;
import com.becareful.becarefulserver.domain.caregiver.dto.response.CaregiverProfileUploadResponse;
import com.becareful.becarefulserver.domain.socialworker.dto.request.NursingInstitutionCreateRequest;
import com.becareful.becarefulserver.domain.socialworker.service.NursingInstitutionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("/nursingInstitution")
@Tag(name = "nursingInstitution", description = "요양기관 관련 API 입니다.")
public class NursingInstitutionController {
    private final NursingInstitutionService nursingInstitutionService;

    @Operation(summary = "요양 기관 조회", description = "요양 기관이 이미 DB에 등록된 기관인지 확인")
    @GetMapping("/{institutionId}/exists")
    public ResponseEntity<Boolean> checkExistingNursingInstitution(@PathVariable String institutionId) {
        boolean exists = nursingInstitutionService.existsById(institutionId);
        return ResponseEntity.ok(exists);
        }
    @Operation(summary = "요양 기관 등록", description = "요양 기관이 등록 되지 않았으면 등록 요청")
    @PostMapping("/register")
    public ResponseEntity<Void> createNursingInstitution(@Valid @RequestBody NursingInstitutionCreateRequest request){
        String id = nursingInstitutionService.saveNursingInstitution(request);
        return ResponseEntity.created(URI.create("/nursingInstitution/" + id)).build();
    }

    @Operation(summary = "요양 기관 프로필 사진 업로드", description = "사회복지사 회원가입 시 마지막에 뜨는 창.")
    @PostMapping(value = "/upload-profile-img", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CaregiverProfileUploadResponse> uploadProfileImg(
            @RequestPart MultipartFile file,
            @RequestPart String phoneNumber) {
        var response = nursingInstitutionService.uploadProfileImage(file, phoneNumber);
        return ResponseEntity.ok(response);
    }
}
