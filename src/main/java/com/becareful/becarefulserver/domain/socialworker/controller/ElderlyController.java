package com.becareful.becarefulserver.domain.socialworker.controller;

import com.becareful.becarefulserver.domain.socialworker.dto.request.ElderlyCreateRequest;
import com.becareful.becarefulserver.domain.socialworker.dto.request.ElderlyUpdateRequest;
import com.becareful.becarefulserver.domain.socialworker.dto.response.ElderlyProfileUploadResponse;
import com.becareful.becarefulserver.domain.socialworker.service.ElderlyService;
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
@RequestMapping("/elderly")
@Tag(name = "Elderly", description = "어르신 관련 API 입니다.") //상세 주소 포함하기
public class ElderlyController {
    private final ElderlyService elderlyService;
    @Operation(summary = "어르신 등록")
    @PostMapping("/register")
    public ResponseEntity<Void> createElderly(@Valid @RequestBody ElderlyCreateRequest request){
        Long id = elderlyService.saveElderly(request);
        return ResponseEntity.created(URI.create("/elderly/" + id)).build();
    }
    //어르신 수정
    @Operation(summary = "어르신 정보 수정", description = "어르신 정보를 수정하는 API 입니다. 프로필이미지를 기본 이미지로 업데이트 시 반드시 공백 uri을 넣어주세요.")
    @PatchMapping("/update/{elderlyId}")
    public ResponseEntity<Void> updateElderly(
            @PathVariable Long elderlyId,
            @Valid @RequestBody ElderlyUpdateRequest request) {
        elderlyService.updateElderly(elderlyId, request);
        return ResponseEntity.noContent().build();
    }

    //어르신 목록 - 성함, 나이, 성별, 프로필, 요양등급, 요양보호자 수, 매칭 중인지(이게 정확히 무슨 의미지),

    //어르신 상세 - 생년월일, 나이, 거주지 상세 주소, 동거인, 동물, 건강상태, 요양등급, 케어필요항목, 담당 보호사(프로필, 성함, 요일, 시간, 케어항목), 모집중인 공고(요일, 시간, 케어항목)

    @Operation(summary = "어르신 프로필 사진 업로드", description = "어르신 등록, 수정 시 사용하는 프로필 이미지 업로드 API 입니다.")
    @PostMapping(value = "/upload-profile-img", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ElderlyProfileUploadResponse> uploadProfileImg(
            @RequestPart MultipartFile file,
            @RequestPart String institutionId) {
        var response = elderlyService.uploadProfileImage(file, institutionId);
        return ResponseEntity.ok(response);
    }
}
