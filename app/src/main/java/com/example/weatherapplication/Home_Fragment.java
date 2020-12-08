package com.example.weatherapplication;

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

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.marcinmoskala.arcseekbar.ArcSeekBar;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class Home_Fragment extends Fragment implements LocationListener {

    TextView city,min,max,tempe,cloud,humidity,pressure,templev2,speed,direction,hlati,hlongi,sunset,sunrise;
    LocationManager locationManager;
    ProgressBar progressBar;
    ArcSeekBar arcSeekBar;
    public static String BaseUrl = "https://api.openweathermap.org/";
    public static String AppId = "4898001760692a304b39e3a20aca1fdd";

    public Home_Fragment() {

    }




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_item, menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item2:
                ManageCitiesFragments manageCitiesFragments = new ManageCitiesFragments();
                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, manageCitiesFragments).addToBackStack("First").commit();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        city = view.findViewById( R.id.home_city );
        min = view.findViewById( R.id.home_min );
        max = view.findViewById( R.id.home_max );
        tempe = view.findViewById( R.id.home_temp );
        cloud = view.findViewById( R.id.home_cloud );
        humidity = view.findViewById( R.id.home_humidity );
        pressure = view.findViewById( R.id.home_pressure );
        templev2 = view.findViewById( R.id.temp_level );
        progressBar = view.findViewById( R.id.progress_bar );
        progressBar.setMax(100);
        speed = view.findViewById( R.id.home_speed );
        direction = view.findViewById( R.id.home_direction );
        hlati = view.findViewById( R.id.home_lati );
        hlongi = view.findViewById( R.id.home_longi );
        sunset = view.findViewById( R.id.home_set );
        sunrise = view.findViewById( R.id.home_rise );
        arcSeekBar = view.findViewById( R.id.seekbar );



        grantPermission();
        checkLocationIsEnableorNot();
        getLocation();

        return view;
    }

    @Override
    public void onLocationChanged( Location location) {
        try{

            Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);


            city.setText((addresses.get(0).getLocality()));
            Double latitude = addresses.get(0).getLatitude();
            int value1 = (int)(latitude-0);
            Double longitude = addresses.get(0).getLongitude();
            int value2 = (int)(longitude-0);
            String lati = Integer.toString(value1);
            String loni = Integer.toString(value2);
            hlati.setText(String.valueOf(value1)+"° Latitude");
            hlongi.setText(String.valueOf(value2)+"° Longitude");

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
                        Integer minimum_temperature = (int)( weatherResponse.main.temp_min-273.15);
                        Integer maximum_temperature = (int)(weatherResponse.main.temp_max-273.15);

                        tempe.setText(String.valueOf(temperature)+"°C");
                        min.setText(String.valueOf( minimum_temperature)+"°C");
                        max.setText(String.valueOf(maximum_temperature)+"°C");
                        humidity.setText((int) weatherResponse.main.humidity +"%");
                        pressure.setText((int) weatherResponse.main.pressure +" RH");
                        templev2.setText(String.valueOf(temperature)+"°C");
                        progressBar.setProgress((int) weatherResponse.main.humidity );
                        speed.setText((int) weatherResponse.wind.speed +" mph");
                        int dir = (int) weatherResponse.wind.deg;

                        //direction.setText((int) weatherResponse.wind.deg );



                        if( (dir >= 348.75) && (dir <= 360) ||
                                (dir >= 0) && (dir <= 11.25)    ){
                            direction.setText("North");
                        } else if( (dir >= 33.75 ) &&(dir <= 78.75)){
                            direction.setText("North East");

                        } else if( (dir >= 78.75 ) && (dir <= 123.75) ){
                            direction.setText("East");
                        } else if( (dir >= 123.75) && (dir <= 168.75) ){
                            direction.setText("South East");
                        } else if( (dir >= 168.75) && (dir <= 213.75) ){
                            direction.setText("South");
                        } else if( (dir >= 213.75) && (dir <= 258.75) ){
                            direction.setText("South West");
                        } else if( (dir >= 258.75) && (dir <= 303.75) ){
                            direction.setText("West");
                        } else if( (dir >= 303.75) && (dir <= 348.75) ){
                            direction.setText("North West");
                        } else {
                            direction.setText("Not Determined");
                        }
                        long srise = weatherResponse.sys.sunrise;
                        Date date = new Date(srise*1000L);
                        SimpleDateFormat jdf = new SimpleDateFormat("HH:mm:ss");
                        jdf.setTimeZone(TimeZone.getTimeZone("GMT-4"));
                        String java_date = jdf.format(date);
                        sunrise.setText(String.valueOf(java_date));

                        long sset = weatherResponse.sys.sunset;
                        Date date1 = new Date(sset*1000L);
                        SimpleDateFormat jdf1 = new SimpleDateFormat("HH:mm:ss");
                        jdf1.setTimeZone(TimeZone.getTimeZone("GMT-4"));
                        String java_date1 = jdf1.format(date1);
                        sunset.setText(String.valueOf(java_date1));


                        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
                        LocalDateTime now = LocalDateTime.now();
                        String timestampStr = dtf.format(now);
                        String[] tokens = timestampStr.split(":");
                        int hours = Integer.parseInt(tokens[0]);
                        int minutes = Integer.parseInt(tokens[1]);
                        int seconds = Integer.parseInt(tokens[2]);
                        int duration = 3600 * hours + 60 * minutes + seconds;

                        int t = duration;
                        int f = t/10000 * 11;

                        arcSeekBar.setProgress(f);


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
        if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION},100);

        }
    }

    private void checkLocationIsEnableorNot() {
        LocationManager lm = (LocationManager) getActivity().getSystemService( Context.LOCATION_SERVICE);
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
            new AlertDialog.Builder(getActivity())
                    .setTitle("Enable Gps Service")
                    .setCancelable(false)
                    .setPositiveButton("Enable", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startActivity(new Intent( Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        }
                    }).setNegativeButton("Cancel",null)
                    .show();
        }
    }

    private void getLocation() {
        try{
            locationManager = (LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,500,5,(LocationListener)this);
        }catch(SecurityException e){
            e.printStackTrace();
        }
    }

}