package com.becareful.becarefulserver.domain.community.domain;

import static com.becareful.becarefulserver.global.exception.ErrorMessage.POST_DIFFERENT_POST_BOARD;
import static com.becareful.becarefulserver.global.exception.ErrorMessage.POST_NOT_UPDATABLE;

import com.becareful.becarefulserver.domain.common.domain.BaseEntity;
import com.becareful.becarefulserver.domain.socialworker.domain.SocialWorker;
import com.becareful.becarefulserver.global.exception.exception.PostException;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post extends BaseEntity {

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
    private SocialWorker author;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostMedia> mediaList = new ArrayList<>();

    @Builder
    private Post(String title, String content, boolean isImportant, PostBoard board, SocialWorker author) {
        this.title = title;
        this.content = content;
        this.isImportant = isImportant;
        this.board = board;
        this.author = author;
    }

    public static Post create(
            String title, String content, boolean isImportant, PostBoard board, SocialWorker socialWorker) {
        return Post.builder()
                .title(title)
                .content(content)
                .isImportant(isImportant)
                .board(board)
                .author(socialWorker)
                .build();
    }

    public void validateAuthor(SocialWorker currentMember) {
        if (!this.author.getId().equals(currentMember.getId())) {
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

    /**
     * 검증 로직
     */
    public void validateBoard(Long boardId) {
        if (!this.board.getId().equals(boardId)) {
            throw new PostException(POST_DIFFERENT_POST_BOARD);
        }
    }

    /**
     * 미디어 추가 로직
     */
    public void addMedia(PostMedia media) {
        this.mediaList.add(media);
    }

    public List<PostMedia> getMediaListByType(FileType fileType) {
        return this.mediaList.stream()
                .filter(media -> media.getFileType() == fileType)
                .toList();
    }
}
