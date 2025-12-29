package com.becareful.becarefulserver.domain.report.service;

import static com.becareful.becarefulserver.global.exception.ErrorMessage.*;

import com.becareful.becarefulserver.domain.association.domain.AssociationMember;
import com.becareful.becarefulserver.domain.caregiver.domain.Caregiver;
import com.becareful.becarefulserver.domain.chat.domain.ChatRoom;
import com.becareful.becarefulserver.domain.chat.repository.ChatRoomRepository;
import com.becareful.becarefulserver.domain.community.domain.Comment;
import com.becareful.becarefulserver.domain.community.domain.Post;
import com.becareful.becarefulserver.domain.community.repository.CommentRepository;
import com.becareful.becarefulserver.domain.community.repository.PostRepository;
import com.becareful.becarefulserver.domain.report.domain.Report;
import com.becareful.becarefulserver.domain.report.dto.request.ReportCreateRequest;
import com.becareful.becarefulserver.domain.report.repository.ReportRepository;
import com.becareful.becarefulserver.domain.socialworker.domain.SocialWorker;
import com.becareful.becarefulserver.global.exception.exception.DomainException;
import com.becareful.becarefulserver.global.util.AuthUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReportRepository reportRepository;
    private final PostRepository postRepository;
    private final AuthUtil authUtil;
    private final CommentRepository commentRepository;
    private final ChatRoomRepository chatRoomRepository;

    @Transactional
    public void reportChatRoomByCaregiver(Long chatRoomId, ReportCreateRequest request) {
        Caregiver caregiver = authUtil.getLoggedInCaregiver();

        ChatRoom chatRoom =
                chatRoomRepository.findById(chatRoomId).orElseThrow(() -> new DomainException(CHAT_ROOM_NOT_EXISTS));

        // TODO : 채팅방 검증 필요 - 채팅방에서 요양보호사를 알아올 수 없는 문제가 있음

        Report report = Report.chatRoomCaregiver(request.reportType(), request.description(), caregiver, chatRoom);

        reportRepository.save(report);
    }

    @Transactional
    public void reportChatRoomBySocialWorker(Long chatRoomId, ReportCreateRequest request) {
        SocialWorker loggedInSocialWorker = authUtil.getLoggedInSocialWorker();

        ChatRoom chatRoom =
                chatRoomRepository.findById(chatRoomId).orElseThrow(() -> new DomainException(CHAT_ROOM_NOT_EXISTS));

        // TODO : 채팅방 검증 필요 - 채팅방에서 요양보호사를 알아올 수 없는 문제가 있음

        Report report = Report.chatRoomSocialWorker(
                request.reportType(), request.description(), loggedInSocialWorker, chatRoom);

        reportRepository.save(report);
    }

    @Transactional
    public void reportPost(String boardType, Long postId, ReportCreateRequest request) {
        AssociationMember associationMember = authUtil.getLoggedInAssociationMember();

        Post post = postRepository.findById(postId).orElseThrow(() -> new DomainException(POST_NOT_FOUND));

        // TODO : Post 접근 권한 검증

        Report report = Report.post(request.reportType(), request.description(), associationMember, post);

        reportRepository.save(report);
    }

    @Transactional
    public void reportComment(String boardType, Long postId, Long commentId, ReportCreateRequest request) {
        AssociationMember associationMember = authUtil.getLoggedInAssociationMember();

        Comment comment =
                commentRepository.findById(commentId).orElseThrow(() -> new DomainException(COMMENT_NOT_FOUND));

        // TODO : Comment 접근 권한 검증

        Report report = Report.comment(request.reportType(), request.description(), associationMember, comment);

        reportRepository.save(report);
    }
}
