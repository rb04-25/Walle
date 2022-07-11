package com.example.wallpaperapp;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class BookMarkViewHolder extends RecyclerView.ViewHolder {
    ImageView imageView ;
    TextView booktext ;


    public BookMarkViewHolder(@NonNull View itemView) {
        super(itemView);
        imageView = itemView.findViewById(R.id.image);
        booktext = itemView.findViewById(R.id.booktext);
    }
}
