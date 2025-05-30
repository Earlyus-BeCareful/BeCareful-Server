package com.becareful.becarefulserver.domain.caregiver.domain.converter;

import jakarta.persistence.AttributeConverter;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.stream.Collectors;

public abstract class EnumSetConverter<T extends Enum<T>> implements AttributeConverter<EnumSet<T>, String> {

    private final Class<T> enumType;

    protected EnumSetConverter(Class<T> enumType) {
        this.enumType = enumType;
    }

    @Override
    public String convertToDatabaseColumn(EnumSet<T> attribute) {
        if (attribute == null || attribute.isEmpty()) {
            return "";
        }
        return attribute.stream().map(Enum::name).collect(Collectors.joining(","));
    }

    @Override
    public EnumSet<T> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isBlank()) {
            return EnumSet.noneOf(enumType);
        }
        return Arrays.stream(dbData.split(","))
                .map(name -> Enum.valueOf(enumType, name))
                .collect(Collectors.toCollection(() -> EnumSet.noneOf(enumType)));
    }
}
