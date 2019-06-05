package com.alienonwork.crossfitcheckin.repository.entity;

import org.threeten.bp.Instant;
import org.threeten.bp.OffsetDateTime;

public class ClassCrossfit {
    Integer id;
    Instant timestampUTC;
    OffsetDateTime datetimeUTC;
    Integer dayOfYear;
    String hour;
    int[] plans;
    String className;
    Boolean checkinMade;
    Boolean weekLimit;
    Integer vacancy;

    public ClassCrossfit(Integer id, Instant timestampUTC, OffsetDateTime datetimeUTC, Integer dayOfYear, String hour, int[] plans, String className, Boolean checkinMade, Boolean weekLimit, Integer vacancy) {
        this.id = id;
        this.timestampUTC = timestampUTC;
        this.datetimeUTC = datetimeUTC;
        this.dayOfYear = dayOfYear;
        this.hour = hour;
        this.plans = plans;
        this.className = className;
        this.checkinMade = checkinMade;
        this.vacancy = vacancy;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Instant getTimestampUTC() {
        return timestampUTC;
    }

    public void setTimestampUTC(Instant timestampUTC) {
        this.timestampUTC = timestampUTC;
    }

    public OffsetDateTime getDatetimeUTC() {
        return datetimeUTC;
    }

    public void setDatetimeUTC(OffsetDateTime datetimeUTC) {
        this.datetimeUTC = datetimeUTC;
    }

    public Integer getDayOfYear() {
        return dayOfYear;
    }

    public void setDayOfYear(Integer dayOfYear) {
        this.dayOfYear = dayOfYear;
    }

    public String getHour() {
        return hour;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }

    public int[] getPlans() {
        return plans;
    }

    public void setPlans(int[] plans) {
        this.plans = plans;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public Boolean getCheckinMade() {
        return checkinMade;
    }

    public void setCheckinMade(Boolean checkinMade) {
        this.checkinMade = checkinMade;
    }

    public Boolean getWeekLimit() {
        return weekLimit;
    }

    public void setWeekLimit(Boolean weekLimit) {
        this.weekLimit = weekLimit;
    }

    public Integer getVacancy() {
        return vacancy;
    }

    public void setVacancy(Integer vacancy) {
        this.vacancy = vacancy;
    }
}
