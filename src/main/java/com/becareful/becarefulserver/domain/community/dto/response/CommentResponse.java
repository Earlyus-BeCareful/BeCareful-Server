package com.becareful.becarefulserver.domain.community.dto.response;

import com.becareful.becarefulserver.domain.community.domain.Comment;
import com.becareful.becarefulserver.domain.community.dto.AuthorSimpleDto;
import java.time.LocalDateTime;

public record CommentResponse(String content, LocalDateTime createdAt, AuthorSimpleDto author) {
    public static CommentResponse from(Comment comment) {
        return new CommentResponse(
                comment.getContent(),
                comment.getCreateDate(), // TODO : 작성 시간 vs 수정 시간 확인 필요
                AuthorSimpleDto.from(comment.getAuthor()));
    }
}
