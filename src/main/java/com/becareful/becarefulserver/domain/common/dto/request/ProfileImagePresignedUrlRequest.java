package com.becareful.becarefulserver.domain.common.dto.request;

import jakarta.validation.constraints.*;

public record ProfileImagePresignedUrlRequest(@NotBlank String fileName, @NotBlank String contentType) {}
