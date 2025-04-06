package com.becareful.becarefulserver.domain.community.service;

import com.becareful.becarefulserver.domain.community.domain.Post;
import com.becareful.becarefulserver.domain.community.domain.PostBoard;
import com.becareful.becarefulserver.domain.community.dto.request.PostCreateRequest;
import com.becareful.becarefulserver.domain.community.dto.request.PostUpdateRequest;
import com.becareful.becarefulserver.domain.community.repository.PostBoardRepository;
import com.becareful.becarefulserver.domain.community.repository.PostRepository;
import com.becareful.becarefulserver.domain.socialworker.domain.Socialworker;
import com.becareful.becarefulserver.global.exception.exception.PostBoardException;
import com.becareful.becarefulserver.global.exception.exception.PostException;
import com.becareful.becarefulserver.global.util.AuthUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.becareful.becarefulserver.global.exception.ErrorMessage.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {

    private final AuthUtil authUtil;
    private final PostRepository postRepository;
    private final PostBoardRepository postBoardRepository;

    @Transactional
    public Long createPost(Long boardId, PostCreateRequest request) {
        Socialworker currentMember = authUtil.getLoggedInSocialWorker();

        PostBoard postBoard = postBoardRepository.findById(boardId)
                .orElseThrow(() -> new PostBoardException(POST_BOARD_NOT_FOUND));

        validateSocialWorkerRankWritable(currentMember, postBoard);

        Post post = new Post(request.title(), request.content(), request.isImportant(), postBoard, currentMember);
        postRepository.save(post);

        return post.getId();
    }

    @Transactional
    public void updatePost(Long boardId, Long postId, PostUpdateRequest request) {
        Socialworker currentMember = authUtil.getLoggedInSocialWorker();

        PostBoard postBoard = postBoardRepository.findById(boardId)
                .orElseThrow(() -> new PostBoardException(POST_BOARD_NOT_FOUND));

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostException(POST_NOT_FOUND));

        validateSocialWorkerRankWritable(currentMember, postBoard);
        post.validateAuthor(currentMember);

        post.update(request.title(), request.content(), request.isImportant());
    }

    @Transactional
    public void deletePost(Long boardId, Long postId) {
        Socialworker currentMember = authUtil.getLoggedInSocialWorker();

        PostBoard postBoard = postBoardRepository.findById(boardId)
                .orElseThrow(() -> new PostBoardException(POST_BOARD_NOT_FOUND));

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostException(POST_NOT_FOUND));

        validateSocialWorkerRankWritable(currentMember, postBoard);
        post.validateAuthor(currentMember);

        postRepository.delete(post);
    }

    private void validateSocialWorkerRankWritable(Socialworker socialworker, PostBoard board) {
        if (!board.getWritableRank().equals(socialworker.getInstitutionRank())) {
            throw new PostBoardException(POST_BOARD_NOT_WRITABLE);
        }
    }
}
