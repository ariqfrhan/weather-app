package maf.mobile.weatherapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.LocationCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private TextView longitude;
    private TextView latitude;
    private TextView city;
    private TextView temperature;
    private TextView condition;
    private TextView wind;
    private ImageView icon;
    private ArrayList<Weather> weatherData;
    private WeatherAdapter weatherAdapter;
    private RecyclerView weatherRV;
    private ImageView backgroundImg;
    private EditText etSearch;
    private Button btSearch;
    public double lat = -7.92;
    public double lon = 112.62;
    private View home;
    private View progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        home = findViewById(R.id.homeScreen);
        progressBar  = findViewById(R.id.progressBar);
        longitude = (TextView) findViewById(R.id.tvLongitude);
        latitude = (TextView) findViewById(R.id.tvLatitude);
        city = (TextView) findViewById(R.id.tvCity);
        temperature = (TextView) findViewById(R.id.tvTemperature);
        icon = (ImageView) findViewById(R.id.ivIcon);
        condition = (TextView) findViewById(R.id.tvCondition);
        wind = (TextView) findViewById(R.id.tvWind);
        backgroundImg = (ImageView) findViewById(R.id.ivBackground);
        weatherRV = (RecyclerView) findViewById(R.id.rvWeather);
        etSearch = (EditText) findViewById(R.id.etSearch);
        btSearch = (Button) findViewById(R.id.btSearch);

        weatherData = new ArrayList<>();
        weatherAdapter = new WeatherAdapter(this, weatherData);
        weatherRV.setAdapter(weatherAdapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        weatherRV.setLayoutManager(layoutManager);


        getWeather(lat, lon);
        
    }

    private void getWeather(double lat, double lon){
        String url = "https://api.open-meteo.com/v1/forecast?latitude="+lat+"&longitude="+lon+"&daily=weathercode&current_weather=true&timezone=auto";
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                progressBar.setVisibility(View.GONE);
                home.setVisibility(View.VISIBLE);

                try {
                    int isDay = response.getJSONObject("current_weather").getInt("is_day");
                    if (isDay == 0) {
                        backgroundImg.setImageResource(R.drawable.night);
                    }else{
                        backgroundImg.setImageResource(R.drawable.day);
                    }

                    String getLat = response.getString("latitude");
                    latitude.setText(getLat);
                    
                    String getLong = response.getString("longitude");
                    longitude.setText(getLong);

                    double lat = Double.parseDouble(getLat);
                    double lon = Double.parseDouble(getLong);
                    Geocoder gcd = new Geocoder(MainActivity.this, Locale.getDefault());
                    try{
                        List<Address> addresses = gcd.getFromLocation(lat,lon,1);
                        if (addresses.size() >0) {
                            String kota = "";
                            for (Address addr : addresses) {
                                if (addr.getSubAdminArea() != null && !addr.getSubAdminArea().isEmpty()) {
                                    kota = addr.getSubAdminArea();
                                    break;
                                } else if (addr.getLocality() != null && !addr.getLocality().isEmpty()) {
                                    kota = addr.getLocality();
                                    break;
                                }
                            }
                            city.setText(kota);
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                    String getTemp = response.getJSONObject("current_weather").getString("temperature");
                    String tempUnit = response.getJSONObject("current_weather_units").getString("temperature");
                    temperature.setText(getTemp + tempUnit);
                    
                    int getCode = response.getJSONObject("current_weather").getInt("weathercode");
                    getCondition(getCode);

                    String getWind = response.getJSONObject("current_weather").getString("windspeed");
                    String windUnit = response.getJSONObject("current_weather_units").getString("windspeed");
                    wind.setText(getWind + windUnit);

                    JSONObject daily = response.getJSONObject("daily");
                    JSONArray time = daily.getJSONArray("time");
                    JSONArray weatherCode = daily.getJSONArray("weathercode");

                    int length = Math.min(time.length(), weatherCode.length());

                    for (int i = 0; i < length; i++) {
                        String timeAct = time.getString(i);
                        int weathAct = weatherCode.getInt(i);
                        Weather weather = new Weather(timeAct, weathAct);
                        weatherData.add(weather);
                    }
                    weatherAdapter.notifyDataSetChanged();

                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        requestQueue.add(jsonObjectRequest);
    }

    private void getCondition(int getCode) {
        if (getCode >= 95) {
            condition.setText("Thunderstorm");
            icon.setImageResource(R.drawable.thunderstorm);
        } else if (getCode >= 85) {
            condition.setText("Snow Showers");
            icon.setImageResource(R.drawable.snow);
        } else if (getCode >= 80) {
            condition.setText("Rain showers : violent");
            icon.setImageResource(R.drawable.storm);
        } else if (getCode >= 61) {
            condition.setText("Rain: heavy intensity");
            icon.setImageResource(R.drawable.heavyrain);
        } else if (getCode >= 45) {
            condition.setText("Cloudy");
            icon.setImageResource(R.drawable.cloudyday);
        } else{
            condition.setText("Clear Sky");
            icon.setImageResource(R.drawable.sunny);
        }
    }

    private double getLatitude(String location){
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addressList;

        try{
            addressList = geocoder.getFromLocationName(location, 10);
            if (addressList != null) {
                lat = addressList.get(0).getLatitude();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return lat;
    }

    private double getLongitude(String location){
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addressList;

        try{
            addressList = geocoder.getFromLocationName(location, 10);
            if (addressList != null) {
                lon = addressList.get(0).getLongitude();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return lon;
    }

    public void searchLocation(View view) {
        progressBar.setVisibility(View.VISIBLE);
        String loc = etSearch.getText().toString();
        double latitude = getLatitude(loc);
        double longitude = getLongitude(loc);
        getWeather(latitude, longitude);
    }
}