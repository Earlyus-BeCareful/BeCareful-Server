package com.becareful.becarefulserver.domain.chat.repository;

import com.becareful.becarefulserver.domain.chat.domain.*;
import com.becareful.becarefulserver.domain.chat.domain.vo.*;
import com.becareful.becarefulserver.domain.matching.domain.*;
import java.util.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    List<ChatRoom> findAllByChatRoomActiveStatusAndRecruitment(ChatRoomActiveStatus status, Recruitment recruitment);

    Iterable<ChatRoom> findAllByChatRoomActiveStatusAndRecruitmentId(
            ChatRoomActiveStatus chatRoomActiveStatus, Long recruitmentId);

    boolean existsByRecruitment(Recruitment recruitment);

    @Query(
            value = "SELECT r.id " + "FROM chat_room r "
                    + "JOIN caregiver_chat_read_status s ON r.id = s.chat_room_id "
                    + "WHERE r.recruitment_id = :recruitmentId "
                    + "AND s.care_giver_id = :caregiverId",
            nativeQuery = true)
    Long findByRecruitmentAndCaregiver(
            @Param("recruitmentId") Long recruitmentId, @Param("caregiverId") Long caregiverId);
}
