package com.example.tnc.weather.Fragment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.tnc.weather.Adapter.CustomInfoAdapter;
import com.example.tnc.weather.Model.WeatherMapObject;
import com.example.tnc.weather.R;
import com.example.tnc.weather.Weather;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MapsFragment extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FloatingActionButton btnLocation;
    private final String ACCU_API_KEY="Mf6Yqscu2k12GXjWg4S6lHzsD2wO1vAo";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        btnLocation=findViewById(R.id.floatingActionButton);
       // Intent intent = getIntent();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment_map);
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                String city=place.getName().toString();

                Toast.makeText(MapsFragment.this,"Waiting response for "+city,Toast.LENGTH_SHORT).show();

                LatLng marker = new LatLng(place.getLatLng().latitude, place.getLatLng().longitude);
                mMap.addMarker(new MarkerOptions().position(marker).title("Marker in "+place.getName().toString()));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(marker,13));
            }

            @Override
            public void onError(Status status) {
                //Toast.makeText(Weather.this,status.toString(),Toast.LENGTH_SHORT).show();
            }
        });


    }

    private class locationAsync extends AsyncTask<Double, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Double... Doubles) {
            Double lat = Doubles[0];
            Double lon = Doubles[1];

            RequestQueue requestQueue = Volley.newRequestQueue(MapsFragment.this);
            String url = "http://dataservice.accuweather.com/locations/v1/cities/geoposition/search?apikey="+ACCU_API_KEY+"&q="+lat.toString()+"%2C"+lon.toString();
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        //location
                        JSONObject rootObject = new JSONObject(response);
                        String key =rootObject.getString("Key");
                        getCurrentWeather(key);
                        System.out.println("info adapter");
                        Toast.makeText(MapsFragment.this,key,Toast.LENGTH_SHORT).show();
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
        RequestQueue requestQueue = Volley.newRequestQueue(MapsFragment.this);
        String url = "http://dataservice.accuweather.com/currentconditions/v1/"+locationKey+"?apikey="+ACCU_API_KEY+"&details=true";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    //location
                    JSONArray jsonArray = new JSONArray(response);
                    JSONObject rootObject = jsonArray.getJSONObject(0);
                    WeatherMapObject weatherMapObject = new WeatherMapObject();

                    String weatherText = rootObject.getString("WeatherText");
                    String weatherIcon = rootObject.getString("WeatherIcon");
                    JSONObject tempArray = rootObject.getJSONObject("Temperature");
                    JSONObject metricObject = tempArray.getJSONObject("Metric");
                    int weatherTemp = (int) Float.parseFloat( metricObject.getString("Value"));
                    weatherMapObject.setIcon(weatherIcon);
                    weatherMapObject.setWeatherText(weatherText);
                    weatherMapObject.setTemp(weatherTemp);

                    CustomInfoAdapter adapter = new CustomInfoAdapter(MapsFragment.this,weatherMapObject);
                    mMap.setInfoWindowAdapter(adapter);

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


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng hanoi = new LatLng(21.027, 105.834);
        mMap.addMarker(new MarkerOptions().position(hanoi).title("Marker in Hanoi"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(hanoi,13));

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                mMap.clear();
                Marker marker = mMap.addMarker(new MarkerOptions().position(latLng));
                marker.showInfoWindow();
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                new locationAsync().execute(latLng.latitude,latLng.longitude);
            }
        });

        btnLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(MapsFragment.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MapsFragment.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                    Toast.makeText(MapsFragment.this,"Please grant permission to access GPS",Toast.LENGTH_SHORT).show();
                    return;
                }
                LocationManager locationManager = (LocationManager) getSystemService(Weather.LOCATION_SERVICE);
                Location lc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                LatLng currentLatLng = new LatLng(lc.getLatitude(),lc.getLongitude());
                MarkerOptions markerOptions = new MarkerOptions().position(currentLatLng);
                Marker currentMarker = mMap.addMarker(markerOptions);
                currentMarker.showInfoWindow();
                mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLatLng));
            }
        });
    }
}
