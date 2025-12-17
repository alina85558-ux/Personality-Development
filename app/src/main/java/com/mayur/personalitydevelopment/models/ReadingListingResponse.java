
package com.mayur.personalitydevelopment.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ReadingListingResponse {
    @SerializedName("articles")
    @Expose
    private ArrayList<Articles> data;



    public ArrayList<Articles> getData() {
        return data;
    }

    public void setData(ArrayList<Articles> data) {
        this.data = data;
    }

}
