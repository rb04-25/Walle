package com.example.wallpaperapp;

import static android.app.Activity.RESULT_OK;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

public class ProfileFragment extends Fragment {

    FirebaseUser user ;
    ExtendedFloatingActionButton edit_profile , logout;
    ImageView profile_image;
    ImageButton image ,image1;
    StorageReference storageReference;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore ;
    Activity mactivity;
    String userId ;
    TextView profile_name , profile_email;
    Dialog pdialog  ;
    TextView text2 ;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Activity) {
            mactivity = (Activity) context;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        profile_image = view.findViewById(R.id.profile_image);
        edit_profile=view.findViewById(R.id.edit_profile);
        logout=view.findViewById(R.id.logout);
        profile_name=view.findViewById(R.id.profile_name);
        fStore = FirebaseFirestore.getInstance();
        profile_email=view.findViewById(R.id.profile_email);
        fAuth = FirebaseAuth.getInstance();
        user = fAuth.getCurrentUser();
        //userId = fAuth.getCurrentUser().getUid();
        storageReference = FirebaseStorage.getInstance().getReference();
        pdialog=new Dialog(mactivity);
        pdialog.setCanceledOnTouchOutside(false);
        pdialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        pdialog.setContentView(R.layout.progress_bar);
        pdialog.show();

        if(user!=null){
            StorageReference profileRef = storageReference.child("users/"+fAuth.getCurrentUser().getUid()+"/profile.jpg");
            profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    pdialog.dismiss();
                    Glide.with(mactivity.getApplicationContext()).load(uri).apply(RequestOptions.circleCropTransform()).into(profile_image);

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    pdialog.dismiss();
                }
            });
            DocumentReference documentReference = fStore.collection("users").document(fAuth.getCurrentUser().getUid());
            if(fStore!=null){
                documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        if(error==null){
                            if(value.exists()){
                                profile_name.setText(value.getString("fullName"));
                                profile_email.setText(value.getString("email"));

                            }
                        }
                        else {
                            Log.d("tag", "onEvent: Document do not exists");
                        }
                    }
                });
            }
        }



        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(mactivity);
                View view1 = LayoutInflater.from(mactivity).inflate(R.layout.alert_dialog, null);
                text2 = view1.findViewById(R.id.text2);
                image=view1.findViewById(R.id.image);
                image1=view1.findViewById(R.id.image1);
                image.setVisibility(View.INVISIBLE);
                image1.setVisibility(View.VISIBLE);
                text2.setText("Are you sure you want to logout?");
                builder.setCancelable(false);
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        FirebaseAuth.getInstance().signOut();//logout
                        startActivity(new Intent(mactivity.getApplicationContext(),LoginUser.class));
                        requireActivity().finish();

                    }
                });

                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

                builder.setView(view1);
                builder.show();
            }
        });

        edit_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(mactivity,EditProfile.class));
            }
        });
        return view ;
    }
}