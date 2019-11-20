package com.alienonwork.crossfitcheckin.util;

import android.util.Pair;

import org.threeten.bp.LocalDate;
import org.threeten.bp.temporal.ChronoUnit;

import androidx.annotation.Nullable;

public class Date {

    public static Pair<LocalDate, LocalDate> getFirstAndLastDayOfCurrentWeek(@Nullable LocalDate d) {
        if (d == null)
            d = LocalDate.now();

        LocalDate firstDayOfCurrentWeek;
        LocalDate lastDayOfCurrentWeek;
        Integer daysUntilFirstDayOfWeek;
        Integer daysUntilLastDayOfWeek;
        Integer currentDayOfWeek;

        currentDayOfWeek = d.getDayOfWeek().getValue();

        daysUntilFirstDayOfWeek = currentDayOfWeek - 7 + 6;
        daysUntilLastDayOfWeek = 7 - currentDayOfWeek;

        firstDayOfCurrentWeek = d.minus(daysUntilFirstDayOfWeek, ChronoUnit.DAYS);
        lastDayOfCurrentWeek = d.plus(daysUntilLastDayOfWeek, ChronoUnit.DAYS);

        return new Pair<>(firstDayOfCurrentWeek, lastDayOfCurrentWeek);
    }

    public static Pair<LocalDate, LocalDate> getFirstAndLastDayOfNextWeek() {
        LocalDate date = LocalDate.now();
        date.plusDays(8 - date.getDayOfWeek().getValue());

        return getFirstAndLastDayOfCurrentWeek(date);
    }
}
