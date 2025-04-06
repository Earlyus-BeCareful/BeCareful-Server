package com.becareful.becarefulserver.domain.community.domain;

import com.becareful.becarefulserver.domain.common.domain.BaseEntity;
import com.becareful.becarefulserver.domain.socialworker.domain.SocialWorker;
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
    private SocialWorker socialworker;

    @JoinColumn(name = "post_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Post post;

    @Builder
    private Comment(String content, SocialWorker socialworker, Post post) {
        this.content = content;
        this.socialworker = socialworker;
        this.post = post;
    }

    public static Comment create(String content, SocialWorker socialworker, Post post) {
        return Comment.builder()
                .content(content)
                .socialworker(socialworker)
                .post(post)
                .build();
    }
}
