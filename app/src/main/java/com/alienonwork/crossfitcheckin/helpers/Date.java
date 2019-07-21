package com.alienonwork.crossfitcheckin.helpers;

import android.util.Pair;

import com.jakewharton.threetenabp.AndroidThreeTen;

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
}
