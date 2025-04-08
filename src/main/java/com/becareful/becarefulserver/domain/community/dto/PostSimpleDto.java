package com.becareful.becarefulserver.domain.community.dto;

import com.becareful.becarefulserver.domain.community.domain.Post;
import com.becareful.becarefulserver.domain.socialworker.domain.SocialWorker;
import com.becareful.becarefulserver.domain.socialworker.domain.vo.Rank;

import java.time.format.DateTimeFormatter;

public record PostSimpleDto(
        Long postId,
        String title,
        boolean isImportant,
        String thumbnailUrl,
        String createdAt,
        String nickname,
        Rank rank,
        String institutionImageUrl
) {
    public static PostSimpleDto of(Post post, SocialWorker socialWorker) {
        return new PostSimpleDto(
                post.getId(),
                post.getTitle(),
                post.isImportant(),
                "", // TODO : 이미지 관련 연관관계 설정 후 작업
                post.getCreateDate().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                socialWorker.getName(), // TODO : 닉네임 필드로 수정
                socialWorker.getInstitutionRank(),
                socialWorker.getNursingInstitution().getProfileImageUrl()
        );
    }
}
