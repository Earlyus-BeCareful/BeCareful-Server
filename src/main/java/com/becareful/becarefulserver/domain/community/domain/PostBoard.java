package com.becareful.becarefulserver.domain.community.domain;

import com.becareful.becarefulserver.domain.nursingInstitution.vo.InstitutionRank;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostBoard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private InstitutionRank readableRank;

    private InstitutionRank writableRank;
}
