package com.becareful.becarefulserver.domain.community.domain;

import com.becareful.becarefulserver.domain.common.domain.BaseEntity;
import com.becareful.becarefulserver.domain.socialworker.domain.SocialWorker;
import com.becareful.becarefulserver.global.exception.exception.PostException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static com.becareful.becarefulserver.global.exception.ErrorMessage.POST_NOT_UPDATABLE;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post  extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long id;

    @Column(name = "post_title")
    private String title;

    @Column(name = "post_content")
    private String content;

    @Column(name = "post_is_important")
    private boolean isImportant;

    @JoinColumn(name = "board_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private PostBoard board;

    @JoinColumn(name = "social_worker_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private SocialWorker socialworker;

    @Builder
    private Post(String title, String content, boolean isImportant, PostBoard board, SocialWorker socialworker) {
        this.title = title;
        this.content = content;
        this.isImportant = isImportant;
        this.board = board;
        this.socialworker = socialworker;
    }

    public static Post create(String title, String content, boolean isImportant, PostBoard board, SocialWorker socialWorker) {
        return Post.builder().
                title(title).
                content(content).
                isImportant(isImportant).
                board(board).
                socialworker(socialWorker).
                build();
    }

    public void validateAuthor(SocialWorker currentMember) {
        if (!this.socialworker.getId().equals(currentMember.getId())) {
            throw new PostException(POST_NOT_UPDATABLE);
        }
    }

    /**
     * Data Update 로직
     */

    public void update(String title, String content, boolean isImportant) {
        this.title = title;
        this.content = content;
        this.isImportant = isImportant;
    }
}
