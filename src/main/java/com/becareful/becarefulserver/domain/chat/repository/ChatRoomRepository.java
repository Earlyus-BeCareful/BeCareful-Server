package com.becareful.becarefulserver.domain.chat.repository;

import com.becareful.becarefulserver.domain.caregiver.domain.Caregiver;
import com.becareful.becarefulserver.domain.chat.domain.ChatRoom;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    @Query(
            """
        SELECT r
          FROM ChatRoom r
         WHERE r.matching.workApplication.caregiver = :caregiver
    """)
    List<ChatRoom> findAllByCaregiver(Caregiver caregiver);
}
