package com.becareful.becarefulserver.domain.community.dto.request;

import com.becareful.becarefulserver.domain.community.domain.*;
import jakarta.validation.constraints.*;

public record PostMediaPresignedUrlRequest(
        @NotBlank String fileName,
        @NotNull Integer fileSize,
        @NotBlank String contentType,
        @NotNull FileType fileType,
        Integer videoDuration) {}
