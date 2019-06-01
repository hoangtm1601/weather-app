package com.example.tnc.weather.Tasks;

import android.net.Uri;
import android.widget.ImageView;
import android.widget.VideoView;

import com.example.tnc.weather.R;

public class CommonTask {

    public static void setWeatherIcon(ImageView imageView, String icon){
        switch (icon){
            case "1":{
                imageView.setImageResource(R.drawable.ic_01);
                break;
            }
            case "2":{
                imageView.setImageResource(R.drawable.ic_02);
                break;
            }
            case "3":{
                imageView.setImageResource(R.drawable.ic_03);
                break;
            }case "4":{
                imageView.setImageResource(R.drawable.ic_04);
                break;
            }case "5":{
                imageView.setImageResource(R.drawable.ic_05);
                break;
            }case "6":{
                imageView.setImageResource(R.drawable.ic_06);
                break;
            }case "7":{
                imageView.setImageResource(R.drawable.ic_07);
                break;
            }case "8":{
                imageView.setImageResource(R.drawable.ic_08);
                break;
            }case "11":{
                imageView.setImageResource(R.drawable.ic_11);
                break;
            }case "12":{
                imageView.setImageResource(R.drawable.ic_12);
                break;
            }case "13":{
                imageView.setImageResource(R.drawable.ic_13);
                break;
            }case "14":{
                imageView.setImageResource(R.drawable.ic_14);
                break;
            }case "15":{
                imageView.setImageResource(R.drawable.ic_15);
                break;
            }case "16":{
                imageView.setImageResource(R.drawable.ic_16);
                break;
            }case "17":{
                imageView.setImageResource(R.drawable.ic_17);
                break;
            }case "18":{
                imageView.setImageResource(R.drawable.ic_18);
                break;
            }case "19":{
                imageView.setImageResource(R.drawable.ic_19);
                break;
            }case "20":{
                imageView.setImageResource(R.drawable.ic_20);
                break;
            }case "21":{
                imageView.setImageResource(R.drawable.ic_21);
                break;
            }case "33":{
                imageView.setImageResource(R.drawable.ic_33);
                break;
            }case "34":{
                imageView.setImageResource(R.drawable.ic_34);
                break;
            }case "35":{
                imageView.setImageResource(R.drawable.ic_35);
                break;
            }case "36":{
                imageView.setImageResource(R.drawable.ic_36);
                break;
            }case "37":{
                imageView.setImageResource(R.drawable.ic_37);
                break;
            }case "38":{
                imageView.setImageResource(R.drawable.ic_38);
                break;
            }case "39":{
                imageView.setImageResource(R.drawable.ic_39);
                break;
            }case "40":{
                imageView.setImageResource(R.drawable.ic_40);
                break;
            }case "41":{
                imageView.setImageResource(R.drawable.ic_41);
                break;
            }
            case "42":{
                imageView.setImageResource(R.drawable.ic_42);
                break;
            }
        }
    }
}
