package com.becareful.becarefulserver.domain.socialworker.domain.vo;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Address {

    String streetAddress;
    String detailAddress;

    public String getFullAddress() {
        return streetAddress + " " + detailAddress;
    }
}
