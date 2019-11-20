package com.alienonwork.crossfitcheckin.converters;

import org.threeten.bp.Instant;
import org.threeten.bp.OffsetDateTime;
import org.threeten.bp.OffsetTime;

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

    @TypeConverter
    public static OffsetTime fromStringToOffsetTime(String time) {
        return time == null ? null : OffsetTime.parse(time);
    }

    @TypeConverter
    public static String fromOffsetTimeToString(OffsetTime time) {
        return time == null ? null : time.toString();
    }
}
