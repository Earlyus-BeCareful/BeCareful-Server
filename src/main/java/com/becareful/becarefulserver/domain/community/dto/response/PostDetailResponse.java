package com.becareful.becarefulserver.domain.community.dto.response;

import com.becareful.becarefulserver.domain.community.domain.Post;
import com.becareful.becarefulserver.domain.community.dto.AuthorSimpleDto;

import java.time.format.DateTimeFormatter;

public record PostDetailResponse(
        Long postId,
        String title,
        String content,
        boolean isImportant,
        boolean isEdited,
        String postedDate,
        AuthorSimpleDto author
) {
    public static PostDetailResponse from(Post post) {
        return new PostDetailResponse(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.isImportant(),
                !post.getCreateDate().isEqual(post.getUpdateDate()),
                post.getUpdateDate().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                AuthorSimpleDto.from(post.getAuthor())
        );
    }
}
