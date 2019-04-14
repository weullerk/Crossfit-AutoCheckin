package com.alienonwork.crossfitcheckin.models;

import com.google.gson.annotations.SerializedName;

public class Checkin {
    String status;
    List[] list;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List[] getList() {
        return list;
    }

    public void setList(List[] list) {
        this.list = list;
    }

    public static class List {
        int id;
        long timestampUTC;
        String datetimeUTC;
        int dayOfYear;
        String hour;
        int[] plans;
        @SerializedName("class") String className;
        boolean checkinMade;
        boolean blocked;
        boolean weekLimit;
        int vacancy;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public long getTimestampUTC() {
            return timestampUTC;
        }

        public void setTimestampUTC(long timestampUTC) {
            this.timestampUTC = timestampUTC;
        }

        public String getDatetimeUTC() {
            return datetimeUTC;
        }

        public void setDatetimeUTC(String datetimeUTC) {
            this.datetimeUTC = datetimeUTC;
        }

        public int getDayOfYear() {
            return dayOfYear;
        }

        public void setDayOfYear(int dayOfYear) {
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

        public boolean isCheckinMade() {
            return checkinMade;
        }

        public void setCheckinMade(boolean checkinMade) {
            this.checkinMade = checkinMade;
        }

        public boolean isBlocked() {
            return blocked;
        }

        public void setBlocked(boolean blocked) {
            this.blocked = blocked;
        }

        public boolean isWeekLimit() {
            return weekLimit;
        }

        public void setWeekLimit(boolean weekLimit) {
            this.weekLimit = weekLimit;
        }

        public int getVacancy() {
            return vacancy;
        }

        public void setVacancy(int vacancy) {
            this.vacancy = vacancy;
        }
    }
}