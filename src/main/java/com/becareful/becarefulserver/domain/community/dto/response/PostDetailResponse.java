package com.becareful.becarefulserver.domain.community.dto.response;

import com.becareful.becarefulserver.domain.community.domain.FileType;
import com.becareful.becarefulserver.domain.community.domain.Post;
import com.becareful.becarefulserver.domain.community.domain.PostMedia;
import com.becareful.becarefulserver.domain.community.dto.AuthorSimpleDto;
import com.becareful.becarefulserver.domain.socialworker.domain.SocialWorker;
import java.time.format.DateTimeFormatter;
import java.util.List;

public record PostDetailResponse(
        Long postId,
        String title,
        String content,
        boolean isImportant,
        boolean isEdited,
        String postedDate,
        AuthorSimpleDto author,
        List<String> imageUrls,
        List<String> videoUrls,
        List<String> fileUrls,
        boolean isMyPost) {

    public static PostDetailResponse of(Post post, Long currentUserId) {
        SocialWorker author = post.getAuthor();
        boolean isMyPost = author != null && author.getId().equals(currentUserId);
        return new PostDetailResponse(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.isImportant(),
                !post.getCreateDate().isEqual(post.getUpdateDate()),
                post.getUpdateDate().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                AuthorSimpleDto.from(author),
                post.getMediaListByType(FileType.IMAGE).stream()
                        .map(PostMedia::getMediaUrl)
                        .toList(),
                post.getMediaListByType(FileType.VIDEO).stream()
                        .map(PostMedia::getMediaUrl)
                        .toList(),
                post.getMediaListByType(FileType.FILE).stream()
                        .map(PostMedia::getMediaUrl)
                        .toList(),
                isMyPost);
    }
}
