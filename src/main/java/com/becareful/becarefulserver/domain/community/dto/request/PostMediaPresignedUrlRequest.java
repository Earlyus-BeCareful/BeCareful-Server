package com.becareful.becarefulserver.domain.community.dto.request;

import com.becareful.becarefulserver.domain.community.domain.*;
import jakarta.validation.constraints.*;

public record PostMediaPresignedUrlRequest(
        @NotBlank String fileName,
        @NotNull Integer fileSize,
        @NotBlank String contentType, //확장자
        @NotNull FileType fileType, //'FILE' | 'IMAGE' | 'VIDEO'
        Integer videoDuration) {}
