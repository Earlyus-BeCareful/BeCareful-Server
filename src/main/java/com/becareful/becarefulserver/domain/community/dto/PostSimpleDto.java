package com.becareful.becarefulserver.domain.community.dto;

import com.becareful.becarefulserver.domain.community.domain.Post;
import java.time.format.DateTimeFormatter;

public record PostSimpleDto(
        Long postId,
        String title,
        boolean isImportant,
        String thumbnailUrl,
        String createdAt,
        AuthorSimpleDto author
) {
    public static PostSimpleDto from(Post post) {
        return new PostSimpleDto(
                post.getId(),
                post.getTitle(),
                post.isImportant(),
                "", // TODO : 이미지 관련 연관관계 설정 후 작업
                post.getCreateDate().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                AuthorSimpleDto.from(post.getAuthor())
        );
    }
}
