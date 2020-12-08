package com.example.weatherapplication;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;


public class AddCitiesFragment extends Fragment {
    EditText title_input;
    Button add_button;

    public AddCitiesFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view =  inflater.inflate( R.layout.fragment_add_cities, container, false );
        title_input = view.findViewById(R.id.title_input);
        add_button = view.findViewById(R.id.add_buttons);
        add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyDatabaseHelper myDB = new MyDatabaseHelper( getActivity() );
                myDB.addBook(title_input.getText().toString().trim());
                ManageCitiesFragments manageCitiesFragments = new ManageCitiesFragments();
                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container,manageCitiesFragments).addToBackStack("First").commit();
            }
        });
        return view;
    }
}