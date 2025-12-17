package com.mayur.personalitydevelopment.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class InnerYoutubeCategoryResponse {

    @SerializedName("exercises")
    private ArrayList<YoutubeItem> musics;

    public ArrayList<YoutubeItem> getMusics() {
        return musics;
    }

    public void setMusics(ArrayList<YoutubeItem> musics) {
        this.musics = musics;
    }
}
