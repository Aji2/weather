package com.example.weatherapplication;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements LocationListener{
    public static String BaseUrl = "https://api.openweathermap.org/";
    public static String AppId = "4898001760692a304b39e3a20aca1fdd";
    LocationManager locationManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Home_Fragment home = new Home_Fragment();
        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction().add(R.id.fragment_container,home,null).commit();

        grantPermission();
        checkLocationIsEnableorNot();
        getLocation();

      Intent serviceIntent = new Intent(this, NotificationServices.class);
       ContextCompat.startForegroundService(this, serviceIntent);
    }
    @Override
    public void onLocationChanged( Location location) {
        try{

            Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
            String city = (addresses.get(0).getLocality());

            Double latitude = addresses.get(0).getLatitude();
            int value1 = (int)(latitude-0);
            Double longitude = addresses.get(0).getLongitude();
            int value2 = (int)(longitude-0);
            String lati = Integer.toString(value1);
            String loni = Integer.toString(value2);


            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BaseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            WeatherService service = retrofit.create(WeatherService.class);
            Call<WeatherResponse> call = service.getCurrentWeatherData(lati, loni, AppId);
            call.enqueue(new Callback<WeatherResponse>() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onResponse(@NonNull Call<WeatherResponse> call, @NonNull Response<WeatherResponse> response) {
                    Log.d("myTag", String.valueOf(response.code()));
                    if (response.code() == 200) {
                        WeatherResponse weatherResponse = response.body();
                        assert weatherResponse != null;

                        Integer temperature = (int)( weatherResponse.main.temp-273.15);
                        String temp = Integer.toString(temperature) ;
                        Intent serviceIntent = new Intent(MainActivity.this, NotificationServices.class);
                        serviceIntent.putExtra("temp", temp);
                        serviceIntent.putExtra("city", city);

                        ContextCompat.startForegroundService(MainActivity.this, serviceIntent);

                    }
                }

                @Override
                public void onFailure(@NonNull Call<WeatherResponse> call, @NonNull Throwable t) {

                }
            });


        }catch(IOException e){
            e.printStackTrace();
        }

    }

    private void grantPermission() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION},100);

        }
    }

    private void checkLocationIsEnableorNot() {
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean gpsEnable = false;
        boolean networkEnable = false;

        try{

            gpsEnable = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);

        }catch(Exception e){
            e.printStackTrace();
        }
        try{
            networkEnable = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        }catch(Exception e){
            e.printStackTrace();
        }
        if(!gpsEnable && !networkEnable){
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Enable Gps Service")
                    .setCancelable(false)
                    .setPositiveButton("Enable", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        }
                    }).setNegativeButton("Cancel",null)
                    .show();
        }
    }

    private void getLocation() {
        try{
            locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,500,5,(LocationListener)this);
        }catch(SecurityException e){
            e.printStackTrace();
        }
    }
}