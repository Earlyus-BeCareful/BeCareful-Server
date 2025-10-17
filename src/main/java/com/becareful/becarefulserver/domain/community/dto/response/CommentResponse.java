package com.becareful.becarefulserver.domain.community.dto.response;

import com.becareful.becarefulserver.domain.community.domain.Comment;
import com.becareful.becarefulserver.domain.community.dto.AuthorSimpleDto;
import java.time.LocalDateTime;

public record CommentResponse(
        Long commentId,
        String content,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        AuthorSimpleDto author,
        Boolean isUpdated,
        Boolean isMyComment) {
    public static CommentResponse of(Comment comment, Boolean isMyComment) {
        return new CommentResponse(
                comment.getId(),
                comment.getContent(),
                comment.getCreateDate(),
                comment.getUpdateDate(),
                AuthorSimpleDto.from(comment.getAuthor()),
                !comment.getCreateDate().isEqual(comment.getUpdateDate()),
                isMyComment);
    }
}
