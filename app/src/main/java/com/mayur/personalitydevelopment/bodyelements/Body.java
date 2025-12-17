package com.mayur.personalitydevelopment.bodyelements;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Body {
    public int f2973a;
    @SerializedName("ads")
    @Expose
    public Ads ads;
    @SerializedName("blockquote")
    @Expose
    public Blockquote blockquote;
    @SerializedName("category")
    @Expose
    public String category;
    @SerializedName("h1")
    @Expose
    public H1 h1;
    @SerializedName("h2")
    @Expose
    public H2 h2;
    @SerializedName("h3")
    @Expose
    public H3 h3;
    @SerializedName("h4")
    @Expose
    public H4 h4;
    @SerializedName("h5")
    @Expose
    public H5 h5;
    @SerializedName("h6")
    @Expose
    public H6 h6;
    @SerializedName("image")
    @Expose
    public Image image;
    @SerializedName("paragraph")
    @Expose
    public Paragraph paragraph;
    @SerializedName("table")
    @Expose
    public Table table;
}
