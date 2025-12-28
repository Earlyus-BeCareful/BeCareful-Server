package com.becareful.becarefulserver.domain.report.service;

import static com.becareful.becarefulserver.global.exception.ErrorMessage.*;

import com.becareful.becarefulserver.domain.caregiver.domain.Caregiver;
import com.becareful.becarefulserver.domain.chat.domain.ChatRoom;
import com.becareful.becarefulserver.domain.chat.repository.ChatRoomRepository;
import com.becareful.becarefulserver.domain.community.repository.CommentRepository;
import com.becareful.becarefulserver.domain.community.repository.PostRepository;
import com.becareful.becarefulserver.domain.report.domain.Report;
import com.becareful.becarefulserver.domain.report.dto.request.ReportCreateRequest;
import com.becareful.becarefulserver.domain.report.repository.ReportRepository;
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
    public void reportChatRoom(Long chatRoomId, ReportCreateRequest request) {
        Caregiver caregiver = authUtil.getLoggedInCaregiver();

        ChatRoom chatRoom =
                chatRoomRepository.findById(chatRoomId).orElseThrow(() -> new DomainException(CHAT_ROOM_NOT_EXISTS));

        // TODO : 채팅방 검증 필요 - 채팅방에서 요양보호사를 알아올 수 없는 문제가 있음

        Report report = Report.chatRoomCaregiver(request.reportType(), request.description(), caregiver, chatRoom);

        reportRepository.save(report);
    }
}
