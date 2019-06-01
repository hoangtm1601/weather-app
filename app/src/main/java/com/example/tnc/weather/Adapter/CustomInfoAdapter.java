package com.example.tnc.weather.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tnc.weather.Model.WeatherMapObject;
import com.example.tnc.weather.R;
import com.example.tnc.weather.Tasks.CommonTask;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import org.json.JSONObject;

public class CustomInfoAdapter implements GoogleMap.InfoWindowAdapter {
    private Context mContext;
    private View mView;
    private WeatherMapObject weatherMapObject;
    public CustomInfoAdapter(Context mContext, WeatherMapObject weatherMapObject){
        this.weatherMapObject=weatherMapObject;
        this.mContext=mContext;
        mView = LayoutInflater.from(mContext).inflate(R.layout.info_window_layout,null);
    }

    @Override
    public View getInfoWindow(Marker marker) {
        TextView tvTemp = mView.findViewById(R.id.txtTemp);
        TextView tvWeatherName = mView.findViewById(R.id.txtWeathername);
        ImageView imgIcon = mView.findViewById(R.id.imgIcon);

        tvTemp.setText(weatherMapObject.getTemp()+" °C");
        tvWeatherName.setText(weatherMapObject.getWeatherText());
        CommonTask.setWeatherIcon(imgIcon,weatherMapObject.getIcon());
        return mView;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }
}
