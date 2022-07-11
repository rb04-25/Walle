package com.example.wallpaperapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.Dialog;
import android.app.DownloadManager;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class setwallpaper extends AppCompatActivity {

    Intent intent;
    ImageView image ;
    FirebaseFirestore fStore ;
    FirebaseAuth fAuth ;
    ExtendedFloatingActionButton set , download , bookmark;
    ImageModel image1 ;
    String url ;
    ArrayList<String> arr1 ;
    Dialog pdialog ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setwallpaper);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        final WallpaperManager wpmanager = WallpaperManager.getInstance(getApplicationContext());
        set = findViewById(R.id.btnset);
        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        download = findViewById(R.id.btndownload);
        bookmark = findViewById(R.id.btnbookmark);
        image = findViewById(R.id.finalimage);
        intent = getIntent();
        arr1=new ArrayList<>();
        url = intent.getStringExtra(HomeAdapter.msg);
        pdialog=new Dialog(setwallpaper.this);
        pdialog.setCanceledOnTouchOutside(false);
        pdialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        pdialog.setContentView(R.layout.progress_bar);
        pdialog.show();
        Glide.with(getApplicationContext()).load(url).into(image);
        if(fAuth.getCurrentUser()!=null){
            DocumentReference documentReference = fStore.collection("users").document(fAuth.getCurrentUser().getUid());
            if(fStore!=null){
                documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        if(error==null){
                            if(value.exists()){
                                pdialog.dismiss();
                                ArrayList<String> arr = ( ArrayList<String>) value.get("bookmark_list");
                                arr1 = ( ArrayList<String>) value.get("bookmark_list");
                                if(arr.contains(url)){
                                    bookmark.setIconResource(R.drawable.ic_wishlist_done);
                                }else {
                                    bookmark.setIconResource(R.drawable.ic_wishlist);
                                }
                            }
                        }
                        else {
                            Log.d("tag", "onEvent: Document do not exists");
                        }
                    }
                });
            }
        }

        bookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(fAuth.getCurrentUser()!=null){
                    DocumentReference documentReference1 = fStore.collection("users").document(fAuth.getCurrentUser().getUid());
                    if(fStore!=null){
                        Map<String,Object> edited = new HashMap<>();
                        if(arr1.contains(url)){
                            arr1.remove(url);
                            edited.put("bookmark_list",arr1);
                            documentReference1.update(edited).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    bookmark.setIconResource(R.drawable.ic_wishlist);
                                    Toast.makeText(setwallpaper.this,"Bookmark Removed",Toast.LENGTH_SHORT).show();
                                }
                            });
                        }else {

                            arr1.add(url);
                            edited.put("bookmark_list",arr1);
                            documentReference1.update(edited).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    bookmark.setIconResource(R.drawable.ic_wishlist_done);
                                    Toast.makeText(setwallpaper.this,"Bookmark Added",Toast.LENGTH_SHORT).show();
                                }
                            });


                        }


                    }

                }
            }
        });


        set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    Bitmap bitmap = ((BitmapDrawable)image.getDrawable()).getBitmap();
                    wpmanager.setBitmap(bitmap);
                    Toast.makeText(setwallpaper.this,"Your new Wallpaper is now set.",Toast.LENGTH_LONG).show();
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        });
        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DownloadManager dmanager = null ;
                dmanager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                Uri uri = Uri.parse(url);
                DownloadManager.Request request= new DownloadManager.Request(uri);
                request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE)
                        .setAllowedOverRoaming(false)
                        .setTitle("Wallpaper_")
                        .setMimeType("image/jpeg")
                        .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                        .setDestinationInExternalPublicDir(Environment.DIRECTORY_PICTURES,"Wallpaper_"+ ".jpg");
                dmanager.enqueue(request);

                Toast.makeText(setwallpaper.this,"Download Completed",Toast.LENGTH_SHORT).show();
            }
        });
    }

}



//// else

