package com.example.demo.converter;

import com.example.demo.transportation.DayOfWeek;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class DayOfWeekConverter implements AttributeConverter<DayOfWeek, Integer> {

    @Override
    public Integer convertToDatabaseColumn(DayOfWeek day) {
        if (day == null) return null;
        return day.getValue();
    }

    @Override
    public DayOfWeek convertToEntityAttribute(Integer value) {
        if (value == null) return null;
        return DayOfWeek.fromValue(value);
    }
}
