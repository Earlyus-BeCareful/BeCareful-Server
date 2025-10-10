package com.becareful.becarefulserver.domain.socialworker.controller;

import com.becareful.becarefulserver.domain.common.dto.request.*;
import com.becareful.becarefulserver.domain.common.dto.response.*;
import com.becareful.becarefulserver.domain.matching.dto.*;
import com.becareful.becarefulserver.domain.socialworker.dto.request.*;
import com.becareful.becarefulserver.domain.socialworker.dto.response.*;
import com.becareful.becarefulserver.domain.socialworker.service.*;
import io.swagger.v3.oas.annotations.*;
import io.swagger.v3.oas.annotations.tags.*;
import jakarta.validation.*;
import java.net.*;
import lombok.*;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/elderly")
@Tag(name = "Elderly", description = "어르신 관련 API 입니다.") // 상세 주소 포함하기
public class ElderlyController {

    private final ElderlyService elderlyService;

    @Operation(summary = "어르신 목록 조회", description = "3.3.2 어르신 목록을 조회합니다.")
    @GetMapping("/list")
    public ResponseEntity<Page<ElderlySimpleDto>> getElderlyList(Pageable pageable) {
        var response = elderlyService.getElderlyList(pageable);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "어르신 검색", description = "3.3.2 검색어를 입력하면 해당 이름을 포함한 어르신 목록을 반환합니다.")
    @GetMapping("/search")
    public ResponseEntity<Page<ElderlySimpleDto>> searchElderly(
            @Parameter(name = "keyword", description = "어르신 이름", example = "홍길동") String keyword, Pageable pageable) {
        var response = elderlyService.searchElderly(keyword, pageable);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "어르신 상세 정보 조회", description = "어르신 상세 정보를 조회합니다. (3.2.1.2 공고 등록 어르신 상세 정보 조회)")
    @GetMapping("/{elderlyId}")
    public ResponseEntity<ElderlyDetailResponse> getElderlyDetail(@PathVariable Long elderlyId) {
        var response = elderlyService.getElderlyDetail(elderlyId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "어르신 등록", description = "3.3.1 어르신 등록")
    @PostMapping
    public ResponseEntity<Void> createElderly(@Valid @RequestBody ElderlyCreateRequest request) {
        Long id = elderlyService.saveElderly(request);
        return ResponseEntity.created(URI.create("/elderly/" + id)).build();
    }

    @Operation(summary = "어르신 정보 수정", description = "3.3.4 어르신 정보를 수정하는 API 입니다.")
    @PutMapping("/{elderlyId}")
    public ResponseEntity<Void> updateElderly(
            @PathVariable Long elderlyId, @Valid @RequestBody ElderlyUpdateRequest request) {
        elderlyService.updateElderly(elderlyId, request);
        return ResponseEntity.ok().build();
    }

    // 어르신 상세 - 생년월일, 나이, 거주지 상세 주소, 동거인, 동물, 건강상태, 요양등급, 케어필요항목, 담당 보호사(프로필, 성함, 요일, 시간, 케어항목), 모집중인 공고(요일, 시간, 케어항목)

    @Operation(summary = " (구버전, 삭제 예정)어르신 프로필 사진 업로드", description = "어르신 등록, 수정 시 사용하는 프로필 이미지 업로드 API 입니다.")
    @PostMapping(value = "/upload-profile-img", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ElderlyProfileUploadResponse> uploadProfileImg(@RequestPart MultipartFile file) {
        var response = elderlyService.uploadProfileImage(file);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "이미지 업로드 (신버전): 프로필 이미지 업로드용 Presigned URL 발급",
            description =
                    """
    프론트엔드에서 사용자가 로컬 파일을 선택할 때마다 이 API를 호출해 Presigned URL을 발급받습니다.
    발급받은 URL로 S3에 이미지를 직접 업로드합니다.
    이후 어르신등록 또는 정보 수정 시, S3에 업로드한 파일의 tempKey를 백엔드로 전달해야 합니다.
    """)
    @PostMapping("/profile-img/presigned-url")
    public ResponseEntity<PresignedUrlResponse> createPresignedUrl(PresignedUrlRequest request) {
        PresignedUrlResponse response = elderlyService.getPresignedUrl(request);
        return ResponseEntity.ok(response);
    }
}
