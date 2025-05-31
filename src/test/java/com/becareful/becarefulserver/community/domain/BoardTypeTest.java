package com.becareful.becarefulserver.community.domain;

import com.becareful.becarefulserver.domain.community.domain.BoardType;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class BoardTypeTest {

    @Test
    public void url에_작성된_board_type을_enum타입_형식으로_변환에_성공한다() {
        assertEquals(BoardType.ASSOCIATION_NOTICE, BoardType.fromUrlBoardType("association-notice"));
        assertEquals(BoardType.SERVICE_NOTICE, BoardType.fromUrlBoardType("service-notice"));
        assertEquals(BoardType.INFORMATION_SHARING, BoardType.fromUrlBoardType("information-sharing"));
        assertEquals(BoardType.PARTICIPATION_APPLICATION, BoardType.fromUrlBoardType("participation-application"));
        assertNull(BoardType.fromUrlBoardType("invalid-board-type"));
    }
}
