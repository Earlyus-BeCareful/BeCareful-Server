package com.becareful.becarefulserver.domain.socialworker.controller;

import com.becareful.becarefulserver.domain.caregiver.dto.response.CaregiverProfileUploadResponse;
import com.becareful.becarefulserver.domain.socialworker.dto.request.ElderlyCreateRequest;
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
@Tag(name = "Elderly", description = "어르신 관련 API 입니다.")
public class ElderlyController {
    private final ElderlyService elderlyService;

    @Operation(summary = "어르신 등록", description = "요양 기관 ID 필수")
    @PostMapping("/register")
    public ResponseEntity<Void> createElderly(@Valid @RequestBody ElderlyCreateRequest request){
        Long id = elderlyService.saveElderly(request);
        return ResponseEntity.created(URI.create("/elderly/" + id)).build();
    }

    @Operation(summary = "어르신 프로필 사진 신규 업로드", description = "어르신 등록시 사용하는 프로필 이미지 업로드 API 입니다.")
    @PostMapping(value = "/upload-profile-img", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ElderlyProfileUploadResponse> uploadProfileImg(
            @RequestPart MultipartFile file,
            @RequestPart String phoneNumber) {
        var response = elderlyService.uploadProfileImage(file, phoneNumber);
        return ResponseEntity.ok(response);
    }
}
