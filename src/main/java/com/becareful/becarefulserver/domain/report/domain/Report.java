package com.becareful.becarefulserver.domain.report.domain;

import com.becareful.becarefulserver.domain.chat.domain.ChatRoom;
import com.becareful.becarefulserver.domain.common.domain.BaseEntity;
import com.becareful.becarefulserver.domain.community.domain.Comment;
import com.becareful.becarefulserver.domain.community.domain.Post;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Report extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "report_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "report_type", nullable = false)
    private ReportType reportType;

    @Column(name = "report_description", length = 500)
    private String description;

    @JoinColumn(name = "reported_post_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Post post;

    @JoinColumn(name = "reported_comment_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Comment comment;

    @JoinColumn(name = "reported_chat_room_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private ChatRoom chatRoom;
}
