package com.example.tnc.weather.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tnc.weather.Model.DayForecast;
import com.example.tnc.weather.R;
import com.example.tnc.weather.Tasks.CommonTask;

import java.util.ArrayList;

public class CustomAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<DayForecast> foreCastList;

    public CustomAdapter(Context context, ArrayList<DayForecast> foreCastList) {
        this.context = context;
        this.foreCastList = foreCastList;
    }

    @Override
    public int getCount() {
        return foreCastList.size();
    }

    @Override
    public Object getItem(int i) {
        return foreCastList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        view = layoutInflater.inflate(R.layout.forecast_layout,null);

        DayForecast dayForecast = foreCastList.get(i);

        TextView txtHigh = view.findViewById(R.id.txtHigh);
        TextView txtLow = view.findViewById(R.id.txtLow);
        TextView txtDay = view.findViewById(R.id.txtDay);
        TextView txtMonth = view.findViewById(R.id.txtMonth);
        TextView txtWeatherName = view.findViewById(R.id.txtWeathername);
        ImageView imgIcon = view.findViewById(R.id.imgIcon);

        txtHigh.setText(dayForecast.getHigh()+"");
        txtLow.setText(dayForecast.getLow()+"");
        txtDay.setText(dayForecast.getDay());
        txtMonth.setText(dayForecast.getMonth());
        txtWeatherName.setText(dayForecast.getWeatherName());
        CommonTask.setWeatherIcon(imgIcon,dayForecast.getWeatherIcon());
        return view;
    }

}
