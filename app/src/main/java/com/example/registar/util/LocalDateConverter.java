package com.example.registar.util;

import androidx.room.TypeConverter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class LocalDateConverter {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

    @TypeConverter
    public static String fromLocalDate(LocalDate localDate) {
        return localDate == null ? null : localDate.format(FORMATTER);
    }

    @TypeConverter
    public static LocalDate toLocalDate(String dateString) {
        return dateString == null ? null : LocalDate.parse(dateString, FORMATTER);
    }
}
