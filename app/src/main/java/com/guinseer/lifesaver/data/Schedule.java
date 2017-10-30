package com.guinseer.lifesaver.data;

import android.view.View;

/**
 * Created by chou6 on 2017-06-11.
 */

public class Schedule {
    private String name = "";
    private String place = "";
    private String day = "";
    private String apm = "";
    private int hour, min;
    private boolean checked = false;
    private View v;

    public Schedule(String name, String place, String day, String apm, int hour, int min) {
        this.name = name;
        this.place = place;
        this.day = day;
        this.hour = hour;
        this.min = min;
        this.apm = apm;
    }

    public void setTable(String name, String place, String day, String apm, int hour, int min) {
        this.name = name;
        this.place = place;
        this.day = day;
        this.hour = hour;
        this.min = min;
        this.apm = apm;
    }


    public String getName() {
        return name;
    }

    public String getPlace() {
        return place;
    }

    public String getDay() {
        return day;
    }

    public int getHour() {
        return hour;
    }

    public int getMin() {
        return min;
    }

    public boolean getChecked() {
        return checked;
    }

    public View getV() {
        return v;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public void setV(View v) {
        this.v = v;
    }

    public String getApm() {
        return apm;
    }
}
