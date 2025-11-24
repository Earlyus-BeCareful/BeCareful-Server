package com.becareful.becarefulserver.fixture;

import com.becareful.becarefulserver.domain.community.domain.BoardType;
import com.becareful.becarefulserver.domain.community.domain.PostBoard;
import com.becareful.becarefulserver.domain.association.domain.AssociationRank;

public class PostBoardFixture {

    public static final PostBoard 협회공지 = PostBoard.create(
            BoardType.ASSOCIATION_NOTICE,
            AssociationRank.MEMBER,
            AssociationRank.MEMBER,
            AssociationFixture.JEONJU_ASSOCIATION);
}
