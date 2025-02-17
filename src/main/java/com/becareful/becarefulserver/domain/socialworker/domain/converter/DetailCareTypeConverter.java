package com.becareful.becarefulserver.domain.socialworker.domain.converter;
import com.becareful.becarefulserver.domain.caregiver.domain.converter.EnumSetConverter;
import com.becareful.becarefulserver.domain.common.domain.CareType;
import jakarta.persistence.Converter;


@Converter
public class DetailCareTypeConverter extends EnumSetConverter<CareType> {
    protected DetailCareTypeConverter() {
        super(CareType.class);
    }
}