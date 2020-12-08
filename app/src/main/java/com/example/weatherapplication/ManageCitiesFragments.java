package com.example.weatherapplication;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.weatherapplication.Adapter.CustomAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;


public class ManageCitiesFragments extends Fragment {

    FloatingActionButton btn_plus;
    RecyclerView recyclerView;
    FloatingActionButton add_button;
    ImageView empty_imageview;
    TextView no_data;

    MyDatabaseHelper myDB;
    ArrayList<String> city_id, city_name, city_pin, city_icon;
    CustomAdapter customAdapter;

    public ManageCitiesFragments() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_manage_cities, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        add_button = view.findViewById(R.id.add_button);
        empty_imageview = view.findViewById(R.id.empty_imageview);
        no_data = view.findViewById(R.id.no_data);

        btn_plus = view.findViewById( R.id.add_button );
        btn_plus.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddCitiesFragment addDataFragments = new AddCitiesFragment();
                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container,addDataFragments).addToBackStack("First").commit();
            }
        } );

        myDB = new MyDatabaseHelper(getActivity());
        city_id = new ArrayList<>();
        city_name = new ArrayList<>();
        city_pin = new ArrayList<>();
        city_icon = new ArrayList<>();

        storeDataInArrays();

        customAdapter = new CustomAdapter(getActivity(),getActivity(), city_id, city_name, city_pin,
                city_icon);
        recyclerView.setAdapter(customAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        return view;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult( requestCode, resultCode, data );
        if(requestCode == 1){
            getFragmentManager()
                    .beginTransaction()
                    .detach(ManageCitiesFragments.this)
                    .attach(ManageCitiesFragments.this)
                    .addToBackStack(null)
                    .commit();

        }
    }

    void storeDataInArrays(){
        Cursor cursor = myDB.readAllData();
        if(cursor.getCount() == 0){
            empty_imageview.setVisibility(View.VISIBLE);
            no_data.setVisibility(View.VISIBLE);
        }else{
            while (cursor.moveToNext()){
                city_id.add(cursor.getString(0));
                city_name.add(cursor.getString(1));
                city_pin.add(cursor.getString(2));
                city_icon.add(cursor.getString(3));
            }
            empty_imageview.setVisibility(View.GONE);
            no_data.setVisibility(View.GONE);
        }
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.my_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.delete_all){
            confirmDialog();
        }
        return super.onOptionsItemSelected(item);
    }
    void confirmDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Delete All?");
        builder.setMessage("Are you sure you want to delete all Data?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                MyDatabaseHelper myDB = new MyDatabaseHelper(getActivity());
                myDB.deleteAllData();
                //Refresh Activity
                ManageCitiesFragments addDataFragments = new ManageCitiesFragments();
                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container,addDataFragments).addToBackStack("First").commit();
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