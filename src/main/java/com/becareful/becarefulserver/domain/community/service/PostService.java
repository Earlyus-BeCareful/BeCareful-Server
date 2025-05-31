package com.becareful.becarefulserver.domain.community.service;

import static com.becareful.becarefulserver.global.exception.ErrorMessage.*;

import com.becareful.becarefulserver.domain.community.domain.BoardType;
import com.becareful.becarefulserver.domain.community.domain.Post;
import com.becareful.becarefulserver.domain.community.domain.PostBoard;
import com.becareful.becarefulserver.domain.community.dto.PostSimpleDto;
import com.becareful.becarefulserver.domain.community.dto.request.PostCreateRequest;
import com.becareful.becarefulserver.domain.community.dto.request.PostUpdateRequest;
import com.becareful.becarefulserver.domain.community.dto.response.PostDetailResponse;
import com.becareful.becarefulserver.domain.community.repository.PostBoardRepository;
import com.becareful.becarefulserver.domain.community.repository.PostRepository;
import com.becareful.becarefulserver.domain.socialworker.domain.SocialWorker;
import com.becareful.becarefulserver.global.exception.exception.PostBoardException;
import com.becareful.becarefulserver.global.exception.exception.PostException;
import com.becareful.becarefulserver.global.util.AuthUtil;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {

    private final AuthUtil authUtil;
    private final PostRepository postRepository;
    private final PostBoardRepository postBoardRepository;

    @Transactional
    public Long createPost(String boardType, PostCreateRequest request) {
        SocialWorker currentMember = authUtil.getLoggedInSocialWorker();
        BoardType type = BoardType.fromUrlBoardType(boardType);

        PostBoard postBoard =
                postBoardRepository.findByBoardTypeAndAssociation(type, currentMember.getAssociation())
                        .orElseThrow(() -> new PostBoardException(POST_BOARD_NOT_FOUND));

        validateSocialWorkerRankWritable(currentMember, postBoard);

        Post post = Post.create(request.title(), request.content(), request.isImportant(), postBoard, currentMember);
        postRepository.save(post);

        return post.getId();
    }

    @Transactional
    public void updatePost(String boardType, Long postId, PostUpdateRequest request) {
        SocialWorker currentMember = authUtil.getLoggedInSocialWorker();
        BoardType type = BoardType.fromUrlBoardType(boardType);

        PostBoard postBoard =
                postBoardRepository.findByBoardTypeAndAssociation(type, currentMember.getAssociation())
                        .orElseThrow(() -> new PostBoardException(POST_BOARD_NOT_FOUND));

        Post post = postRepository.findById(postId).orElseThrow(() -> new PostException(POST_NOT_FOUND));

        validateSocialWorkerRankWritable(currentMember, postBoard);
        post.validateAuthor(currentMember);

        post.update(request.title(), request.content(), request.isImportant());
    }

    @Transactional
    public void deletePost(String boardType, Long postId) {
        SocialWorker currentMember = authUtil.getLoggedInSocialWorker();
        BoardType type = BoardType.fromUrlBoardType(boardType);

        PostBoard postBoard =
                postBoardRepository.findByBoardTypeAndAssociation(type, currentMember.getAssociation())
                        .orElseThrow(() -> new PostBoardException(POST_BOARD_NOT_FOUND));

        Post post = postRepository.findById(postId).orElseThrow(() -> new PostException(POST_NOT_FOUND));

        validateSocialWorkerRankWritable(currentMember, postBoard);
        post.validateAuthor(currentMember);

        postRepository.delete(post);
    }

    @Transactional(readOnly = true)
    public List<PostSimpleDto> getPosts(String boardType, Pageable pageable) {
        SocialWorker currentMember = authUtil.getLoggedInSocialWorker();
        BoardType type = BoardType.fromUrlBoardType(boardType);

        PostBoard postBoard =
                postBoardRepository.findByBoardTypeAndAssociation(type, currentMember.getAssociation())
                        .orElseThrow(() -> new PostBoardException(POST_BOARD_NOT_FOUND));

        validateSocialWorkerRankReadable(currentMember, postBoard);

        return postRepository
                .findAllByBoard(postBoard, pageable)
                .map(PostSimpleDto::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<PostSimpleDto> getImportantPosts(Pageable pageable) {
        SocialWorker currentMember = authUtil.getLoggedInSocialWorker();

        return postRepository
                .findAllReadableImportantPosts(currentMember.getAssociationRank(), pageable)
                .map(PostSimpleDto::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public PostDetailResponse getPost(String boardType, Long postId) {
        SocialWorker currentMember = authUtil.getLoggedInSocialWorker();
        BoardType type = BoardType.fromUrlBoardType(boardType);

        PostBoard postBoard =
                postBoardRepository.findByBoardTypeAndAssociation(type, currentMember.getAssociation())
                        .orElseThrow(() -> new PostBoardException(POST_BOARD_NOT_FOUND));

        validateSocialWorkerRankReadable(currentMember, postBoard);

        return postRepository
                .findById(postId)
                .map(PostDetailResponse::from)
                .orElseThrow(() -> new PostException(POST_NOT_FOUND));
    }

    private void validateSocialWorkerRankWritable(SocialWorker socialworker, PostBoard board) {
        if (!board.getWritableRank().equals(socialworker.getAssociationRank()) || !board.getAssociation().getId().equals(socialworker.getAssociation().getId())) {
            throw new PostBoardException(POST_BOARD_NOT_WRITABLE);
        }
    }

    private void validateSocialWorkerRankReadable(SocialWorker socialWorker, PostBoard board) {
        if (!board.getReadableRank().equals(socialWorker.getAssociationRank()) || !board.getAssociation().getId().equals(socialWorker.getAssociation().getId())) {
            throw new PostBoardException(POST_BOARD_NOT_READABLE);
        }
    }
}
