package com.becareful.becarefulserver.fixture;

import com.becareful.becarefulserver.domain.community.domain.PostBoard;
import com.becareful.becarefulserver.domain.socialworker.domain.vo.Rank;

public class PostBoardFixture {

    public static final PostBoard 협회공지 = PostBoard.create("협회공지", Rank.MANAGER, Rank.MANAGER);
}
