package com.mayur.personalitydevelopment.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class InnerAffirmationCategoryResponse {

    @SerializedName("affirmation_categories")
    private ArrayList<Affirmation> musics;

    public ArrayList<Affirmation> getMusics() {
        return musics;
    }

    public void setMusics(ArrayList<Affirmation> musics) {
        this.musics = musics;
    }
}
