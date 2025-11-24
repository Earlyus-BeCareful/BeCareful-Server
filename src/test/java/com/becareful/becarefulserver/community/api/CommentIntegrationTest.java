package com.becareful.becarefulserver.community.api;

import static org.assertj.core.api.Assertions.assertThat;

import com.becareful.becarefulserver.common.IntegrationTest;
import com.becareful.becarefulserver.common.WithSocialWorker;
import com.becareful.becarefulserver.domain.association.domain.Association;
import com.becareful.becarefulserver.domain.association.domain.AssociationMember;
import com.becareful.becarefulserver.domain.association.repository.AssociationMemberRepository;
import com.becareful.becarefulserver.domain.association.repository.AssociationRepository;
import com.becareful.becarefulserver.domain.common.domain.Gender;
import com.becareful.becarefulserver.domain.community.domain.BoardType;
import com.becareful.becarefulserver.domain.community.domain.Post;
import com.becareful.becarefulserver.domain.community.domain.PostBoard;
import com.becareful.becarefulserver.domain.community.dto.request.CommentCreateRequest;
import com.becareful.becarefulserver.domain.community.dto.request.CommentUpdateRequest;
import com.becareful.becarefulserver.domain.community.repository.CommentRepository;
import com.becareful.becarefulserver.domain.community.repository.PostBoardRepository;
import com.becareful.becarefulserver.domain.community.repository.PostRepository;
import com.becareful.becarefulserver.domain.community.service.CommentService;
import com.becareful.becarefulserver.domain.nursing_institution.domain.vo.InstitutionRank;
import com.becareful.becarefulserver.domain.socialworker.domain.SocialWorker;
import com.becareful.becarefulserver.domain.association.domain.AssociationRank;
import com.becareful.becarefulserver.domain.socialworker.repository.SocialWorkerRepository;
import com.becareful.becarefulserver.fixture.NursingInstitutionFixture;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class CommentIntegrationTest extends IntegrationTest {

    @Autowired
    private CommentService commentService;

    @Autowired
    private PostBoardRepository postBoardRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private SocialWorkerRepository socialWorkerRepository;

    @Autowired
    private AssociationRepository associationRepository;

    @Autowired
    private AssociationMemberRepository associationMemberRepository;

    private AssociationMember createMember(String phone) {
        SocialWorker socialWorker = SocialWorker.create(
                "name",
                "nick",
                LocalDate.now(),
                Gender.FEMALE,
                phone,
                InstitutionRank.SOCIAL_WORKER,
                true,
                NursingInstitutionFixture.NURSING_INSTITUTION);
        Association association = associationRepository.findAll().get(0);
        AssociationMember member =
                AssociationMember.create(socialWorker, association, AssociationRank.MEMBER, true, true, true);
        socialWorker.joinAssociation(member);
        associationMemberRepository.save(member);
        socialWorkerRepository.save(socialWorker);
        return member;
    }

    private PostBoard createBoard() {
        Association association = associationRepository.findAll().get(0);
        PostBoard board = PostBoard.create(
                BoardType.ASSOCIATION_NOTICE, AssociationRank.MEMBER, AssociationRank.MEMBER, association);
        return postBoardRepository.save(board);
    }

    private Post createPost(PostBoard board, AssociationMember author) {
        return postRepository.save(Post.create("t", "c", false, null, board, author));
    }

    @Test
    @WithSocialWorker(phoneNumber = "01099999999")
    void 댓글_수정과_삭제가_성공한다() {
        AssociationMember member = createMember("01099999999");
        PostBoard board = createBoard();
        Post post = createPost(board, member);

        Long commentId =
                commentService.createComment("association-notice", post.getId(), new CommentCreateRequest("hello"));

        commentService.updateComment("association-notice", post.getId(), commentId, new CommentUpdateRequest("hi"));

        assertThat(commentRepository.findById(commentId).get().getContent()).isEqualTo("hi");

        commentService.deleteComment("association-notice", post.getId(), commentId);

        assertThat(commentRepository.findById(commentId)).isEmpty();
    }
}
