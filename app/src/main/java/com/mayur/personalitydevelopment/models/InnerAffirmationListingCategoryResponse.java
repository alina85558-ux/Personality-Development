package com.mayur.personalitydevelopment.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class InnerAffirmationListingCategoryResponse {

    @SerializedName("affirmations")
    private ArrayList<AffirmationListing> musics;

    public ArrayList<AffirmationListing> getMusics() {
        return musics;
    }

    public void setMusics(ArrayList<AffirmationListing> musics) {
        this.musics = musics;
    }
}
