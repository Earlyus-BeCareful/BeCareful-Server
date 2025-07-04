package com.becareful.becarefulserver.domain.community.service;

import static com.becareful.becarefulserver.global.exception.ErrorMessage.POST_BOARD_NOT_FOUND;
import static com.becareful.becarefulserver.global.exception.ErrorMessage.POST_NOT_FOUND;

import com.becareful.becarefulserver.domain.community.domain.BoardType;
import com.becareful.becarefulserver.domain.community.domain.Comment;
import com.becareful.becarefulserver.domain.community.domain.Post;
import com.becareful.becarefulserver.domain.community.domain.PostBoard;
import com.becareful.becarefulserver.domain.community.dto.request.CommentCreateRequest;
import com.becareful.becarefulserver.domain.community.dto.response.CommentResponse;
import com.becareful.becarefulserver.domain.community.repository.CommentRepository;
import com.becareful.becarefulserver.domain.community.repository.PostBoardRepository;
import com.becareful.becarefulserver.domain.community.repository.PostRepository;
import com.becareful.becarefulserver.domain.socialworker.domain.SocialWorker;
import com.becareful.becarefulserver.global.exception.exception.PostBoardException;
import com.becareful.becarefulserver.global.exception.exception.PostException;
import com.becareful.becarefulserver.global.util.AuthUtil;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final AuthUtil authUtil;
    private final CommentRepository commentRepository;
    private final PostBoardRepository postBoardRepository;
    private final PostRepository postRepository;

    @Transactional
    public Long createComment(String boardType, Long postId, CommentCreateRequest request) {
        SocialWorker currentMember = authUtil.getLoggedInSocialWorker();
        BoardType type = BoardType.fromUrlBoardType(boardType);

        PostBoard postBoard = postBoardRepository
                .findByBoardTypeAndAssociation(type, currentMember.getAssociation())
                .orElseThrow(() -> new PostBoardException(POST_BOARD_NOT_FOUND));
        Post post = postRepository.findById(postId).orElseThrow(() -> new PostException(POST_NOT_FOUND));

        postBoard.validateReadableFor(currentMember);
        post.validateBoard(postBoard.getId());

        Comment comment = Comment.create(request.content(), currentMember, post);

        return commentRepository.save(comment).getId();
    }

    @Transactional(readOnly = true)
    public List<CommentResponse> getComments(String boardType, Long postId) {
        SocialWorker currentMember = authUtil.getLoggedInSocialWorker();
        BoardType type = BoardType.fromUrlBoardType(boardType);

        PostBoard postBoard = postBoardRepository
                .findByBoardTypeAndAssociation(type, currentMember.getAssociation())
                .orElseThrow(() -> new PostBoardException(POST_BOARD_NOT_FOUND));
        Post post = postRepository.findById(postId).orElseThrow(() -> new PostException(POST_NOT_FOUND));

        postBoard.validateReadableFor(currentMember);
        post.validateBoard(postBoard.getId());

        return commentRepository.findAllByPost(post).stream()
                .map(CommentResponse::from)
                .toList();
    }
}
