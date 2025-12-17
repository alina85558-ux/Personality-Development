package com.mayur.personalitydevelopment.bodyelements;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Blockquote extends BodyElement {
    @SerializedName("content_plain_text")
    @Expose
    public String contentPlainText;
}
