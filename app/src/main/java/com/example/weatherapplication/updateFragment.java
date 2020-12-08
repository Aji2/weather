package com.example.weatherapplication;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.marcinmoskala.arcseekbar.ArcSeekBar;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class updateFragment extends AppCompatActivity {
    String id, title, author, pages;
    String apikey = "4898001760692a304b39e3a20aca1fdd";
    Button delete_button;
    TextView ucity,umin,umax,utempe,uhumidity,upressure,utemplev2,uspeed,udirection,usunset,usunrise;
    ProgressBar uprogressBar;
    ArcSeekBar uarcSeekBar;
    public static String BaseUrl = "https://api.openweathermap.org/";
    public static String AppId = "4898001760692a304b39e3a20aca1fdd";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_fragment);
        delete_button = findViewById(R.id.delete);
        ucity = findViewById(R.id.update_city);
        umin = findViewById( R.id.home_min );
        umax = findViewById( R.id.home_max );
        utempe = findViewById( R.id.home_temp );
        uhumidity = findViewById( R.id.home_humidity );
        upressure = findViewById( R.id.home_pressure );
        utemplev2 = findViewById( R.id.temp_level );
        uprogressBar = findViewById( R.id.progress_bar );
        uprogressBar.setMax(100);
        uspeed = findViewById( R.id.home_speed );
        udirection = findViewById( R.id.home_direction );
        usunset = findViewById( R.id.home_set );
        usunrise = findViewById( R.id.home_rise );
        uarcSeekBar = findViewById( R.id.seekbar );
        //First we call this
        getAndSetIntentData();

        //Set actionbar title after getAndSetIntentData method
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setTitle(title);
        }


        delete_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                confirmDialog();
            }
        });

    }
    void getAndSetIntentData(){
        if(getIntent().hasExtra("id") && getIntent().hasExtra("title") ){
            //Getting Data from Intent
            id = getIntent().getStringExtra("id");
            title = getIntent().getStringExtra("title");
            ucity.setText(title);

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BaseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            WeatherService service = retrofit.create(WeatherService.class);
            Call<WeatherResponse> call = service.getCurrentWeatherData1(title, AppId);
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

                        utempe.setText(temperature+"°C");
                        umin.setText(minimum_temperature+"°C");
                        umax.setText(maximum_temperature+"°C");
                        uhumidity.setText((int) weatherResponse.main.humidity +"%");
                        upressure.setText((int) weatherResponse.main.pressure +" RH");
                        utemplev2.setText(String.valueOf(temperature)+"°C");
                        uprogressBar.setProgress((int) weatherResponse.main.humidity );
                        uspeed.setText((int) weatherResponse.wind.speed +" mph");
                        int dir = (int) weatherResponse.wind.deg;

                        //direction.setText((int) weatherResponse.wind.deg );



                        if( (dir >= 348.75) && (dir <= 360) ||
                                (dir >= 0) && (dir <= 11.25)    ){
                            udirection.setText("North");
                        } else if( (dir >= 33.75 ) &&(dir <= 78.75)){
                            udirection.setText("North East");

                        } else if( (dir >= 78.75 ) && (dir <= 123.75) ){
                            udirection.setText("East");
                        } else if( (dir >= 123.75) && (dir <= 168.75) ){
                            udirection.setText("South East");
                        } else if( (dir >= 168.75) && (dir <= 213.75) ){
                            udirection.setText("South");
                        } else if( (dir >= 213.75) && (dir <= 258.75) ){
                            udirection.setText("South West");
                        } else if( (dir >= 258.75) && (dir <= 303.75) ){
                            udirection.setText("West");
                        } else if( (dir >= 303.75) && (dir <= 348.75) ){
                            udirection.setText("North West");
                        } else {
                            udirection.setText("Not Determined");
                        }
                        long srise = weatherResponse.sys.sunrise;
                        Date date = new Date(srise*1000L);
                        SimpleDateFormat jdf = new SimpleDateFormat("HH:mm:ss");
                        jdf.setTimeZone(TimeZone.getTimeZone("GMT-4"));
                        String java_date = jdf.format(date);
                        usunrise.setText(String.valueOf(java_date));

                        long sset = weatherResponse.sys.sunset;
                        Date date1 = new Date(sset*1000L);
                        SimpleDateFormat jdf1 = new SimpleDateFormat("HH:mm:ss");
                        jdf1.setTimeZone(TimeZone.getTimeZone("GMT-4"));
                        String java_date1 = jdf1.format(date1);
                        usunset.setText(String.valueOf(java_date1));


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

                        uarcSeekBar.setProgress(f);


                    }
                }

                @Override
                public void onFailure(@NonNull Call<WeatherResponse> call, @NonNull Throwable t) {

                }
            });



            Log.d("stev", title+" "+author+" "+pages);
        }else{
            Toast.makeText(this, "No data.", Toast.LENGTH_SHORT).show();
        }
    }

    void confirmDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete " + title + " ?");
        builder.setMessage("Are you sure you want to delete " + title + " ?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                MyDatabaseHelper myDB = new MyDatabaseHelper(updateFragment.this);
                myDB.deleteOneRow(id);
                finish();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.create().show();
    }
}