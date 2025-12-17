package com.mayur.personalitydevelopment.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class InnerCategoryList {
    @SerializedName("course_categories")
    @Expose
    private ArrayList<CourseCategory> data;


    @SerializedName("trial_info")
    @Expose
    private TrialInfo trialInfo;


    public ArrayList<CourseCategory> getData() {
        return data;
    }

    public void setData(ArrayList<CourseCategory> data) {
        this.data = data;
    }

    public TrialInfo getTrialInfo() {
        return trialInfo;
    }

    public void setTrialInfo(TrialInfo trialInfo) {
        this.trialInfo = trialInfo;
    }

    public class TrialInfo {

        @SerializedName("access_message")
        @Expose
        private String accessMessage;

        @SerializedName("access_flag")
        @Expose
        private int accessFlag;

        public String getAccessMessage() {
            return accessMessage;
        }

        public void setAccessMessage(String accessMessage) {
            this.accessMessage = accessMessage;
        }

        public int getAccessFlag() {
            return accessFlag;
        }

        public void setAccessFlag(int accessFlag) {
            this.accessFlag = accessFlag;
        }
    }

}
