package com.becareful.becarefulserver.domain.chat.repository;

import com.becareful.becarefulserver.domain.chat.domain.Chat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRepository extends JpaRepository<Chat,Long> {

}
