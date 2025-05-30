package com.becareful.becarefulserver.domain.association.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Entity
@Getter
@RequiredArgsConstructor
public class Association {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String profileImageUrl;

    private Integer establishedYear;

    @Builder(access = AccessLevel.PRIVATE)
    private Association(String name, String profileImageUrl, Integer establishedYear) {
        this.name = name;
        this.profileImageUrl = profileImageUrl;
        this.establishedYear = establishedYear;
    }

    public static Association create(String name, String profileImageUrl, Integer establishedYear) {
        return Association.builder()
                .name(name)
                .profileImageUrl(profileImageUrl)
                .establishedYear(establishedYear)
                .build();
    }
}
