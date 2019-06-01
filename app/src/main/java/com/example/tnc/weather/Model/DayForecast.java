package com.example.tnc.weather.Model;

import android.net.Uri;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DayForecast implements Serializable {
    private int low;
    private int high;
    private String weatherName;
    private Date date;
    private String weatherIcon;
    private String forecastLink;

    public DayForecast() {
    }

    public String getForecastLink() {
        return forecastLink;
    }

    public void setForecastLink(String forecastLink) {
        this.forecastLink = forecastLink;
    }

    public String getDay(){
        SimpleDateFormat dateFormat= new SimpleDateFormat("dd");
        String day = dateFormat.format(date.getTime());
        return day;
    }
    public String getMonth(){
        SimpleDateFormat dateFormat=new SimpleDateFormat("EEE, MMMM");
        String month = dateFormat.format(date.getTime());
        return month;
    }
    public int getLow() {
        return low;
    }

    public void setLow(int low) {
        this.low = low;
    }

    public int getHigh() {
        return high;
    }

    public void setHigh(int high) {
        this.high = high;
    }

    public String getWeatherName() {
        return weatherName;
    }

    public void setWeatherName(String weatherName) {
        this.weatherName = weatherName;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getWeatherIcon() {
        return weatherIcon;
    }

    public void setWeatherIcon(String weatherIcon) {
        this.weatherIcon = weatherIcon;
    }
}
