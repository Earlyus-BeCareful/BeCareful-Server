package com.becareful.becarefulserver.domain.community.service;

import static com.becareful.becarefulserver.global.exception.ErrorMessage.*;

import com.becareful.becarefulserver.domain.association.domain.Association;
import com.becareful.becarefulserver.domain.association.domain.AssociationMember;
import com.becareful.becarefulserver.domain.community.domain.BoardType;
import com.becareful.becarefulserver.domain.community.domain.Post;
import com.becareful.becarefulserver.domain.community.domain.PostBoard;
import com.becareful.becarefulserver.domain.community.domain.PostMedia;
import com.becareful.becarefulserver.domain.community.dto.MediaInfoDto;
import com.becareful.becarefulserver.domain.community.dto.PostSimpleDto;
import com.becareful.becarefulserver.domain.community.dto.request.PostCreateOrUpdateRequest;
import com.becareful.becarefulserver.domain.community.dto.response.PostDetailResponse;
import com.becareful.becarefulserver.domain.community.repository.PostBoardRepository;
import com.becareful.becarefulserver.domain.community.repository.PostRepository;
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

    private static final int MAX_IMAGE_COUNT = 100;
    private static final int MAX_VIDEO_COUNT = 3;
    private static final int MAX_FILE_COUNT = 5;
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB
    private static final long MAX_TOTAL_FILE_SIZE = 30 * 1024 * 1024; // 30MB

    @Transactional
    public Long createPost(String boardType, PostCreateOrUpdateRequest request) {
        AssociationMember currentMember = authUtil.getLoggedInAssociationMember();
        BoardType type = BoardType.fromUrlBoardType(boardType);

        PostBoard postBoard = postBoardRepository
                .findByBoardTypeAndAssociation(type, currentMember.getAssociation())
                .orElseThrow(() -> new PostBoardException(POST_BOARD_NOT_FOUND));

        postBoard.validateWritableFor(currentMember);

        Post post = Post.create(
                request.title(),
                request.content(),
                request.isImportant(),
                request.originalUrl(),
                postBoard,
                currentMember);

        // 이미지 처리
        if (request.imageList() != null) {
            validateImageCount(request.imageList().size());
            for (MediaInfoDto imageInfo : request.imageList()) {
                PostMedia imageMedia =
                        PostMedia.createImage(imageInfo.fileName(), imageInfo.mediaUrl(), imageInfo.fileSize(), post);
                post.addMedia(imageMedia);
            }
        }

        // 비디오 처리
        if (request.videoList() != null) {
            validateVideoCount(request.videoList().size());
            for (MediaInfoDto videoInfo : request.videoList()) {
                PostMedia videoMedia = PostMedia.createVideo(
                        videoInfo.fileName(),
                        videoInfo.mediaUrl(),
                        videoInfo.fileSize(),
                        videoInfo.videoDuration(),
                        post);
                post.addMedia(videoMedia);
            }
        }

        // 파일 처리
        if (request.fileList() != null && !request.fileList().isEmpty()) {
            validateFileList(request.fileList());
            for (MediaInfoDto fileInfo : request.fileList()) {
                PostMedia fileMedia =
                        PostMedia.createFile(fileInfo.fileName(), fileInfo.mediaUrl(), fileInfo.fileSize(), post);
                post.addMedia(fileMedia);
            }
        }

        postRepository.save(post);
        return post.getId();
    }

    @Transactional
    public void updatePost(String boardType, Long postId, PostCreateOrUpdateRequest request) {
        AssociationMember currentMember = authUtil.getLoggedInAssociationMember();
        BoardType type = BoardType.fromUrlBoardType(boardType);

        PostBoard postBoard = postBoardRepository
                .findByBoardTypeAndAssociation(type, currentMember.getAssociation())
                .orElseThrow(() -> new PostBoardException(POST_BOARD_NOT_FOUND));
        postBoard.validateWritableFor(currentMember);

        Post post = postRepository.findById(postId).orElseThrow(() -> new PostException(POST_NOT_FOUND));
        post.validateAuthor(currentMember);

        // 기존 미디어 삭제
        post.getMediaList().clear();

        // 새로운 미디어 추가
        if (request.imageList() != null) {
            validateImageCount(request.imageList().size());
            for (MediaInfoDto imageInfo : request.imageList()) {
                PostMedia imageMedia =
                        PostMedia.createImage(imageInfo.fileName(), imageInfo.mediaUrl(), imageInfo.fileSize(), post);
                post.addMedia(imageMedia);
            }
        }

        if (request.videoList() != null) {
            validateVideoCount(request.videoList().size());
            for (MediaInfoDto videoInfo : request.videoList()) {
                PostMedia videoMedia = PostMedia.createVideo(
                        videoInfo.fileName(),
                        videoInfo.mediaUrl(),
                        videoInfo.fileSize(),
                        videoInfo.videoDuration(),
                        post);
                post.addMedia(videoMedia);
            }
        }

        // 파일 처리
        if (request.fileList() != null && !request.fileList().isEmpty()) {
            validateFileList(request.fileList());
            for (MediaInfoDto fileInfo : request.fileList()) {
                PostMedia fileMedia =
                        PostMedia.createFile(fileInfo.fileName(), fileInfo.mediaUrl(), fileInfo.fileSize(), post);
                post.addMedia(fileMedia);
            }
        }

        post.update(request.title(), request.content(), request.isImportant(), request.originalUrl());
    }

    @Transactional
    public void deletePost(String boardType, Long postId) {
        AssociationMember currentMember = authUtil.getLoggedInAssociationMember();
        BoardType type = BoardType.fromUrlBoardType(boardType);

        PostBoard postBoard = postBoardRepository
                .findByBoardTypeAndAssociation(type, currentMember.getAssociation())
                .orElseThrow(() -> new PostBoardException(POST_BOARD_NOT_FOUND));
        postBoard.validateWritableFor(currentMember);

        Post post = postRepository.findById(postId).orElseThrow(() -> new PostException(POST_NOT_FOUND));
        post.validateAuthor(currentMember);

        postRepository.delete(post);
    }

    @Transactional(readOnly = true)
    public List<PostSimpleDto> getPosts(String boardType, Pageable pageable) {
        AssociationMember currentMember = authUtil.getLoggedInAssociationMember();
        BoardType type = BoardType.fromUrlBoardType(boardType);
        Association association = currentMember.getAssociation();

        PostBoard postBoard = postBoardRepository
                .findByBoardTypeAndAssociation(type, association)
                .orElseThrow(() -> new PostBoardException(POST_BOARD_NOT_FOUND));

        postBoard.validateReadableFor(currentMember);

        return postRepository
                .findAllByBoardAndAssociation(postBoard, association, pageable)
                .map(PostSimpleDto::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<PostSimpleDto> getImportantPosts(Pageable pageable) {
        AssociationMember currentMember = authUtil.getLoggedInAssociationMember();
        Association association = currentMember.getAssociation();

        return postRepository.findAllImportantPosts(association, pageable).stream()
                .filter(post -> post.getBoard().isReadableFor(currentMember))
                .map(PostSimpleDto::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public PostDetailResponse getPost(String boardType, Long postId) {
        AssociationMember currentMember = authUtil.getLoggedInAssociationMember();
        BoardType type = BoardType.fromUrlBoardType(boardType);

        PostBoard postBoard = postBoardRepository
                .findByBoardTypeAndAssociation(type, currentMember.getAssociation())
                .orElseThrow(() -> new PostBoardException(POST_BOARD_NOT_FOUND));
        postBoard.validateReadableFor(currentMember);

        Post post = postRepository.findById(postId).orElseThrow(() -> new PostException(POST_NOT_FOUND));
        validatePostBoardHasPost(postBoard, post);

        return PostDetailResponse.of(post, currentMember.getId());
    }

    private void validatePostBoardHasPost(PostBoard board, Post post) {
        if (!post.getBoard().equals(board)) {
            throw new PostException(POST_NOT_FOUND_IN_BOARD);
        }
    }

    private void validateImageCount(int count) {
        if (count > MAX_IMAGE_COUNT) {
            throw new PostException(POST_MEDIA_IMAGE_COUNT_EXCEEDED);
        }
    }

    private void validateVideoCount(int count) {
        if (count > MAX_VIDEO_COUNT) {
            throw new PostException(POST_MEDIA_VIDEO_COUNT_EXCEEDED);
        }
    }

    private void validateFileList(List<MediaInfoDto> fileList) {
        if (fileList.size() > MAX_FILE_COUNT) {
            throw new PostException(POST_MEDIA_FILE_COUNT_EXCEEDED);
        }
        long totalSize = 0;
        for (MediaInfoDto file : fileList) {
            if (file.fileSize() > MAX_FILE_SIZE) {
                throw new PostException(POST_MEDIA_FILE_SIZE_EXCEEDED);
            }
            totalSize += file.fileSize();
        }
        if (totalSize > MAX_TOTAL_FILE_SIZE) {
            throw new PostException(POST_MEDIA_TOTAL_FILE_SIZE_EXCEEDED);
        }
    }
}
