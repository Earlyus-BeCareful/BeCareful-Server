package com.becareful.becarefulserver.domain.community.dto.response;

import com.becareful.becarefulserver.domain.community.dto.PostSimpleDto;

import java.util.List;

public record PostWholeResponse(
        List<PostSimpleDto> importantNoticeList,
        List<PostSimpleDto> associationNoticeList,
        List<PostSimpleDto> careCorporationNoticeList
) {}
