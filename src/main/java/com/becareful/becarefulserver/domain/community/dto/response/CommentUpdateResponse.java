package com.becareful.becarefulserver.domain.community.dto.response;

import com.becareful.becarefulserver.domain.community.domain.Comment;
import java.time.LocalDateTime;

public record CommentUpdateResponse(LocalDateTime updatedAt) {
    public static CommentUpdateResponse from(Comment comment) {
        return new CommentUpdateResponse(comment.getUpdateDate());
    }
}
