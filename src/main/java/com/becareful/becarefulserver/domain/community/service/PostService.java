package com.becareful.becarefulserver.domain.community.service;

import com.becareful.becarefulserver.domain.community.domain.Post;
import com.becareful.becarefulserver.domain.community.domain.PostBoard;
import com.becareful.becarefulserver.domain.community.dto.request.PostCreateRequest;
import com.becareful.becarefulserver.domain.community.repository.PostBoardRepository;
import com.becareful.becarefulserver.domain.community.repository.PostRepository;
import com.becareful.becarefulserver.domain.socialworker.domain.Socialworker;
import com.becareful.becarefulserver.global.exception.exception.PostBoardException;
import com.becareful.becarefulserver.global.util.AuthUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.becareful.becarefulserver.global.exception.ErrorMessage.POST_BOARD_CANNOT_WRITABLE;
import static com.becareful.becarefulserver.global.exception.ErrorMessage.POST_BOARD_NOT_FOUND;

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

    private void validateSocialWorkerRankWritable(Socialworker socialworker, PostBoard board) {
        if (!board.getWritableRank().equals(socialworker.getInstitutionRank())) {
            throw new PostBoardException(POST_BOARD_CANNOT_WRITABLE);
        }
    }
}
