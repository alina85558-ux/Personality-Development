package com.mayur.personalitydevelopment.bodyelements;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Image extends BodyElement {
    @SerializedName("url")
    @Expose
    public String url;
}
