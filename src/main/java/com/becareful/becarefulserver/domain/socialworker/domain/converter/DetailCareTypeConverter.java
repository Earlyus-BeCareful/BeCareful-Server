package com.becareful.becarefulserver.domain.socialworker.domain.converter;

import com.becareful.becarefulserver.domain.caregiver.domain.converter.EnumSetConverter;
import com.becareful.becarefulserver.domain.common.domain.DetailCareType;
import jakarta.persistence.Converter;

@Converter
public class DetailCareTypeConverter extends EnumSetConverter<DetailCareType> {
    protected DetailCareTypeConverter() {
        super(DetailCareType.class);
    }
}
