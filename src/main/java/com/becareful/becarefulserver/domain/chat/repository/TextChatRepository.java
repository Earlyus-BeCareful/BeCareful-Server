package com.becareful.becarefulserver.domain.chat.repository;

import com.becareful.becarefulserver.domain.chat.domain.TextChat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TextChatRepository extends JpaRepository<TextChat,Long> {
}
