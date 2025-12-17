package com.mayur.personalitydevelopment.models;

/**
 * Created by Admin on 5/15/2017.
 */

public class BaseData {
   // private boolean success;
    private int status_code;
    private String message;
    private Object data;
    private Object a;

    public Object getA() {
        return a;
    }

    public void setA(Object a) {
        this.a = a;
    }

    public int getCode() {
        return status_code;
    }

    public void setCode(int code) {
        this.status_code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
