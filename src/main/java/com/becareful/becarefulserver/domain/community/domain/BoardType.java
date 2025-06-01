package com.becareful.becarefulserver.domain.community.domain;

public enum BoardType {
    /**
     * ASSOCIATION = 협회 (각 기관장이 속한 협회)
     * INSTITUTION = 기관 (사회복지사가 소속된 기관)
     * SERVICE = 공단 (건강보험공단)
     **/
    ASSOCIATION_NOTICE,
    SERVICE_NOTICE,
    INFORMATION_SHARING,
    PARTICIPATION_APPLICATION;

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
