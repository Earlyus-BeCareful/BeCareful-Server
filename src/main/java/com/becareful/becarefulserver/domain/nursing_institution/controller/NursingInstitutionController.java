package com.becareful.becarefulserver.domain.nursing_institution.controller;

import static com.becareful.becarefulserver.global.exception.ErrorMessage.*;

import com.becareful.becarefulserver.domain.common.dto.request.*;
import com.becareful.becarefulserver.domain.common.dto.response.*;
import com.becareful.becarefulserver.domain.nursing_institution.dto.InstitutionSimpleDto;
import com.becareful.becarefulserver.domain.nursing_institution.dto.request.*;
import com.becareful.becarefulserver.domain.nursing_institution.dto.response.*;
import com.becareful.becarefulserver.domain.nursing_institution.service.*;
import com.becareful.becarefulserver.global.exception.exception.*;
import io.swagger.v3.oas.annotations.*;
import io.swagger.v3.oas.annotations.tags.*;
import jakarta.validation.*;
import java.net.*;
import java.util.List;
import lombok.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/nursingInstitution") // TODO : url 변경 -> nursing-institution
@Tag(name = "Nursing Institution", description = "요양기관 관련 API 입니다.")
public class NursingInstitutionController {
    private final NursingInstitutionService nursingInstitutionService;

    @Operation(summary = "서비스에 등록된 요양 기관 검색", description = "서비스에 등록된 요양 기관 검색 API")
    @GetMapping("/search")
    public ResponseEntity<List<InstitutionSimpleDto>> searchNursingInstitution(
            @RequestParam(required = false) String nursingInstitutionName) {
        var response = nursingInstitutionService.searchNursingInstitutionByName(nursingInstitutionName);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "서비스에 등록된 요양 기관 리스트 조회", description = "서비스에 등록된 모든 요양 기관 반환 API")
    @GetMapping("/list")
    public ResponseEntity<List<InstitutionSimpleDto>> getNursingInstitutionList() {
        var response = nursingInstitutionService.getNursingInstitutionList();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "요양 기관 등록 전: 기관 검색 (대표,센터장 전용)", description = "기관 등록 과정에서 기관이 서비스에 이미 등록되어있는지 확인하는 API")
    @GetMapping("/for-guest/check/already-register") // TODO : url 수정 : /exist
    public ResponseEntity<Boolean> checkAlreadyRegister(@RequestParam(required = true) String nursingInstitutionCode) {
        if (nursingInstitutionCode == null || nursingInstitutionCode.isBlank()) {
            throw new NursingInstitutionException(NURSING_INSTITUTION_REQUIRE_CODE);
        }
        Boolean isRegister = nursingInstitutionService.existsByNameAndCode(nursingInstitutionCode);
        return ResponseEntity.ok(isRegister);
    }

    @Deprecated
    @Operation(summary = "(구버전.삭제예정)요양 기관 프로필 사진 업로드(대표,센터장 전용)", description = "요양기관 프로필 이미지 저장 API.")
    @PostMapping(value = "/upload-profile-img", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<NursingInstitutionProfileUploadResponse> uploadProfileImg(
            @RequestPart MultipartFile file, @RequestPart String institutionName) {

        var response = nursingInstitutionService.uploadProfileImage(file, institutionName);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "이미지 업로드 (신버전): 요양기관 프로필 이미지 업로드용 Presigned URL 발급",
            description =
                    """
    프론트엔드에서 사용자가 로컬 파일을 선택할 때마다 이 API를 호출해 Presigned URL을 발급받습니다.
    발급받은 URL로 S3에 이미지를 직접 업로드합니다.
    이후 기관 등록 또는 정보 수정 시, S3에 업로드한 파일의 tempKey를 백엔드로 전달해야 합니다.
    """)
    @PostMapping("/profile-img/presigned-url")
    public ResponseEntity<PresignedUrlResponse> createPresignedUrl(PresignedUrlRequest request) {
        PresignedUrlResponse response = nursingInstitutionService.getPresignedUrl(request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "요양 기관 등록", description = "요양 기관이 등록 되지 않았으면 등록 요청")
    @PostMapping("/for-guest/register")
    public ResponseEntity<Void> createNursingInstitution(@Valid @RequestBody NursingInstitutionCreateRequest request) {
        Long id = nursingInstitutionService.saveNursingInstitution(request);
        return ResponseEntity.created(URI.create("nursingInstitution/" + id)).build();
    }

    @Operation(summary = "요양 기관 수정")
    @PutMapping("/info")
    public ResponseEntity<Void> updateNursingInstitution(
            @Valid @RequestBody UpdateNursingInstitutionInfoRequest request) {
        nursingInstitutionService.UpdateNursingInstitutionInfo(request);
        return ResponseEntity.ok().build();
    }

    @Hidden
    // @Operation(summary = "요양 기관 조회", description = "요양 기관이 이미 DB에 등록된 기관인지 확인")
    @GetMapping("/exists")
    public ResponseEntity<Boolean> checkExistingNursingInstitution() {
        boolean exists = nursingInstitutionService.existsById();
        return ResponseEntity.ok(exists);
    }
}
