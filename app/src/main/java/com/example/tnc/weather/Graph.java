package com.example.tnc.weather;

import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.tnc.weather.Model.DayForecast;
import com.example.tnc.weather.Model.Hourly;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.DataPointInterface;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.OnDataPointTapListener;
import com.jjoe64.graphview.series.Series;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Graph extends AppCompatActivity {

    private VideoView videoView;
    private ArrayList<Hourly> listHourly;
    private ArrayList<DayForecast> listForecast;
    private ImageButton btnBack;
    private GraphView graph;
    private GraphView graphDay;

    @Override
    protected void onPause() {
        super.onPause();
        videoView.stopPlayback();
    }

    @Override
    protected void onResume() {
        super.onResume();
        videoView.resume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);
        init();
        Intent intent=getIntent();
        listHourly = (ArrayList<Hourly>) intent.getSerializableExtra("listHourly");
        listForecast = (ArrayList<DayForecast>) intent.getSerializableExtra("listForecast");
        String weatherIcon = intent.getStringExtra("weatherIcon");

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        setBackground(videoView,weatherIcon);
        videoView.start();
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true);
            }
        });

        //12h forecast
        graph = (GraphView) findViewById(R.id.graph);
        graph.getGridLabelRenderer().setNumHorizontalLabels(12);
        graph.getGridLabelRenderer().setGridStyle(GridLabelRenderer.GridStyle.NONE);
        graph.getViewport().setDrawBorder(true);
        graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(Graph.this, new SimpleDateFormat("HH")));
        graph.getGridLabelRenderer().setHumanRounding(false);

        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(getListPoint());
        series.setColor(Color.WHITE);
        graph.addSeries(series);

        series.setOnDataPointTapListener(new OnDataPointTapListener() {
            @Override
            public void onTap(Series series, DataPointInterface dataPoint) {
                Toast.makeText(Graph.this,dataPoint.getY()+" °C",Toast.LENGTH_SHORT ).show();
            }
        });

        //5 days forecast
        graphDay = findViewById(R.id.graphDay);
        graphDay.getGridLabelRenderer().setNumHorizontalLabels(5);
        graphDay.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(Graph.this, new SimpleDateFormat("dd/MM")));
        graphDay.getGridLabelRenderer().setHumanRounding(false);

        LineGraphSeries<DataPoint> minPoint = new LineGraphSeries<>(getMinPoint());
        minPoint.setColor(Color.BLUE);
        LineGraphSeries<DataPoint> maxPoint = new LineGraphSeries<>(getMaxPoint());
        maxPoint.setColor(Color.RED);
        graphDay.addSeries(minPoint);
        graphDay.addSeries(maxPoint);

    }

    private DataPoint[] getMinPoint(){
        DataPoint[] listPoint = new DataPoint[listForecast.size()];
        for (int i = 0;i< listForecast.size();i++){
            Date time = listForecast.get(i).getDate();
            int temp = listForecast.get(i).getLow();
            listPoint[i]=new DataPoint(time, temp);
        }
        return listPoint;
    }

    private DataPoint[] getMaxPoint(){
        DataPoint[] listPoint = new DataPoint[listForecast.size()];
        for (int i = 0;i< listForecast.size();i++){
            Date time = listForecast.get(i).getDate();
            int temp = listForecast.get(i).getHigh();
            listPoint[i]=new DataPoint(time, temp);
        }
        return listPoint;
    }

    private DataPoint[] getListPoint (){
        DataPoint[] listPoint = new DataPoint[listHourly.size()];
        for (int i =0;i<listHourly.size();i++){
            Date time = listHourly.get(i).getTime();
            int temp = Integer.parseInt(listHourly.get(i).getTemp());
            listPoint[i] = new DataPoint(time,temp);
        }
        return listPoint;
    }

    public void init(){
        videoView = findViewById(R.id.graphVideo);
        listForecast = new ArrayList<>();
        listHourly = new ArrayList<>();
        btnBack = findViewById(R.id.btnGraphBack);
    }

    public void setBackground(VideoView videoView, String icon){
        if (icon.equals("1") || icon.equals("2") || icon.equals("3") || icon.equals("4")
                || icon.equals("5") ){
            Uri uri= Uri.parse("android.resource://"+getPackageName()+"/"+R.raw.bg_video_clear);
            videoView.setVideoURI(uri);
        }else if (icon.equals("7") || icon.equals("6") || icon.equals("8") || icon.equals("9") || icon.equals("11")){
            Uri uri= Uri.parse("android.resource://"+getPackageName()+"/"+R.raw.bg_video_cloud);
            videoView.setVideoURI(uri);
        }else if (icon.equals("12") || icon.equals("13") || icon.equals("14") || icon.equals("15")
                || icon.equals("16") || icon.equals("17") || icon.equals("18") || icon.equals("39")
                || icon.equals("40") || icon.equals("41") || icon.equals("42")){
            Uri uri= Uri.parse("android.resource://"+getPackageName()+"/"+R.raw.bg_video_rain);
            videoView.setVideoURI(uri);
        }else if (icon.equals("34") || icon.equals("35")) {
            Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.bg_video_clear_night);
            videoView.setVideoURI(uri);
        }else if (icon.equals("36") || icon.equals("36") || icon.equals("37") || icon.equals("38") ) {
            Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.bg_video_cloud_night);
            videoView.setVideoURI(uri);
        }
    }
}
