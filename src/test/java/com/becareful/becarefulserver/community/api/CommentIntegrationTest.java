package com.becareful.becarefulserver.community.api;

import static org.assertj.core.api.Assertions.assertThat;

import com.becareful.becarefulserver.common.IntegrationTest;
import com.becareful.becarefulserver.common.WithSocialWorker;
import com.becareful.becarefulserver.domain.community.domain.Post;
import com.becareful.becarefulserver.domain.community.domain.PostBoard;
import com.becareful.becarefulserver.domain.community.dto.request.CommentCreateRequest;
import com.becareful.becarefulserver.domain.community.dto.request.CommentUpdateRequest;
import com.becareful.becarefulserver.domain.community.repository.CommentRepository;
import com.becareful.becarefulserver.domain.community.repository.PostBoardRepository;
import com.becareful.becarefulserver.domain.community.repository.PostRepository;
import com.becareful.becarefulserver.domain.community.service.CommentService;
import com.becareful.becarefulserver.domain.common.vo.Gender;
import com.becareful.becarefulserver.domain.nursing_institution.vo.InstitutionRank;
import com.becareful.becarefulserver.domain.socialworker.domain.SocialWorker;
import com.becareful.becarefulserver.domain.socialworker.domain.vo.AssociationRank;
import com.becareful.becarefulserver.domain.socialworker.repository.SocialWorkerRepository;
import com.becareful.becarefulserver.fixture.AssociationFixture;
import com.becareful.becarefulserver.fixture.NursingInstitutionFixture;
import com.becareful.becarefulserver.fixture.PostBoardFixture;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class CommentIntegrationTest extends IntegrationTest {

    @Autowired private CommentService commentService;
    @Autowired private PostBoardRepository postBoardRepository;
    @Autowired private PostRepository postRepository;
    @Autowired private CommentRepository commentRepository;
    @Autowired private SocialWorkerRepository socialWorkerRepository;

    private SocialWorker createMember(String phone) {
        SocialWorker member =
                SocialWorker.create(
                        "name",
                        "nick",
                        LocalDate.now(),
                        Gender.FEMALE,
                        phone,
                        InstitutionRank.SOCIAL_WORKER,
                        AssociationRank.MEMBER,
                        true,
                        NursingInstitutionFixture.NURSING_INSTITUTION);
        member.joinAssociation(AssociationFixture.JEONJU_ASSOCIATION, AssociationRank.MEMBER);
        return socialWorkerRepository.save(member);
    }

    private PostBoard createBoard() {
        return postBoardRepository.save(PostBoardFixture.협회공지);
    }

    private Post createPost(PostBoard board, SocialWorker author) {
        return postRepository.save(Post.create("t", "c", false, null, board, author));
    }

    @Test
    @WithSocialWorker(phoneNumber = "01099999999")
    void 댓글_수정과_삭제가_성공한다() {
        SocialWorker member = createMember("01099999999");
        PostBoard board = createBoard();
        Post post = createPost(board, member);

        Long commentId =
                commentService.createComment(
                        "association-notice", post.getId(), new CommentCreateRequest("hello"));

        commentService.updateComment(
                "association-notice", post.getId(), commentId, new CommentUpdateRequest("hi"));

        assertThat(commentRepository.findById(commentId).get().getContent()).isEqualTo("hi");

        commentService.deleteComment("association-notice", post.getId(), commentId);

        assertThat(commentRepository.findById(commentId)).isEmpty();
    }
}

