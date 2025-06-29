package com.becareful.becarefulserver.domain.nursing_institution.controller;

import static com.becareful.becarefulserver.global.exception.ErrorMessage.NURSING_INSTITUTION_REQUIRE_CODE;

import com.becareful.becarefulserver.domain.nursing_institution.dto.request.NursingInstitutionCreateRequest;
import com.becareful.becarefulserver.domain.nursing_institution.dto.response.NursingInstitutionProfileUploadResponse;
import com.becareful.becarefulserver.domain.nursing_institution.dto.response.NursingInstitutionSearchResponse;
import com.becareful.becarefulserver.domain.nursing_institution.service.NursingInstitutionService;
import com.becareful.becarefulserver.global.exception.exception.NursingInstitutionException;
import io.swagger.v3.oas.annotations.Hidden;
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
@RequestMapping("/nursingInstitution")
@Tag(name = "Nursing Institution", description = "요양기관 관련 API 입니다.")
public class NursingInstitutionController {
    private final NursingInstitutionService nursingInstitutionService;

    @Operation(summary = "회원가입 전: 서비스에 등록된 요양 기관 검색", description = "회원가입 단계에서 요양 기관 검색 API")
    @GetMapping("/for-guest/search")
    public ResponseEntity<NursingInstitutionSearchResponse> searchNursingInstitution(
            @RequestParam(required = false) String nursingInstitutionName) {
        NursingInstitutionSearchResponse response =
                nursingInstitutionService.searchNursingInstitutionByName(nursingInstitutionName);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "회원가입 전: 서비스에 등록된 요양 기관 리스트 조회", description = "회원가입 단계에서 요양 기관 조회 API")
    @GetMapping("/for-guest/list")
    public ResponseEntity<NursingInstitutionSearchResponse> getNursingInstitutionList() {
        NursingInstitutionSearchResponse response = nursingInstitutionService.getNursingInstitutionList();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "요양 기관 등록 전: 기관 검색 (대표,센터장 전용)", description = "기관 등록 과정에서 기관이 서비스에 이미 등록되어있는지 확인하는 API")
    @GetMapping("/for-guest/check/already-register")
    public ResponseEntity<Boolean> checkAlreadyRegister(@RequestParam(required = true) String nursingInstitutionCode) {
        if (nursingInstitutionCode == null || nursingInstitutionCode.isBlank()) {
            throw new NursingInstitutionException(NURSING_INSTITUTION_REQUIRE_CODE);
        }
        Boolean isRegister = nursingInstitutionService.existsByNameAndCode(nursingInstitutionCode);
        return ResponseEntity.ok(isRegister);
    }

    @Operation(summary = "요양 기관 프로필 사진 업로드(대표,센터장 전용)", description = "요양기관 프로필 이미지 저장 API.")
    @PostMapping(value = "/upload-profile-img", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<NursingInstitutionProfileUploadResponse> uploadProfileImg(
            @RequestPart MultipartFile file, @RequestPart String institutionName) {

        var response = nursingInstitutionService.uploadProfileImage(file, institutionName);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "요양 기관 등록", description = "요양 기관이 등록 되지 않았으면 등록 요청")
    @PostMapping("/for-guest/register")
    public ResponseEntity<Void> createNursingInstitution(@Valid @RequestBody NursingInstitutionCreateRequest request) {
        Long id = nursingInstitutionService.saveNursingInstitution(request);
        return ResponseEntity.created(URI.create("nursingInstitution/" + id)).build();
    }

    @Hidden
    // @Operation(summary = "요양 기관 조회", description = "요양 기관이 이미 DB에 등록된 기관인지 확인")
    @GetMapping("/exists")
    public ResponseEntity<Boolean> checkExistingNursingInstitution() {
        boolean exists = nursingInstitutionService.existsById();
        return ResponseEntity.ok(exists);
    }
}
