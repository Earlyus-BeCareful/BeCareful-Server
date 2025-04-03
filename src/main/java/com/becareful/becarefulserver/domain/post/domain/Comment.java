package com.becareful.becarefulserver.domain.post.domain;

import com.becareful.becarefulserver.domain.socialworker.domain.Socialworker;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long id;

    @Column(name = "comment_content")
    private String content;

    @JoinColumn(name = "social_worker_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Socialworker socialworker;

    @JoinColumn(name = "post_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Post post;
}
