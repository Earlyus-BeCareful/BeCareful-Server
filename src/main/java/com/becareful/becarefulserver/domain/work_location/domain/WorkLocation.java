package com.becareful.becarefulserver.domain.work_location.domain;

import com.becareful.becarefulserver.domain.common.domain.BaseEntity;
import com.becareful.becarefulserver.domain.common.domain.vo.Location;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WorkLocation extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "work_location_id")
    private Long id;

    @Embedded
    private Location location;

    @Builder(access = AccessLevel.PRIVATE)
    private WorkLocation(Location location) {
        this.location = location;
    }

    public static WorkLocation from(Location location) {
        return WorkLocation.builder().location(location).build();
    }
}
