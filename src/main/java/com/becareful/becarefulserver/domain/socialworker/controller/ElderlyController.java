package com.becareful.becarefulserver.domain.socialworker.controller;

import com.becareful.becarefulserver.domain.matching.dto.ElderlySimpleDto;
import com.becareful.becarefulserver.domain.socialworker.dto.request.ElderlyCreateOrUpdateRequest;
import com.becareful.becarefulserver.domain.socialworker.dto.response.ElderlyDetailResponse;
import com.becareful.becarefulserver.domain.socialworker.dto.response.ElderlyProfileUploadResponse;
import com.becareful.becarefulserver.domain.socialworker.service.ElderlyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
    public ResponseEntity<Void> createElderly(@Valid @RequestBody ElderlyCreateOrUpdateRequest request) {
        Long id = elderlyService.saveElderly(request);
        return ResponseEntity.created(URI.create("/elderly/" + id)).build();
    }

    @Operation(
            summary = "어르신 정보 수정",
            description = "3.3.4 어르신 정보를 수정하는 API 입니다. 프로필이미지를 기본 이미지로 업데이트 시 반드시 공백 uri을 넣어주세요.")
    @PutMapping("/{elderlyId}")
    public ResponseEntity<Void> updateElderly(
            @PathVariable Long elderlyId, @Valid @RequestBody ElderlyCreateOrUpdateRequest request) {
        elderlyService.updateElderly(elderlyId, request);
        return ResponseEntity.ok().build();
    }

    // 어르신 상세 - 생년월일, 나이, 거주지 상세 주소, 동거인, 동물, 건강상태, 요양등급, 케어필요항목, 담당 보호사(프로필, 성함, 요일, 시간, 케어항목), 모집중인 공고(요일, 시간, 케어항목)

    @Operation(summary = "어르신 프로필 사진 업로드", description = "어르신 등록, 수정 시 사용하는 프로필 이미지 업로드 API 입니다.")
    @PostMapping(value = "/upload-profile-img", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ElderlyProfileUploadResponse> uploadProfileImg(@RequestPart MultipartFile file) {
        var response = elderlyService.uploadProfileImage(file);
        return ResponseEntity.ok(response);
    }
}
