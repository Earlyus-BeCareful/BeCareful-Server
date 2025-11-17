package com.becareful.becarefulserver.domain.matching.repository;

import com.becareful.becarefulserver.domain.chat.domain.ChatRoom;
import com.becareful.becarefulserver.domain.chat.domain.Contract;
import jakarta.validation.constraints.NotNull;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContractRepository extends JpaRepository<Contract, Long> {

    Optional<Contract> findTopByChatRoom(ChatRoom chatRoom);

    Optional<Contract> findLastContractByChatRoomId(@NotNull Long chatRoomId);

    Optional<Contract> findTopByChatRoomId(Long chatRoomId);
}
