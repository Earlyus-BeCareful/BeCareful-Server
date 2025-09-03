package com.becareful.becarefulserver.domain.community.dto.response;

import com.becareful.becarefulserver.domain.community.domain.Comment;
import com.becareful.becarefulserver.domain.community.dto.AuthorSimpleDto;
import java.time.LocalDateTime;

public record CommentResponse(
        Long commentId, String content, LocalDateTime createdAt, AuthorSimpleDto author, Boolean isMyComment) {
    public static CommentResponse of(Comment comment, Boolean isMyComment) {
        return new CommentResponse(
                comment.getId(),
                comment.getContent(),
                comment.getCreateDate(), // TODO : 작성 시간 vs 수정 시간 확인 필요
                AuthorSimpleDto.from(comment.getAuthor()),
                isMyComment);
    }
}
