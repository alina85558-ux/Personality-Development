package com.mayur.personalitydevelopment.bodyelements;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Ads extends BodyElement {
    @SerializedName("size")
    @Expose
    public String size;
    @SerializedName("unit_id")
    @Expose
    public String unitId;
}
