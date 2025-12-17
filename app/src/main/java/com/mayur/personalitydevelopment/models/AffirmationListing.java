package com.mayur.personalitydevelopment.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AffirmationListing {

    boolean isSelected = true;
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("text")
    @Expose
    private String text;
    @SerializedName("affirmation_category_id")
    @Expose
    private Integer affirmationCategoryId;
    @SerializedName("affirmation_category")
    @Expose
    private String affirmationCategory;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Integer getAffirmationCategoryId() {
        return affirmationCategoryId;
    }

    public void setAffirmationCategoryId(Integer affirmationCategoryId) {
        this.affirmationCategoryId = affirmationCategoryId;
    }

    public String getAffirmationCategory() {
        return affirmationCategory;
    }

    public void setAffirmationCategory(String affirmationCategory) {
        this.affirmationCategory = affirmationCategory;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}