package com.alienonwork.crossfitcheckin;

public class ClassModel {
    Integer id;
    Integer timestampUTC;
    String datetimeUTC;
    Integer dayOfYear;
    String hour;
    String description;
    Integer vacancy;

    public ClassModel(String description, String hour) {
        this.description = description;
        this.hour = hour;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getTimestampUTC() {
        return timestampUTC;
    }

    public void setTimestampUTC(Integer timestampUTC) {
        this.timestampUTC = timestampUTC;
    }

    public String getDatetimeUTC() {
        return datetimeUTC;
    }

    public void setDatetimeUTC(String datetimeUTC) {
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getVacancy() {
        return vacancy;
    }

    public void setVacancy(Integer vacancy) {
        this.vacancy = vacancy;
    }
}
