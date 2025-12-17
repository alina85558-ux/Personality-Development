package com.mayur.personalitydevelopment.models;

/**
 * Created by Admin on 2/26/2018.
 */

public class DATAHTML {
    public String type;
    public String value;

    public DATAHTML(String image, String src) {
        this.type = image;
        this.value = src;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}