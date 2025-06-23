package com.becareful.becarefulserver.domain.common.vo;

import jakarta.persistence.Embeddable;
import lombok.*;

@Getter
@Embeddable
@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Location {

    private String siDo;

    private String siGuGun;

    private String eupMyeonDong;

    public String getFullAddress() {
        return siDo + " " + siGuGun + " " + eupMyeonDong;
    }

    public static Location of(String siDo, String siGuGun, String eupMyeonDong) {
        return Location.builder()
                .siDo(siDo)
                .siGuGun(siGuGun)
                .eupMyeonDong(eupMyeonDong)
                .build();
    }
}
