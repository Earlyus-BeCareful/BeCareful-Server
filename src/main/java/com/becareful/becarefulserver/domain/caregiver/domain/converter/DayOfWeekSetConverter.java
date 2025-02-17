package com.becareful.becarefulserver.domain.caregiver.domain.converter;

import jakarta.persistence.Converter;

import java.time.DayOfWeek;

@Converter
public class DayOfWeekSetConverter extends EnumSetConverter<DayOfWeek> {

    protected DayOfWeekSetConverter() {
        super(DayOfWeek.class);
    }
}
