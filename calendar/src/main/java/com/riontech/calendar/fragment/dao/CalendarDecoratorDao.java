package com.riontech.calendar.fragment.dao;

/**
 * Created by Dhaval Soneji on 28/3/16.
 */
public class CalendarDecoratorDao {
    private String date;
    private int count;
    private int position;
    private boolean isSelected = false;

    public CalendarDecoratorDao(String date, int count) {
        this.date = date;
        this.count = count;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getPosition() {
        return position;
    }

    public String getDay() {
        String[] separatedTime = date.split("-");
        return separatedTime[2].replaceFirst("^0*", "");
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
