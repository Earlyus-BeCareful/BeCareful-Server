package com.becareful.becarefulserver.community.api;

import com.becareful.becarefulserver.common.IntegrationTest;
import com.becareful.becarefulserver.domain.community.repository.PostBoardRepository;
import com.becareful.becarefulserver.domain.community.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;

public class PostIntegrationTest extends IntegrationTest {

    @Autowired
    private PostService postService;

    @Autowired
    private PostBoardRepository postBoardRepository;

    //    @Test
    //    @WithSocialWorker(phoneNumber = "01012345679")
    //    void 게시글_생성에_성공한다() {
    //        PostCreateRequest request = new PostCreateRequest("title", "content", false);
    //        PostBoard board = postBoardRepository.save(PostBoardFixture.협회공지);
    //
    //        postService.createPost(board.getId(), request);
    //    }
    //
    //    @Test
    //    @WithSocialWorker(phoneNumber = "01012345678")
    //    void 작성권한이_없으면_게시글_생성에_실패한다() {
    //        PostCreateRequest request = new PostCreateRequest("title", "content", false);
    //        PostBoard board = postBoardRepository.save(PostBoardFixture.협회공지);
    //
    //        Assertions.assertThatThrownBy(() -> postService.createPost(board.getId(), request))
    //                .isInstanceOf(PostBoardException.class);
    //    }
    //
    //    @Test
    //    @WithSocialWorker(phoneNumber = "01012345679")
    //    void 게시글_수정에_성공한다() {
    //        PostCreateRequest request = new PostCreateRequest("title", "content", false);
    //        PostBoard board = postBoardRepository.save(PostBoardFixture.협회공지);
    //        Long postId = postService.createPost(board.getId(), request);
    //
    //        postService.updatePost(board.getId(), postId, new PostUpdateRequest("title2", "content2", false));
    //    }
}
