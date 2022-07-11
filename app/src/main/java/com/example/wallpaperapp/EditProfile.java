package com.example.wallpaperapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
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

import java.util.HashMap;
import java.util.Map;

public class EditProfile extends AppCompatActivity {
    FirebaseFirestore fStore ;
    String iname,iemail,nname,nemail;
    TextView text2 , textview_resetal,textview2_resetal;
    FirebaseUser user ;
    FirebaseAuth fAuth ;
    Uri turi , t1uri ;
    EditText edit_resetal ;
    ImageButton image_resetal ;
    AppCompatEditText edit_name , edit_email;
    AppCompatButton edit_btn_exit , edit_btn_save ;
    ImageView edit_image ;
    ExtendedFloatingActionButton edit_image_btn ;
    StorageReference storageReference;
    Dialog pdialog  ;

    @Override
    public void onBackPressed() {

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_profile);
        edit_name = findViewById(R.id.edit_name);
        edit_email = findViewById(R.id.edit_email);
        edit_btn_exit = findViewById(R.id.edit_btn_exit);
        edit_btn_save = findViewById(R.id.edit_btn_save);
        edit_image=findViewById(R.id.edit_image);
        edit_image_btn=findViewById(R.id.edit_image_btn);
        fAuth=FirebaseAuth.getInstance();
        fStore=FirebaseFirestore.getInstance();
        //userId=fAuth.getCurrentUser().getUid();
        storageReference = FirebaseStorage.getInstance().getReference();
        pdialog=new Dialog(EditProfile.this);
        pdialog.setCanceledOnTouchOutside(false);
        user = fAuth.getCurrentUser();
        pdialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        pdialog.setContentView(R.layout.progress_bar);
        pdialog.show();
        if(user!=null){
            StorageReference profileRef = storageReference.child("users/"+fAuth.getCurrentUser().getUid()+"/profile.jpg");
            profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    pdialog.dismiss();
                    turi=uri ;
                    t1uri = turi ;
                    Glide.with(getApplicationContext()).load(uri).apply(RequestOptions.circleCropTransform()).into(edit_image);

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
                                edit_name.setText(value.getString("fullName"));
                                iname = value.getString("fullName");
                                edit_email.setText(value.getString("email"));
                                iemail = value.getString("email");

                            }
                        }
                        else {
                            Log.d("tag", "onEvent: Document do not exists");
                        }
                    }
                });
            }

        }

        edit_btn_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(EditProfile.this);
                View view1 = LayoutInflater.from(EditProfile.this).inflate(R.layout.alert_dialog, null);
                text2 = view1.findViewById(R.id.text2);
                text2.setText("Discard Changes?");
                builder.setCancelable(false);
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        finish();

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

        edit_image_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pdialog.show();
                CropImage.activity().setGuidelines(CropImageView.Guidelines.ON).start(EditProfile.this);
            }
        });

        edit_btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                nemail = edit_email.getText().toString();
                nname = edit_name.getText().toString();
                if (TextUtils.isEmpty(nemail)) {
                    edit_email.setError("Email is Required.");
                    edit_email.requestFocus();
                    return;
                }

                if (TextUtils.isEmpty(nname)) {
                    edit_name.setError("Name is Required.");
                    edit_name.requestFocus();
                    return;
                }

                if(turi!=t1uri) uploadImageToFirebase(turi);

                DocumentReference docRef = fStore.collection("users").document(user.getUid());
                Map<String,Object> edited = new HashMap<>();
                edited.put("fullName",nname);
                docRef.update(edited).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                    }
                });

                if(!iemail.equals(nemail)){
                    AlertDialog.Builder builder = new AlertDialog.Builder(EditProfile.this);
                    View view1 = LayoutInflater.from(EditProfile.this).inflate(R.layout.alertdialog_reset, null);
                    textview_resetal = view1.findViewById(R.id.textview_resetal);
                    textview2_resetal = view1.findViewById(R.id.textview2_resetal);
                    textview_resetal.setText("Reset Email");
                    textview2_resetal.setText("Enter you current password");
                    edit_resetal = view1.findViewById(R.id.edit_resetal);
                    image_resetal=view1.findViewById(R.id.image_resetal);
                    image_resetal.setImageResource(R.drawable.reset_email);
                    edit_resetal.setHint("Type your Password");
                    edit_resetal.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    builder.setCancelable(false);
                    builder.setView(view1);
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {



                        }
                    });

                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });

                    final AlertDialog dialog = builder.create();
                    dialog.show();
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String password = edit_resetal.getText().toString();
                            if(TextUtils.isEmpty(password)){
                                edit_resetal.setError("This field cannot be empty");
                                edit_resetal.requestFocus();
                            }else {
                                dialog.dismiss();
                                pdialog.show();
                                AuthCredential credential = EmailAuthProvider.getCredential(iemail, password); // Current Login Credential
                                user.reauthenticate(credential).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        FirebaseUser nuser = FirebaseAuth.getInstance().getCurrentUser();
                                        nuser.updateEmail(nemail).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                DocumentReference docRef = fStore.collection("users").document(user.getUid());
                                                Map<String,Object> edited = new HashMap<>();
                                                edited.put("email",nemail);
                                                edited.put("fullName",nname);
                                                docRef.update(edited).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {

                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        pdialog.dismiss();
                                                        Toast.makeText(EditProfile.this,   e.getMessage(), Toast.LENGTH_SHORT).show();
                                                    }
                                                });

                                                nuser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Log.d("verify","check");
                                                        pdialog.dismiss();
                                                        Toast.makeText(EditProfile.this, "Verify Email and login again.", Toast.LENGTH_LONG).show();
                                                        FirebaseAuth.getInstance().signOut();//logout
                                                        startActivity(new Intent(EditProfile.this,LoginUser.class));
                                                        finish();
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        pdialog.dismiss();
                                                        Toast.makeText(EditProfile.this,   e.getMessage(), Toast.LENGTH_SHORT).show();
                                                        Log.d("eemail", "onFailure: Email not sent " + e.getMessage());
                                                    }
                                                });
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                pdialog.dismiss();
                                                Toast.makeText(EditProfile.this,   e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        pdialog.dismiss();
                                        Toast.makeText(EditProfile.this,   e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    });
                    dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                        }
                    });
                }else {
                    pdialog.dismiss();
                    Toast.makeText(EditProfile.this,   "Profile Updated", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(EditProfile.this,MainActivity.class));
                }
            }
        });



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if(resultCode==RESULT_OK){
                Uri uri = result.getUri();
                turi = uri ;
                pdialog.dismiss();
                Glide.with(getApplicationContext()).load(uri).apply(RequestOptions.circleCropTransform()).into(edit_image);
            }
        }
    }

    private void uploadImageToFirebase(Uri uri) {

        final StorageReference fileRef = storageReference.child("users/"+fAuth.getCurrentUser().getUid()+"/profile.jpg");
        fileRef.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Glide.with(getApplicationContext()).load(uri).apply(RequestOptions.circleCropTransform()).into(edit_image);
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Failed.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
