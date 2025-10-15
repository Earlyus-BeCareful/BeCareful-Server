package com.becareful.becarefulserver.community.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.becareful.becarefulserver.common.IntegrationTest;
import com.becareful.becarefulserver.common.WithSocialWorker;
import com.becareful.becarefulserver.domain.association.domain.Association;
import com.becareful.becarefulserver.domain.association.domain.vo.AssociationRank;
import com.becareful.becarefulserver.domain.association.repository.AssociationRepository;
import com.becareful.becarefulserver.domain.common.domain.Gender;
import com.becareful.becarefulserver.domain.community.domain.BoardType;
import com.becareful.becarefulserver.domain.community.domain.Post;
import com.becareful.becarefulserver.domain.community.domain.PostBoard;
import com.becareful.becarefulserver.domain.community.dto.request.PostCreateOrUpdateRequest;
import com.becareful.becarefulserver.domain.community.repository.PostBoardRepository;
import com.becareful.becarefulserver.domain.community.repository.PostRepository;
import com.becareful.becarefulserver.domain.community.service.PostService;
import com.becareful.becarefulserver.domain.nursing_institution.domain.vo.InstitutionRank;
import com.becareful.becarefulserver.domain.socialworker.domain.SocialWorker;
import com.becareful.becarefulserver.domain.socialworker.repository.SocialWorkerRepository;
import com.becareful.becarefulserver.fixture.NursingInstitutionFixture;
import com.becareful.becarefulserver.global.exception.exception.PostBoardException;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class PostIntegrationTest extends IntegrationTest {

    @Autowired
    private PostService postService;

    @Autowired
    private PostBoardRepository postBoardRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private SocialWorkerRepository socialWorkerRepository;

    @Autowired
    private AssociationRepository associationRepository;

    private SocialWorker createMember(String phone, AssociationRank rank) {
        SocialWorker member = SocialWorker.create(
                "name",
                "nick",
                LocalDate.now(),
                Gender.FEMALE,
                phone,
                InstitutionRank.SOCIAL_WORKER,
                rank,
                true,
                NursingInstitutionFixture.NURSING_INSTITUTION);
        Association association = associationRepository.findAll().get(0);
        member.joinAssociation(association, rank);
        return socialWorkerRepository.save(member);
    }

    private PostBoard createBoard() {
        Association association = associationRepository.findAll().get(0);
        PostBoard board = PostBoard.create(
                BoardType.ASSOCIATION_NOTICE, AssociationRank.MEMBER, AssociationRank.MEMBER, association);
        return postBoardRepository.save(board);
    }

    @Test
    @WithSocialWorker(phoneNumber = "01010000000")
    void 게시글_생성에_성공한다() {
        createMember("01010000000", AssociationRank.MEMBER);
        createBoard();

        PostCreateOrUpdateRequest request =
                new PostCreateOrUpdateRequest("title", "content", false, null, null, null, null);

        Long postId = postService.createPost("association-notice", request);

        assertThat(postRepository.findById(postId)).isPresent();
    }

    @Test
    @WithSocialWorker(phoneNumber = "01020000000")
    void 작성권한이_없으면_게시글_생성에_실패한다() {
        createMember("01020000000", AssociationRank.NONE);
        createBoard();

        PostCreateOrUpdateRequest request =
                new PostCreateOrUpdateRequest("title", "content", false, null, null, null, null);

        assertThatThrownBy(() -> postService.createPost("association-notice", request))
                .isInstanceOf(PostBoardException.class);
    }

    @Test
    @WithSocialWorker(phoneNumber = "01030000000")
    void 게시글_수정에_성공한다() {
        createMember("01030000000", AssociationRank.MEMBER);
        createBoard();

        PostCreateOrUpdateRequest request =
                new PostCreateOrUpdateRequest("title", "content", false, null, null, null, null);
        Long postId = postService.createPost("association-notice", request);

        postService.updatePost(
                "association-notice",
                postId,
                new PostCreateOrUpdateRequest("title2", "content2", false, null, null, null, null));

        Post post = postRepository.findById(postId).get();
        assertThat(post.getTitle()).isEqualTo("title2");
    }
}
