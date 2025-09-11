package com.becareful.becarefulserver.domain.socialworker.controller;

import com.becareful.becarefulserver.domain.socialworker.dto.request.ElderlyCreateRequest;
import com.becareful.becarefulserver.domain.socialworker.dto.request.ElderlyUpdateRequest;
import com.becareful.becarefulserver.domain.socialworker.dto.response.ElderlyInfoResponse;
import com.becareful.becarefulserver.domain.socialworker.dto.response.ElderlyProfileUploadResponse;
import com.becareful.becarefulserver.domain.socialworker.service.ElderlyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
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

    @Operation(summary = "어르신 목록 조회", description = "요양기관의 어르신 목록을 반환합니다.")
    @GetMapping("/list")
    public ResponseEntity<List<ElderlyInfoResponse>> getElderlyList() {
        var response = elderlyService.getElderlyList();
        return ResponseEntity.ok(response);
    }

    // 어르신 목록 - 성함, 나이, 성별, 프로필, 요양등급, 요양보호자 수, 매칭 중인지(공고 진행중인거), 검색어 입력
    @Operation(summary = "어르신 검색", description = "검색어를 입력하면 해당 이름을 포함한 어르신 목록을 반환합니다.")
    @GetMapping("/search")
    public ResponseEntity<List<ElderlyInfoResponse>> getElderlyListBySearch(
            @Parameter(name = "searchString", description = "검색어 (어르신 이름 일부 또는 전체)", example = "홍길동")
                    @RequestParam(required = false)
                    String searchString) {
        var response = elderlyService.getElderlyListBySearch(searchString);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "어르신 등록")
    @PostMapping("/register") // TODO : URL 변경 (register 제거)
    public ResponseEntity<Void> createElderly(@Valid @RequestBody ElderlyCreateRequest request) {
        Long id = elderlyService.saveElderly(request);
        return ResponseEntity.created(URI.create("/elderly/" + id)).build();
    }

    // 어르신 수정
    @Operation(summary = "어르신 정보 수정", description = "어르신 정보를 수정하는 API 입니다. 프로필이미지를 기본 이미지로 업데이트 시 반드시 공백 uri을 넣어주세요.")
    @PatchMapping("/update/{elderlyId}") // TODO : URL 변경 (update 제거, PATCH -> PUT)
    public ResponseEntity<Void> updateElderly(
            @PathVariable Long elderlyId, @Valid @RequestBody ElderlyUpdateRequest request) {
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
