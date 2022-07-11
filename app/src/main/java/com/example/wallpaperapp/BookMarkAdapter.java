package com.example.wallpaperapp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class BookMarkAdapter extends RecyclerView.Adapter<BookMarkViewHolder> {


    Context context ;
    ArrayList<String> bookmark ;
    public static final String msg = "image";

    public BookMarkAdapter(Context context, ArrayList<String> bookmark) {
        this.context = context;
        this.bookmark = bookmark;
    }

    @NonNull
    @Override
    public BookMarkViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_layout,parent,false);
        return new BookMarkViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookMarkViewHolder holder, int position) {


        Glide.with(context).load(bookmark.get(position)).into(holder.imageView);
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context,setwallpaper.class);
                intent.putExtra(msg,bookmark.get(holder.getAdapterPosition()));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);

            }
        });


    }

    @Override
    public int getItemCount() {
        return bookmark.size();
    }
}
