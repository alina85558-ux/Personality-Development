package com.mayur.personalitydevelopment.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class InnerMusicCategoryResponse {

    @SerializedName("musics")
    private ArrayList<MusicItem> musics;

    public ArrayList<MusicItem> getMusics() {
        return musics;
    }

    public void setMusics(ArrayList<MusicItem> musics) {
        this.musics = musics;
    }
}
