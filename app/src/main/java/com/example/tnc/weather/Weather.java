package com.example.tnc.weather;

import com.example.tnc.weather.Model.DayForecast;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.tnc.weather.Adapter.CustomRecycleAdapter;
import com.example.tnc.weather.Fragment.MapsFragment;
import com.example.tnc.weather.Model.Hourly;
import com.example.tnc.weather.Model.LocationInfo;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class Weather extends AppCompatActivity {
    private String weatherIcon;
    private TextView currentDay;
    private ImageButton btnLocation, btnReload;
    private Button
            btnForecast, btnGraph, btnMap;
    private ArrayList<Hourly> listHourly;
    private ArrayList<DayForecast> forecastList;
    private RecyclerView rHourList;
    private final String ACCU_API_KEY="Mf6Yqscu2k12GXjWg4S6lHzsD2wO1vAo";
    private String locationKEY="353412";
    private Button btnShareFB;
    //opZyTFhYKNVU7nEbP8MJTOnPalNAwmge ||  Mf6Yqscu2k12GXjWg4S6lHzsD2wO1vAo || AFMDeJ27w6UOR0UtCEfwnRsuSXuoIPdV || ytNrrOK5Jsa2zCQzy7PhED4Fapj57oEd
    private CustomRecycleAdapter recycleAdapter;
    private TextView tvWeatherText, tvWind, tvHumidity, tvPressure, tvRealFeel, tvTemp, tvLocation, tvLastUpdate;
    private LocationInfo locationInfo;
    private String currentWeatherLink = "";
    private VideoView videoView;
    ShareDialog shareDialog;
    ShareLinkContent shareLinkContent;

    @Override
    protected void onResume() {
        super.onResume();
        videoView.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        videoView.stopPlayback();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(Weather.this);
        //init Components
        init();

        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true);
            }
        });

        listHourly= new ArrayList<>();
        forecastList = new ArrayList<>();

        LinearLayoutManager horizontalLayoutManager =
                new LinearLayoutManager(Weather.this, LinearLayoutManager.HORIZONTAL, false);
        rHourList=findViewById(R.id.rHourList);
        rHourList.setLayoutManager(horizontalLayoutManager);

        recycleAdapter = new CustomRecycleAdapter(listHourly);
        rHourList.setAdapter(recycleAdapter);


        if (ActivityCompat.checkSelfPermission(Weather.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(Weather.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            Toast.makeText(Weather.this,"Please grant permission to access GPS",Toast.LENGTH_SHORT).show();
            return;
        }else{
            try{
                LocationManager locationManager = (LocationManager) getSystemService(Weather.LOCATION_SERVICE);
                Location lc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                Toast.makeText(Weather.this,"Determining location from GPS",Toast.LENGTH_LONG).show();
                new locationAsync().execute(lc.getLatitude(),lc.getLongitude());
            }catch (NullPointerException e){
                Toast.makeText(Weather.this,"Cannot determine location, use default location",Toast.LENGTH_SHORT).show();
                getHourlyForecastWeather(locationKEY);
                getCurrentWeather(locationKEY);
                getWeatherForecast(locationKEY);
                setLastUpdate();
                e.printStackTrace();
            }
        }


        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                String city=place.getName().toString();
                Toast.makeText(Weather.this,"Waiting response for "+city,Toast.LENGTH_SHORT).show();
                new locationAsync().execute(place.getLatLng().latitude,place.getLatLng().longitude);
            }

            @Override
            public void onError(Status status) {
                Toast.makeText(Weather.this,status.toString(),Toast.LENGTH_SHORT).show();
            }
        });


        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Date date = new Date();
                        SimpleDateFormat sdf2 = new SimpleDateFormat("E, MMMM d");
                        //currentTime.setText(sdf.format(date.getTime()));
                        currentDay.setText(sdf2.format(date.getTime()));
                    }
                });
            }
        }, 0, 3600*1000);

        btnLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(Weather.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(Weather.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                    Toast.makeText(Weather.this,"Please grant permission to access GPS",Toast.LENGTH_SHORT).show();
                    return;
                }else{
                    try{
                        LocationManager locationManager = (LocationManager) getSystemService(Weather.LOCATION_SERVICE);
                        Location lc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        Toast.makeText(Weather.this,"Determining location from GPS",Toast.LENGTH_LONG).show();
                        new locationAsync().execute(lc.getLatitude(),lc.getLongitude());
                    }catch (NullPointerException e){
                        Toast.makeText(Weather.this,"Cannot determine location, please search your city by name",Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }
            }
        });


        btnReload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!locationKEY.equals("") && locationKEY!=null){
                    listHourly.clear();
                    getCurrentWeather(locationKEY);
                    getHourlyForecastWeather(locationKEY);
                    setLastUpdate();
                }
            }
        });

        btnGraph.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Weather.this,Graph.class);
                intent.putExtra("listHourly", listHourly);
                intent.putExtra("listForecast", forecastList);
                intent.putExtra("weatherIcon", weatherIcon);
                System.out.println(forecastList.size());
                System.out.println(listHourly.size());
                startActivity(intent);
            }
        });

        btnForecast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Weather.this,Forecast.class);
                intent.putExtra("locationInfo",locationInfo);
                System.out.println("intent");
                startActivity(intent);
            }
        });

        btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Weather.this,MapsFragment.class);
                startActivity(intent);
            }
        });

        btnShareFB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ShareDialog.canShow(ShareLinkContent.class)){
                    shareLinkContent = new ShareLinkContent.Builder().setContentUrl(Uri.parse(currentWeatherLink)).build();
                }
                shareDialog.show(shareLinkContent);
            }
        });

    }


    public void init(){
        btnGraph = findViewById(R.id.btnGraph);
        shareDialog = new ShareDialog(Weather.this);
        btnShareFB=findViewById(R.id.btnShareFB);
        btnMap = findViewById(R.id.btnMap);
        btnForecast=findViewById(R.id.btnForecast);
        btnReload = findViewById(R.id.btnReload);
        btnLocation=findViewById(R.id.btnLocation);
       // currentTime=findViewById(R.id.tvTime);
        currentDay=findViewById(R.id.txtNgay);
       // imgWeatherIcon = findViewById(R.id.imgWeatherIcon);

        videoView = findViewById(R.id.videoView);

        tvLastUpdate=findViewById(R.id.tvLastupdate);
        tvTemp=findViewById(R.id.tvTemp);
        tvHumidity=findViewById(R.id.tvHumidity);
        tvPressure=findViewById(R.id.tvPressure);
        tvRealFeel=findViewById(R.id.tvRealfeel);
        tvWind=findViewById(R.id.tvWind);
        tvWeatherText=findViewById(R.id.tvWeatherText);
        tvLocation=findViewById(R.id.tvLocation);

        locationInfo = new LocationInfo(locationKEY,"Hanoi, VN");


    }


    private class locationAsync extends AsyncTask<Double, Void, Void>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Double... Doubles) {
            Double lat = Doubles[0];
            Double lon = Doubles[1];

            RequestQueue requestQueue = Volley.newRequestQueue(Weather.this);
            String url = "http://dataservice.accuweather.com/locations/v1/cities/geoposition/search?apikey="+ACCU_API_KEY+
                    "&q="+lat.toString()+"%2C"+lon.toString();
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        //location
                        JSONObject rootObject = new JSONObject(response);
                        String key =rootObject.getString("Key");
                        locationKEY=key;
                        String location = rootObject.getString("LocalizedName");
                        JSONObject countryObject = rootObject.getJSONObject("Country");
                        location +=", "+countryObject.getString("ID");
                        tvLocation.setText(location);
                        locationInfo.setLocationKey(locationKEY);
                        locationInfo.setLocationName(location);
                        getHourlyForecastWeather(key);
                        getCurrentWeather(key);
                        getWeatherForecast(key);
                        setLastUpdate();

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
            return null;
        }
    }


    public void getCurrentWeather(String locationKey){
        System.out.println("current");
        RequestQueue requestQueue = Volley.newRequestQueue(Weather.this);
        String url = "http://dataservice.accuweather.com/currentconditions/v1/"+locationKey+"?apikey="+ACCU_API_KEY
                +"&details=true";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    //location
                    JSONArray jsonArray = new JSONArray(response);
                    JSONObject rootObject = jsonArray.getJSONObject(0);

                    currentWeatherLink=rootObject.getString("Link");
                    System.out.println(currentWeatherLink);
                    String weatherText = rootObject.getString("WeatherText");
                    tvWeatherText.setText(weatherText);

                    weatherIcon = rootObject.getString("WeatherIcon");
                    setBackground(videoView,weatherIcon);
                    videoView.start();
                   // CommonTask.setWeatherIcon(imgWeatherIcon,weatherIcon);

                    String weatherHumidity = rootObject.getString("RelativeHumidity");
                    tvHumidity.setText(weatherHumidity+" %");

                    {
                        JSONObject tempArray = rootObject.getJSONObject("Temperature");
                        JSONObject metricObject = tempArray.getJSONObject("Metric");
                        int weatherTemp = (int) Float.parseFloat( metricObject.getString("Value"));
                        tvTemp.setText(weatherTemp+"");
                    }

                    {
                        JSONObject realFeelArray = rootObject.getJSONObject("RealFeelTemperature");
                        JSONObject metricRealFeel =realFeelArray.getJSONObject("Metric");
                        int weatherRealFeelTemp =(int) Float.parseFloat(metricRealFeel.getString("Value"));
                        tvRealFeel.setText(weatherRealFeelTemp+"");
                    }

                    {
                        JSONObject pressureArray = rootObject.getJSONObject("Pressure");
                        JSONObject metricPressure = pressureArray.getJSONObject("Metric");
                        int weatherPressure = (int) Float.parseFloat(metricPressure.getString("Value"));
                        tvPressure.setText(weatherPressure+" mb");
                    }

                    {
                        JSONObject windObject = rootObject.getJSONObject("Wind");
                        JSONObject speedObject = windObject.getJSONObject("Speed");
                        JSONObject metricSpeed = speedObject.getJSONObject("Metric");
                        int speed = (int) Float.parseFloat(metricSpeed.getString("Value"));
                        tvWind.setText(speed+" km/h");
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


    public void setLastUpdate(){
        Date date = new Date();
        SimpleDateFormat df = new SimpleDateFormat("HH:mm dd/MM");
        tvLastUpdate.setText(df.format(date.getTime()));
    }

    public void getHourlyForecastWeather(String locationKey){
        listHourly.clear();
        RequestQueue requestQueue = Volley.newRequestQueue(Weather.this);
        String url = "http://dataservice.accuweather.com/forecasts/v1/hourly/12hour/"+locationKey+"?apikey="+ACCU_API_KEY
                +"&metric=true";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    //location
                    JSONArray jsonArray = new JSONArray(response);
                    System.out.println(jsonArray.toString());
                    for (int i=0;i<jsonArray.length();i++){
                        JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                        String icon = jsonObject.getString("WeatherIcon");
                        long date = jsonObject.getLong("EpochDateTime");
                        JSONObject js1 = jsonObject.getJSONObject("Temperature");
                        String temp = js1.getString("Value");
                        int temp_int =(int) Float.parseFloat(temp);

                        Hourly hour = new Hourly();
                        hour.setIcon(icon);
                        Date date1 = new Date(1000L*date);
                        hour.setTime(date1);
                        hour.setTemp(temp_int+"");

                        listHourly.add(hour);
                    }
                    recycleAdapter.notifyDataSetChanged();
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
        System.out.println(listHourly.size()+ "size on function");
    }

    public void getWeatherForecast(String locationKey){
        forecastList.clear();
        RequestQueue requestQueue = Volley.newRequestQueue(Weather.this);
        String url = "http://dataservice.accuweather.com/forecasts/v1/daily/5day/"+locationKey+"?apikey="+ACCU_API_KEY+
                "&details=false&metric=true";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    //location
                    JSONObject rootObject = new JSONObject(response);

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
