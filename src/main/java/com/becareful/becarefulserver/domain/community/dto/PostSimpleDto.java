package com.becareful.becarefulserver.domain.community.dto;

import com.becareful.becarefulserver.domain.community.domain.Post;
import com.becareful.becarefulserver.domain.socialworker.domain.SocialWorker;
import com.becareful.becarefulserver.domain.socialworker.domain.vo.Rank;

import java.time.format.DateTimeFormatter;

public record PostSimpleDto(
        String institutionImageUrl,
        String nickname,
        Rank rank,
        boolean isImportant,
        String title,
        String createdAt,
        String thumbnailUrl
) {
    public static PostSimpleDto of(Post post, SocialWorker socialWorker) {
        return new PostSimpleDto(
                socialWorker.getNursingInstitution().getProfileImageUrl(),
                socialWorker.getName(), // TODO : 닉네임 필드로 수정
                socialWorker.getInstitutionRank(),
                post.isImportant(),
                post.getTitle(),
                post.getCreateDate().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                "" // TODO : 이미지 관련 연관관계 설정 후 작업
        );
    }
}
