package com.becareful.becarefulserver.domain.community.domain;

import static com.becareful.becarefulserver.global.exception.ErrorMessage.COMMENT_NOT_FOUND;
import static com.becareful.becarefulserver.global.exception.ErrorMessage.COMMENT_NOT_UPDATABLE;

import com.becareful.becarefulserver.domain.common.domain.BaseEntity;
import com.becareful.becarefulserver.domain.socialworker.domain.SocialWorker;
import com.becareful.becarefulserver.global.exception.exception.CommentException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long id;

    @Column(name = "comment_content")
    private String content;

    @JoinColumn(name = "social_worker_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private SocialWorker author;

    @JoinColumn(name = "post_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Post post;

    @Builder(access = AccessLevel.PRIVATE)
    private Comment(String content, SocialWorker author, Post post) {
        this.content = content;
        this.author = author;
        this.post = post;
    }

    public static Comment create(String content, SocialWorker author, Post post) {
        return Comment.builder().content(content).author(author).post(post).build();
    }

    public void update(String content) {
        this.content = content;
    }

    public void validateAuthor(SocialWorker currentMember) {
        if (!this.author.getId().equals(currentMember.getId())) {
            throw new CommentException(COMMENT_NOT_UPDATABLE);
        }
    }

    public void validatePost(Post post) {
        if (!this.post.getId().equals(post.getId())) {
            throw new CommentException(COMMENT_NOT_FOUND);
        }
    }
}
