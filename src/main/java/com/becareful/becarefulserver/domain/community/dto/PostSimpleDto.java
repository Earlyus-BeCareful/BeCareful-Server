package com.becareful.becarefulserver.domain.community.dto;

import static com.becareful.becarefulserver.domain.community.domain.FileType.IMAGE;

import com.becareful.becarefulserver.domain.community.domain.BoardType;
import com.becareful.becarefulserver.domain.community.domain.Post;
import com.becareful.becarefulserver.domain.community.domain.PostMedia;
import java.time.format.DateTimeFormatter;
import java.util.List;

public record PostSimpleDto(
        Long postId,
        String title,
        BoardType boardType,
        boolean isImportant,
        String thumbnailUrl,
        String createdAt,
        AuthorSimpleDto author) {
    public static PostSimpleDto from(Post post) {
        List<PostMedia> images = post.getMediaListByType(IMAGE);
        return new PostSimpleDto(
                post.getId(),
                post.getTitle(),
                post.getBoard().getBoardType(),
                post.isImportant(),
                images.isEmpty() ? null : images.get(0).getMediaUrl(),
                post.getCreateDate().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                AuthorSimpleDto.from(post.getAuthor()));
    }
}
