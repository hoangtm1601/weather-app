package com.example.tnc.weather;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.tnc.weather.Adapter.CustomAdapter;
import com.example.tnc.weather.Model.DayForecast;
import com.example.tnc.weather.Model.Hourly;
import com.example.tnc.weather.Model.LocationInfo;
import com.facebook.share.model.ShareContent;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Forecast extends AppCompatActivity {
   // LocationInfo locationInfo;
    private ImageButton btnBack;
    private ListView listView;
    private ArrayList<DayForecast> forecastList;
    private final String ACCU_API_KEY="Mf6Yqscu2k12GXjWg4S6lHzsD2wO1vAo";
    private LocationInfo locationInfo;
    private TextView tvLocation, tvForecastText;


    private CustomAdapter customAdapter;
    ShareDialog shareDialog;
    ShareLinkContent shareLinkContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forecast);
        init();

        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(Forecast.this);

        Intent intent=getIntent();
        locationInfo= (LocationInfo) intent.getSerializableExtra("locationInfo");
        tvLocation.setText(locationInfo.getLocationName());
        System.out.println(locationInfo.getLocationKey());
        getWeatherForecast(locationInfo.getLocationKey());
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                PopupMenu popupMenu = new PopupMenu(Forecast.this,view);
                //Inflating the Popup using xml file
                popupMenu.getMenuInflater().inflate(R.menu.poupup_menu, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getItemId()==R.id.share){
                            if(ShareDialog.canShow(ShareLinkContent.class)){
                                shareLinkContent = new ShareLinkContent.Builder().setContentUrl(Uri.parse(forecastList.get(position).getForecastLink())).build();
                            }
                            shareDialog.show(shareLinkContent);
                        }
                        else if (item.getItemId()==R.id.web){
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(forecastList.get(position).getForecastLink()));
                            startActivity(browserIntent);
                        }
                        return false;
                    }
                });
                popupMenu.show();
            }
        });
    }

    public void init(){
        tvForecastText=findViewById(R.id.tvForecastText);
        tvLocation=findViewById(R.id.tvLocation);
        shareDialog = new ShareDialog(Forecast.this);
        btnBack=findViewById(R.id.btnBack);
        listView=findViewById(R.id.lvWeather);
        forecastList=new ArrayList<>();
        customAdapter=new CustomAdapter(Forecast.this,forecastList);
        listView.setAdapter(customAdapter);
    }

    public void getWeatherForecast(String locationKey){
        Toast.makeText(Forecast.this,"Waiting response for "+locationInfo.getLocationName(),Toast.LENGTH_SHORT).show();

        RequestQueue requestQueue = Volley.newRequestQueue(Forecast.this);
        String url = "http://dataservice.accuweather.com/forecasts/v1/daily/5day/"+locationKey+"?apikey="+ACCU_API_KEY+
                "&details=false&metric=true";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    //location
                    JSONObject rootObject = new JSONObject(response);
                    System.out.println(rootObject.toString());
                    {
                        JSONObject headLine = rootObject.getJSONObject("Headline");
                        String weatherText = headLine.getString("Text");
                        tvForecastText.setText(weatherText);
                    }
                    {
                        JSONArray forecastArray = rootObject.getJSONArray("DailyForecasts");
                        for (int i=0;i<forecastArray.length();i++){
                            DayForecast dayForecast = new DayForecast();

                            JSONObject arrayObject = forecastArray.getJSONObject(i);
                            Date date = new Date(1000L*arrayObject.getLong("EpochDate"));
                            dayForecast.setDate(date);
                            String forecastLink = arrayObject.getString("MobileLink");
                            dayForecast.setForecastLink(forecastLink);
                            JSONObject tempObject = arrayObject.getJSONObject("Temperature");
                                JSONObject minTemp = tempObject.getJSONObject("Minimum");
                                    int min = (int) minTemp.getDouble("Value");
                                    dayForecast.setLow(min);
                                JSONObject maxTemp = tempObject.getJSONObject("Maximum");
                                    int max = (int) maxTemp.getDouble("Value");
                                    dayForecast.setHigh(max);
                            JSONObject dayObject = arrayObject.getJSONObject("Day");
                                String icon = dayObject.getString("Icon");
                                dayForecast.setWeatherIcon(icon);
                                String weatherName = dayObject.getString("IconPhrase");
                                dayForecast.setWeatherName(weatherName);
                            forecastList.add(dayForecast);
                        }
                        customAdapter.notifyDataSetChanged();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        requestQueue.add(stringRequest);
    }

}
