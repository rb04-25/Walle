package com.example.wallpaperapp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class HomeAdapter extends RecyclerView.Adapter<WallViewHolder> {


    Context context ;
    ArrayList<ImageModel> wallpaper ;
    public static final String msg = "image";

    public HomeAdapter(Context context, ArrayList<ImageModel> wallpaper) {
        this.context = context;
        this.wallpaper = wallpaper;
    }


    @NonNull
    @Override
    public WallViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_layout,parent,false);
        return new WallViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WallViewHolder holder, int position) {
        Glide.with(context).load(wallpaper.get(position).getSrc().getPortrait()).into(holder.imageView);
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context,setwallpaper.class);
                intent.putExtra(msg,wallpaper.get(holder.getAdapterPosition()).getSrc().getPortrait());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);

            }
        });
    }

    @Override
    public int getItemCount() {
        return wallpaper.size();
    }
}


