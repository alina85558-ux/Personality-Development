package com.mayur.personalitydevelopment.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Map;

public class CalenderData {

    @SerializedName("tracked_dates")
    @Expose
    private Map<String, Integer> trackedDates;
    @SerializedName("total_completed_days")
    @Expose
    private Integer totalCompletedDays;

    public Map<String, Integer> getTrackedDates() {
        return trackedDates;
    }

    public void setTrackedDates(Map<String, Integer> trackedDates) {
        this.trackedDates = trackedDates;
    }

    public Integer getTotalCompletedDays() {
        return totalCompletedDays;
    }

    public void setTotalCompletedDays(Integer totalCompletedDays) {
        this.totalCompletedDays = totalCompletedDays;
    }

}