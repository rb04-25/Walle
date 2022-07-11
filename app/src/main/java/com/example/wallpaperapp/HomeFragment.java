package com.example.wallpaperapp;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.util.ArrayList;


public class HomeFragment extends Fragment {

    private String mParam1;
    private String mParam2;
    private ArrayList<ImageModel> modelClasslist ;
    private RecyclerView recyclerview;
    HomeAdapter hadapter ;
    CardView mnature , mcar , mtrain , mtrending ;
    EditText etext ;
    ImageButton search ;
    Context context ;
    Activity mactivity ;
    ApiUtilities manager ;
    ExtendedFloatingActionButton next ,prev ;
    String word = "";
    boolean check = true ;
    int pagee ;
    Dialog pdialog , dialog ;


    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        if(context instanceof Activity){
            mactivity = (Activity) context ;
        }
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

         View view = inflater.inflate(R.layout.fragment_home, container, false);
        //((AppCompatActivity)mactivity).getSupportActionBar().hide();
        dialog=new Dialog(mactivity);
        manager = new ApiUtilities(mactivity);
         recyclerview = view.findViewById(R.id.recyclerview);
         mnature = view.findViewById(R.id.nature);
        mtrain = view.findViewById(R.id.train);
        mcar = view.findViewById(R.id.car);
        mtrending = view.findViewById(R.id.trending);
        etext=view.findViewById(R.id.edittext);
        search=view.findViewById(R.id.search);
        next = view.findViewById(R.id.btnnext);
        prev = view.findViewById(R.id.btnprev);
        pdialog=new Dialog(mactivity);
        pdialog.setCanceledOnTouchOutside(false);
        pdialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        pdialog.setContentView(R.layout.progress_bar);
        pdialog.show();
        manager.getapi(listener,1,80);




        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pdialog.show();
                int new_page = pagee + 1 ;
                if(check) manager.getapi(listener,new_page,80);
                else {
                    manager.getapisearch(listener,word,new_page,80);
                }
            }
        });

        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(pagee>1){
                    pdialog.show();
                    int new_page = pagee - 1 ;
                    if(check) manager.getapi(listener,new_page,80);
                    else {
                        manager.getapisearch(listener,word,new_page,80);
                    }
                }
            }
        });

        mnature.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String query = "nature";
                word = query ;
                check = false ;
                pdialog.show();
                manager.getapisearch(listener,query,1,80);
            }
        });
        mtrain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String query = "train";
                word = query ;
                check = false ;
                pdialog.show();
                manager.getapisearch(listener,query,1,80);
            }
        });
        mcar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String query = "car";
                word = query ;
                check = false ;
                pdialog.show();

                manager.getapisearch(listener,query,1,80);
            }
        });
        mtrending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                check = true ;
                pdialog.show();
                manager.getapi(listener,1,80);
            }
        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String query = etext.getText().toString().trim().toLowerCase();
                if(query.isEmpty()){
                    Toast.makeText(mactivity.getApplicationContext(),"This field cannot be empty",Toast.LENGTH_SHORT).show();
                }else {
                    word = query ;
                    check = false ;
                    pdialog.show();
                    manager.getapisearch(listener,query,1,80);
                }
            }
        });
        return view ;
    }





    private final Fetchlistener listener = new Fetchlistener() {
        @Override
        public void onFetch(SearchModel response, String message) {
            pdialog.dismiss();
            if(response.getPhotos().isEmpty()){
                Toast.makeText(mactivity,"No Image Found",Toast.LENGTH_SHORT).show();
                return ;
            }
            pagee = response.getPage();
            showData(response.getPhotos());
        }

        @Override
        public void onError(String message) {
            pdialog.dismiss();
            if(message=="Internet")showDialog();
            else Toast.makeText(mactivity,message,Toast.LENGTH_SHORT).show();

        }
    };

    private void showData(ArrayList<ImageModel> photos) {


        recyclerview.setHasFixedSize(true);
        recyclerview.setLayoutManager(new GridLayoutManager(mactivity,2));
        hadapter = new HomeAdapter(mactivity,photos);
        recyclerview.setAdapter(hadapter);
    }

    private void showDialog() {
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setContentView(R.layout.error_dialog);
        dialog.show();
        AppCompatButton button = dialog.findViewById(R.id.ebtn);
        ImageButton button1 = dialog.findViewById(R.id.butn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               startActivity(new Intent(mactivity,MainActivity.class));
            }
        });
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });


    }
}