package com.becareful.becarefulserver.domain.report.domain;

import com.becareful.becarefulserver.domain.association.domain.AssociationMember;
import com.becareful.becarefulserver.domain.caregiver.domain.Caregiver;
import com.becareful.becarefulserver.domain.chat.domain.ChatRoom;
import com.becareful.becarefulserver.domain.common.domain.BaseEntity;
import com.becareful.becarefulserver.domain.community.domain.Comment;
import com.becareful.becarefulserver.domain.community.domain.Post;
import com.becareful.becarefulserver.domain.socialworker.domain.SocialWorker;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
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

    @JoinColumn(name = "reporter_caregiver_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Caregiver caregiver;

    @JoinColumn(name = "reporter_social_worker_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private SocialWorker socialWorker;

    @JoinColumn(name = "reporter_association_member_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private AssociationMember associationMember;

    @Builder(access = AccessLevel.PRIVATE)
    public Report(
            ReportType reportType,
            String description,
            Post post,
            Comment comment,
            ChatRoom chatRoom,
            Caregiver caregiver,
            SocialWorker socialWorker,
            AssociationMember associationMember) {
        this.reportType = reportType;
        this.description = description;
        this.post = post;
        this.comment = comment;
        this.chatRoom = chatRoom;
        this.caregiver = caregiver;
        this.socialWorker = socialWorker;
        this.associationMember = associationMember;
    }

    public static Report post(
            ReportType reportType, String description, AssociationMember associationMember, Post post) {
        return Report.builder()
                .reportType(reportType)
                .description(description)
                .associationMember(associationMember)
                .post(post)
                .build();
    }

    public static Report comment(
            ReportType reportType, String description, AssociationMember associationMember, Comment comment) {
        return Report.builder()
                .reportType(reportType)
                .description(description)
                .associationMember(associationMember)
                .comment(comment)
                .build();
    }

    public static Report chatRoomSocialWorker(
            ReportType reportType, String description, SocialWorker socialWorker, ChatRoom chatRoom) {
        return Report.builder()
                .reportType(reportType)
                .description(description)
                .socialWorker(socialWorker)
                .chatRoom(chatRoom)
                .build();
    }

    public static Report chatRoomCaregiver(
            ReportType reportType, String description, Caregiver caregiver, ChatRoom chatRoom) {
        return Report.builder()
                .reportType(reportType)
                .description(description)
                .caregiver(caregiver)
                .chatRoom(chatRoom)
                .build();
    }
}
