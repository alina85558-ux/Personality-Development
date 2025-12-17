package com.mayur.personalitydevelopment.bodyelements;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Table extends BodyElement {

    @SerializedName("rows")
    @Expose
    public List<List<String>> rows = null;
}
