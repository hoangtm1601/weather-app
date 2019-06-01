package com.example.tnc.weather.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tnc.weather.Model.Hourly;
import com.example.tnc.weather.R;
import com.example.tnc.weather.Tasks.CommonTask;

import java.util.ArrayList;

public class CustomRecycleAdapter extends RecyclerView.Adapter<CustomRecycleAdapter.ViewHolder> {

    private ArrayList<Hourly> listHourly;

    public CustomRecycleAdapter(ArrayList<Hourly> listHourly) {
        this.listHourly = listHourly;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater layoutInflater=LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.hourly_layout,parent,false);
        ViewHolder vh = new ViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Hourly hour = listHourly.get(position);
        holder.tvTemp.setText(hour.getTemp()+"°C");
        holder.tvHour.setText(hour.getHour()+"h");
        CommonTask.setWeatherIcon(holder.imgIcon,hour.getIcon());
    }


    @Override
    public int getItemCount() {
        return listHourly.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvHour;
        private TextView tvTemp;
        private ImageView imgIcon;
        public ViewHolder(View itemView) {
            super(itemView);
            tvHour=itemView.findViewById(R.id.tvHour);
            tvTemp=itemView.findViewById(R.id.tvTemp);
            imgIcon=itemView.findViewById(R.id.imgIcon);
        }
    }
}
