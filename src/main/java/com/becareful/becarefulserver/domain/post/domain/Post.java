package com.becareful.becarefulserver.domain.post.domain;

import com.becareful.becarefulserver.domain.socialworker.domain.Socialworker;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long id;

    @Column(name = "post_title")
    private String title;

    @Column(name = "post_is_important")
    private boolean isImportant;

    @JoinColumn(name = "board_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private PostBoard board;

    @JoinColumn(name = "social_worker_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Socialworker socialworker;
}
