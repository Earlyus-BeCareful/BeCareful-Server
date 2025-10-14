package com.becareful.becarefulserver.domain.chat.dto;

import static com.becareful.becarefulserver.global.exception.ErrorMessage.CHAT_MESSAGE_SENDER_NOT_EXISTS;

import com.becareful.becarefulserver.domain.caregiver.domain.Caregiver;
import com.becareful.becarefulserver.domain.chat.domain.ChatMessage;
import com.becareful.becarefulserver.domain.matching.domain.Contract;
import com.becareful.becarefulserver.domain.nursing_institution.domain.NursingInstitution;
import com.becareful.becarefulserver.global.exception.exception.ChatException;
import java.time.LocalDateTime;

public record ChatMessageDto(
        String senderName,
        String senderProfileImageUrl,
        LocalDateTime sendDateTime,
        String content,
        ContractDto contractInfo) {
    public static ChatMessageDto from(ChatMessage message) {
        String senderName;
        String senderProfileImageUrl;

        if (message.getCaregiver() != null) {
            Caregiver caregiver = message.getCaregiver();
            senderName = caregiver.getName();
            senderProfileImageUrl = caregiver.getProfileImageUrl();
        } else if (message.getSocialWorker() != null) {
            NursingInstitution institution = message.getSocialWorker().getNursingInstitution();
            senderName = institution.getName();
            senderProfileImageUrl = institution.getProfileImageUrl();
        } else {
            throw new ChatException(CHAT_MESSAGE_SENDER_NOT_EXISTS);
        }

        Contract contract = message.getContract();

        return new ChatMessageDto(
                senderName,
                senderProfileImageUrl,
                message.getCreateDate(),
                message.getContent(),
                contract == null ? null : ContractDto.from(message.getContract()));
    }
}
