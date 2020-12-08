package com.example.weatherapplication.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weatherapplication.R;
import com.example.weatherapplication.updateFragment;

import java.util.ArrayList;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MyViewHolder> {

    private Context context;
    private Activity activity;
    private ArrayList city_id, city_name, city_pin, city_icon;

    public CustomAdapter(Activity activity, Context context, ArrayList city_id, ArrayList city_name, ArrayList city_pin,
                         ArrayList city_icon){
        this.activity = activity;
        this.context = context;
        this.city_id = city_id;
        this.city_name = city_name;
        this.city_pin = city_pin;
        this.city_icon = city_icon;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.my_row, parent, false);
        return new MyViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        holder.city_id_txt.setText("");
        holder.city_name_txt.setText(String.valueOf(city_name.get(position)));
        holder.city_pin_txt.setText(".......");
        holder.city_icon_txt.setText("");
        holder.city_icon_txt.setBackgroundResource(R.drawable.ic_location);
        //Recyclerview onClickListener
        holder.mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, updateFragment.class);
                intent.putExtra("id", String.valueOf(city_id.get(position)));
                intent.putExtra("title", String.valueOf(city_name.get(position)));
                intent.putExtra("author", String.valueOf(city_pin.get(position)));
                intent.putExtra("pages", String.valueOf(city_icon.get(position)));
                activity.startActivityForResult(intent, 1);
            }
        });


    }

    @Override
    public int getItemCount() {
        return city_id.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView city_id_txt, city_name_txt, city_pin_txt, city_icon_txt;
        LinearLayout mainLayout;

        MyViewHolder(@NonNull View itemView) {
            super(itemView);
            city_id_txt = itemView.findViewById(R.id.city_id_txt);
            city_name_txt = itemView.findViewById(R.id.city_name_txt);
            city_pin_txt = itemView.findViewById(R.id.city_pin_txt);
            city_icon_txt = itemView.findViewById(R.id.city_icon_txt);
            mainLayout = itemView.findViewById(R.id.mainLayout);
            //Animate Recyclerview
            Animation translate_anim = AnimationUtils.loadAnimation(context, R.anim.translate_anim);
            mainLayout.setAnimation(translate_anim);
        }

    }

}