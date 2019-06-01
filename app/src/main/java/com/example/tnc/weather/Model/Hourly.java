package com.example.tnc.weather.Model;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Hourly implements Serializable {
    private String icon;
    private String temp;
    private Date time;

    public Hourly() {
    }

    public String getHour(){
        SimpleDateFormat df = new SimpleDateFormat("HH");
        return df.format(time.getTime());
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getTemp() {
        return temp;
    }

    public void setTemp(String temp) {
        this.temp = temp;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }
}
