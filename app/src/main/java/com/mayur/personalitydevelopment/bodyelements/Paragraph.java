package com.mayur.personalitydevelopment.bodyelements;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Paragraph extends BodyElement {
    @SerializedName("content_html")
    @Expose
    public String contentHtml;
    @SerializedName("content_plain_text")
    @Expose
    public String contentPlainText;
}
