package com.alienonwork.crossfitcheckin.converters;

import org.threeten.bp.Instant;
import org.threeten.bp.OffsetDateTime;

import androidx.room.TypeConverter;

public class DateTypeConverters {

    @TypeConverter
    public static OffsetDateTime fromStringToOffsetDatetime(String offsetDateTime) {
        return offsetDateTime == null ? null : OffsetDateTime.parse(offsetDateTime);
    }

    @TypeConverter
    public static String fromOffsetDatetimeToString(OffsetDateTime offsetDateTime) {
        return offsetDateTime == null ? null : offsetDateTime.toString();
    }

    @TypeConverter
    public static Instant fromLongToInstant(Long timestamp) {
        return timestamp == null ? null : Instant.ofEpochMilli(timestamp);
    }

    @TypeConverter
    public static Long fromInstantToLong(Instant timestamp) {
        return timestamp == null ? null : timestamp.toEpochMilli();
    }
}
