package com.example.wallpaperapp;

import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class WallViewHolder extends RecyclerView.ViewHolder {

    ImageView imageView ;
    public WallViewHolder(@NonNull View itemView) {
        super(itemView);
        imageView = itemView.findViewById(R.id.image);
    }
}
