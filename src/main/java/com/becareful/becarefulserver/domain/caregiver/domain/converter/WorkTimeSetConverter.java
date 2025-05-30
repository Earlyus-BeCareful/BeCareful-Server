package com.becareful.becarefulserver.domain.caregiver.domain.converter;

import com.becareful.becarefulserver.domain.caregiver.domain.WorkTime;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.Arrays;
import java.util.EnumSet;

@Converter
public class WorkTimeSetConverter implements AttributeConverter<EnumSet<WorkTime>, String> {

    @Override
    public String convertToDatabaseColumn(EnumSet<WorkTime> attribute) {
        StringBuilder sb = new StringBuilder();
        attribute.forEach(workTime -> sb.append(workTime.name()).append(","));
        if (sb.charAt(sb.length() - 1) == ',') {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }

    @Override
    public EnumSet<WorkTime> convertToEntityAttribute(String dbData) {
        EnumSet<WorkTime> result = EnumSet.noneOf(WorkTime.class);

        if (dbData.isBlank()) return result;

        Arrays.stream(dbData.split(",")).forEach(e -> result.add(WorkTime.valueOf(e)));

        return result;
    }
}
