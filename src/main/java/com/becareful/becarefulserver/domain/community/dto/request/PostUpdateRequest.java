package com.becareful.becarefulserver.domain.community.dto.request;

import com.becareful.becarefulserver.domain.community.dto.*;
import java.util.*;

public record PostUpdateRequest(
        String title,
        String content,
        List<Long> deleteMediaIdList,
        boolean isImportant,
        String originalUrl,
        List<MediaInfoDto> imageList,
        List<MediaInfoDto> videoList,
        List<MediaInfoDto> fileList) {}
