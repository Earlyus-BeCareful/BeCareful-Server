/* (C)2025 */
package com.becareful.becarefulserver.domain.community.domain;

import com.becareful.becarefulserver.domain.common.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class File extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String url;

    @Enumerated(EnumType.STRING)
    private FileType fileType;

    @JoinColumn(name = "post_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Post post;

    @Builder(access = AccessLevel.PRIVATE)
    private File(String url, FileType fileType, Post post) {
        this.url = url;
        this.fileType = fileType;
        this.post = post;
    }

    public static File create(String url, FileType fileType, Post post) {
        return File.builder().url(url).fileType(fileType).post(post).build();
    }
}
