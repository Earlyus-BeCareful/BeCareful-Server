package com.becareful.becarefulserver.domain.community.service;

import static com.becareful.becarefulserver.global.exception.ErrorMessage.*;

import com.becareful.becarefulserver.domain.association.domain.*;
import com.becareful.becarefulserver.domain.community.domain.*;
import com.becareful.becarefulserver.domain.community.dto.*;
import com.becareful.becarefulserver.domain.community.dto.request.*;
import com.becareful.becarefulserver.domain.community.dto.response.*;
import com.becareful.becarefulserver.domain.community.repository.*;
import com.becareful.becarefulserver.domain.socialworker.domain.*;
import com.becareful.becarefulserver.global.exception.exception.*;
import com.becareful.becarefulserver.global.service.*;
import com.becareful.becarefulserver.global.util.*;
import java.util.*;
import lombok.*;
import lombok.extern.slf4j.*;
import org.springframework.data.domain.*;
import org.springframework.stereotype.*;
import org.springframework.transaction.annotation.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {

    private final AuthUtil authUtil;
    private final PostRepository postRepository;
    private final PostBoardRepository postBoardRepository;
    private final S3Service s3Service;
    private final S3Util s3Util;

    private static final int MAX_IMAGE_COUNT = 100;
    private static final int MAX_VIDEO_COUNT = 3;
    private static final int MAX_FILE_COUNT = 5;
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB
    private static final long MAX_TOTAL_FILE_SIZE = 30 * 1024 * 1024; // 30MB
    private final PostMediaRepository postMediaRepository;

    @Transactional
    public Long createPost(String boardType, PostCreateRequest request) {
        SocialWorker currentMember = authUtil.getLoggedInSocialWorker();
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
        if (request.imageList() != null && !request.imageList().isEmpty()) {
            validateImageCount(request.imageList().size());
            for (MediaInfoDto imageInfo : request.imageList()) {
                String imageUrl = s3Util.getPermanentUrlFromTempKey(imageInfo.tempKey());
                PostMedia imageMedia =
                        PostMedia.createImage(imageInfo.fileName(), imageUrl, imageInfo.fileSize(), post);
                post.addMedia(imageMedia);
                s3Service.moveTempFileToPermanent(imageInfo.tempKey());
            }
        }

        // 비디오 처리
        if (request.videoList() != null && !request.videoList().isEmpty()) {
            validateVideoCount(request.videoList().size());
            for (MediaInfoDto videoInfo : request.videoList()) {
                String videoUrl = s3Util.getPermanentUrlFromTempKey(videoInfo.tempKey());
                PostMedia videoMedia =
                        PostMedia.createVideo(videoInfo.fileName(), videoUrl, videoInfo.fileSize(), post);
                post.addMedia(videoMedia);
                s3Service.moveTempFileToPermanent(videoInfo.tempKey());
            }
        }

        // 파일 처리
        if (request.fileList() != null && !request.fileList().isEmpty()) {
            validateFileList(request.fileList());
            for (MediaInfoDto fileInfo : request.fileList()) {
                String fileUrl = s3Util.getPermanentUrlFromTempKey(fileInfo.tempKey());
                PostMedia fileMedia = PostMedia.createFile(fileInfo.fileName(), fileUrl, fileInfo.fileSize(), post);
                post.addMedia(fileMedia);
                s3Service.moveTempFileToPermanent(fileInfo.tempKey());
            }
        }
        postRepository.save(post);
        return post.getId();
    }

    @Transactional
    public void updatePost(String boardType, Long postId, PostUpdateRequest request) {
        SocialWorker currentMember = authUtil.getLoggedInSocialWorker();
        BoardType type = BoardType.fromUrlBoardType(boardType);

        PostBoard postBoard = postBoardRepository
                .findByBoardTypeAndAssociation(type, currentMember.getAssociation())
                .orElseThrow(() -> new PostBoardException(POST_BOARD_NOT_FOUND));
        postBoard.validateWritableFor(currentMember);

        Post post = postRepository.findById(postId).orElseThrow(() -> new PostException(POST_NOT_FOUND));
        post.validateAuthor(currentMember);

        validatePostMediaUpdate(
                post, request.deleteMediaIdList(), request.imageList(), request.videoList(), request.fileList());

        // 미디어 삭제
        if (request.imageList() != null && !request.imageList().isEmpty()) {
            for (Long mediaId : request.deleteMediaIdList()) {
                postMediaRepository.deleteById(mediaId);
            }
        }

        // 새로운 미디어 추가
        if (request.imageList() != null && !request.imageList().isEmpty()) {
            validateImageCount(request.imageList().size());
            //
            for (MediaInfoDto imageInfo : request.imageList()) {
                String imageUrl = s3Util.getPermanentUrlFromTempKey(imageInfo.tempKey());
                PostMedia imageMedia =
                        PostMedia.createImage(imageInfo.fileName(), imageUrl, imageInfo.fileSize(), post);
                post.addMedia(imageMedia);
                s3Service.moveTempFileToPermanent(imageInfo.tempKey());
            }
        }

        if (request.videoList() != null && !request.videoList().isEmpty()) {
            validateVideoCount(request.videoList().size());
            for (MediaInfoDto videoInfo : request.videoList()) {
                String videoUrl = s3Util.getPermanentUrlFromTempKey(videoInfo.tempKey());
                PostMedia videoMedia =
                        PostMedia.createVideo(videoInfo.fileName(), videoUrl, videoInfo.fileSize(), post);
                post.addMedia(videoMedia);
                s3Service.moveTempFileToPermanent(videoInfo.tempKey());
            }
        }

        // 파일 처리
        if (request.fileList() != null && !request.fileList().isEmpty()) {
            validateFileList(request.fileList());
            for (MediaInfoDto fileInfo : request.fileList()) {
                String fileUrl = s3Util.getPermanentUrlFromTempKey(fileInfo.tempKey());
                PostMedia fileMedia = PostMedia.createFile(fileInfo.fileName(), fileUrl, fileInfo.fileSize(), post);
                post.addMedia(fileMedia);
                s3Service.moveTempFileToPermanent(fileInfo.tempKey());
            }
        }

        post.update(request.title(), request.content(), request.isImportant(), request.originalUrl());
    }

    @Transactional
    public void deletePost(String boardType, Long postId) {
        SocialWorker currentMember = authUtil.getLoggedInSocialWorker();
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
        SocialWorker currentMember = authUtil.getLoggedInSocialWorker();
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
        SocialWorker currentMember = authUtil.getLoggedInSocialWorker();
        Association association = currentMember.getAssociation();

        return postRepository.findAllImportantPosts(association, pageable).stream()
                .filter(post -> post.getBoard().isReadableFor(currentMember))
                .map(PostSimpleDto::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public PostDetailResponse getPost(String boardType, Long postId) {
        SocialWorker currentMember = authUtil.getLoggedInSocialWorker();
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

    private void validatePostMediaUpdate(
            Post post,
            List<Long> deleteMediaIdList,
            List<MediaInfoDto> imageList,
            List<MediaInfoDto> videoList,
            List<MediaInfoDto> fileList) {
        List<PostMedia> postMediaList = postMediaRepository.findAllByPost(post);
        postMediaList = postMediaList.stream()
                .filter(media -> !deleteMediaIdList.contains(media.getId()))
                .toList();

        int imageCount = 0;
        int videoCount = 0;
        List<MediaInfoDto> fileListForValidation = new ArrayList<>();

        for (PostMedia postMedia : postMediaList) {
            switch (postMedia.getFileType()) {
                case IMAGE -> imageCount++;
                case VIDEO -> videoCount++;
                case FILE -> fileListForValidation.add(new MediaInfoDto(null, null, null, postMedia.getFileSize()));
            }
        }

        if (imageList != null) {
            imageCount += imageList.size();
        }
        if (videoList != null) {
            videoCount += videoList.size();
        }
        if (fileList != null) {
            fileListForValidation.addAll(fileList);
        }

        validateImageCount(imageCount);
        validateVideoCount(videoCount);
        validateFileList(fileListForValidation);
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
