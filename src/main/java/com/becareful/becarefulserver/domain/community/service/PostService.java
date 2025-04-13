package com.becareful.becarefulserver.domain.community.service;

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
import com.becareful.becarefulserver.global.util.FileUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

import static com.becareful.becarefulserver.global.constant.S3Constant.*;
import static com.becareful.becarefulserver.global.exception.ErrorMessage.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {

    private final AuthUtil authUtil;
    private final PostRepository postRepository;
    private final PostBoardRepository postBoardRepository;
    private final FileUtil fileUtil;

    @Transactional
    public Long createPost(Long boardId, PostCreateRequest request) {
        SocialWorker currentMember = authUtil.getLoggedInSocialWorker();

        PostBoard postBoard = postBoardRepository.findById(boardId)
                .orElseThrow(() -> new PostBoardException(POST_BOARD_NOT_FOUND));

        validateSocialWorkerRankWritable(currentMember, postBoard);

        Post post = Post.create(request.title(), request.content(), request.isImportant(), postBoard, currentMember);
        postRepository.save(post);

        return post.getId();
    }

    @Transactional
    public void updatePost(Long boardId, Long postId, PostUpdateRequest request) {
        SocialWorker currentMember = authUtil.getLoggedInSocialWorker();

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
        SocialWorker currentMember = authUtil.getLoggedInSocialWorker();

        PostBoard postBoard = postBoardRepository.findById(boardId)
                .orElseThrow(() -> new PostBoardException(POST_BOARD_NOT_FOUND));

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostException(POST_NOT_FOUND));

        validateSocialWorkerRankWritable(currentMember, postBoard);
        post.validateAuthor(currentMember);

        postRepository.delete(post);
    }

    @Transactional(readOnly = true)
    public List<PostSimpleDto> getPosts(Long boardId, Pageable pageable) {
        SocialWorker currentMember = authUtil.getLoggedInSocialWorker();
        PostBoard postBoard = postBoardRepository.findById(boardId)
                .orElseThrow(() -> new PostBoardException(POST_BOARD_NOT_FOUND));

        validateSocialWorkerRankReadable(currentMember, postBoard);

        return postRepository.findAllByBoard(postBoard, pageable)
                .map(PostSimpleDto::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<PostSimpleDto> getImportantPosts(Pageable pageable) {
        SocialWorker currentMember = authUtil.getLoggedInSocialWorker();

        return postRepository.findAllReadableImportantPosts(currentMember.getAssociationRank(), pageable)
                .map(PostSimpleDto::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public PostDetailResponse getPost(Long boardId, Long postId) {
        SocialWorker currentMember = authUtil.getLoggedInSocialWorker();
        PostBoard postBoard = postBoardRepository.findById(boardId)
                .orElseThrow(() -> new PostBoardException(POST_BOARD_NOT_FOUND));

        validateSocialWorkerRankReadable(currentMember, postBoard);

        return postRepository.findById(postId)
                .map(PostDetailResponse::from)
                .orElseThrow(() -> new PostException(POST_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public String uploadFile(Long boardId, MultipartFile file) {
        SocialWorker currentMember = authUtil.getLoggedInSocialWorker();
        PostBoard postBoard = postBoardRepository.findById(boardId)
                .orElseThrow(() -> new PostBoardException(POST_BOARD_NOT_FOUND));

        postBoard.validateWritableFor(currentMember);
        String path = validateFileTypeAndSize(file);

        // TODO : 파일 업로드 개수 제한을 업로드시 결정 vs 게시글 포스팅 시 결정 + file 의 경우 총 업로드 용량 제한도 체크

        try {
            return fileUtil.upload(file, path, file.getName());
        } catch (IOException e) {
            throw new PostException(POST_FILE_UPLOAD_FAILED);
        }
    }

    private String validateFileTypeAndSize(MultipartFile file) {
        String type = file.getContentType();
        if (type == null) {
            throw new PostException(POST_FILE_TYPE_INVALID);
        }

        if (type.startsWith("image/")) {
            if (file.getSize() > IMAGE_MAX_SIZE) {
                throw new PostException(POST_FILE_UPLOAD_SIZE_EXCEED);
            }
            return POST_IMAGE_PATH;
        }

        if (type.startsWith("video/")) {
            if (file.getSize() > VIDEO_MAX_SIZE) {
                throw new PostException(POST_FILE_UPLOAD_SIZE_EXCEED);
            }
            return POST_VIDEO_PATH;
        }

        if (type.startsWith("application/")) {
            if (file.getSize() > FILE_MAX_SIZE) {
                throw new PostException(POST_FILE_UPLOAD_SIZE_EXCEED);
            }
            return POST_FILE_PATH;
        }

        throw new PostException(POST_FILE_TYPE_INVALID);
    }

    private void validateSocialWorkerRankWritable(SocialWorker socialworker, PostBoard board) {
        if (!board.getWritableRank().equals(socialworker.getAssociationRank())) {
            throw new PostBoardException(POST_BOARD_NOT_WRITABLE);
        }
    }

    private void validateSocialWorkerRankReadable(SocialWorker socialWorker, PostBoard board) {
        if (!board.getReadableRank().equals(socialWorker.getAssociationRank())) {
            throw new PostBoardException(POST_BOARD_NOT_READABLE);
        }
    }
}
