package com.becareful.becarefulserver.domain.caregiver.domain.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.time.DayOfWeek;
import java.util.Arrays;
import java.util.EnumSet;

@Converter
public class DayOfWeekSetConverter extends EnumSetConverter<DayOfWeek> {

    protected DayOfWeekSetConverter() {
        super(DayOfWeek.class);
    }

//    @Override
//    public String convertToDatabaseColumn(EnumSet<DayOfWeek> attribute) {
//        StringBuilder sb = new StringBuilder();
//        attribute.forEach(dayOfWeek -> sb.append(dayOfWeek.name()).append(","));
//        if (sb.charAt(sb.length() - 1) == ',') {
//            sb.deleteCharAt(sb.length() - 1);
//        }
//        return sb.toString();
//    }
//
//    @Override
//    public EnumSet<DayOfWeek> convertToEntityAttribute(String dbData) {
//        EnumSet<DayOfWeek> result = EnumSet.noneOf(DayOfWeek.class);
//
//        if (dbData.isBlank())
//            return result;
//
//        Arrays.stream(dbData.split(","))
//                .forEach(e -> result.add(DayOfWeek.valueOf(e)));
//
//        return result;
//    }
}
