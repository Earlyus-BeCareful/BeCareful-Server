package com.becareful.becarefulserver.domain.community.domain;

import com.becareful.becarefulserver.domain.common.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostMedia extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_media_id")
    private Long id;

    @Column(nullable = false)
    private String fileName;

    @Column(nullable = false)
    private String mediaUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FileType fileType;

    private Long fileSize;

    private Integer videoDuration;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @Builder(access = AccessLevel.PRIVATE)
    private PostMedia(
            String fileName, String mediaUrl, FileType fileType, Long fileSize, Integer videoDuration, Post post) {
        this.fileName = fileName;
        this.mediaUrl = mediaUrl;
        this.fileType = fileType;
        this.fileSize = fileSize;
        this.videoDuration = videoDuration;
        this.post = post;
    }

    public static PostMedia createImage(String fileName, String mediaUrl, Long fileSize, Post post) {
        return PostMedia.builder()
                .fileName(fileName)
                .mediaUrl(mediaUrl)
                .fileType(FileType.IMAGE)
                .fileSize(fileSize)
                .post(post)
                .build();
    }

    public static PostMedia createVideo(
            String fileName, String mediaUrl, Long fileSize, Integer videoDuration, Post post) {
        return PostMedia.builder()
                .fileName(fileName)
                .mediaUrl(mediaUrl)
                .fileType(FileType.VIDEO)
                .fileSize(fileSize)
                .videoDuration(videoDuration)
                .post(post)
                .build();
    }

    public static PostMedia createFile(String fileName, String mediaUrl, Long fileSize, Post post) {
        return PostMedia.builder()
                .fileName(fileName)
                .mediaUrl(mediaUrl)
                .fileType(FileType.FILE)
                .fileSize(fileSize)
                .post(post)
                .build();
    }
}
