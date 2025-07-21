package com.becareful.becarefulserver.domain.community.domain;

public enum BoardType {
    /** 게시판 종류 */
    ASSOCIATION_NOTICE, // 협회 공지 게시판
    SERVICE_NOTICE, // 공단 공지 게시판
    INFORMATION_SHARING, // 정보 공유 게시판
    PARTICIPATION_APPLICATION; // 행사/신청 게시판

    public static BoardType fromUrlBoardType(String boardType) {
        boardType = convertUrlBoardTypeToName(boardType);
        for (BoardType type : BoardType.values()) {
            if (type.name().equals(boardType)) {
                return type;
            }
        }
        return null;
    }

    private static String convertUrlBoardTypeToName(String boardType) {
        boardType = boardType.toUpperCase();
        String[] tokens = boardType.split("-");
        return String.join("_", tokens);
    }
}
