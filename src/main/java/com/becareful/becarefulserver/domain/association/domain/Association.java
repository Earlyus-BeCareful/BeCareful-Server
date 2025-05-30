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

    @Builder(access = AccessLevel.PRIVATE)
    private Association(String name) {
        this.name = name;
    }

    public static Association create(String name) {
        return Association.builder().name(name).build();
    }
}
