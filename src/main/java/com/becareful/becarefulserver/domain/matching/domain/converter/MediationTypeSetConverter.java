package com.becareful.becarefulserver.domain.matching.domain.converter;

import com.becareful.becarefulserver.domain.caregiver.domain.converter.EnumSetConverter;
import com.becareful.becarefulserver.domain.matching.domain.MediationType;
import jakarta.persistence.Converter;

@Converter
public class MediationTypeSetConverter extends EnumSetConverter<MediationType> {

    protected MediationTypeSetConverter() {
        super(MediationType.class);
    }
}
