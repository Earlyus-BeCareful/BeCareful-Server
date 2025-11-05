package com.becareful.becarefulserver.domain.association.domain;

import com.becareful.becarefulserver.domain.association.dto.request.*;
import com.becareful.becarefulserver.domain.common.domain.*;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Association extends BaseEntity {
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

    public void updateAssociationInfo(UpdateAssociationInfoRequest request, String profileImageUrl) {
        this.name = request.associationName();
        this.establishedYear = request.associationEstablishedYear();
        this.profileImageUrl = profileImageUrl;
    }
}
