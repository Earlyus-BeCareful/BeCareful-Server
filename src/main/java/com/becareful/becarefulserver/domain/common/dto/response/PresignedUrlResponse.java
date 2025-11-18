package com.becareful.becarefulserver.domain.common.dto.response;

public record PresignedUrlResponse(String tempKey, String presignedUrl) {}
