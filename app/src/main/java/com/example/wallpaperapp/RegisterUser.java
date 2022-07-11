package com.example.wallpaperapp;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RegisterUser extends AppCompatActivity {
    public static final String TAG = "TAG";
    TextView signup_login;
    AppCompatButton signup_button;
    EditText signup_name , signup_email,signup_password;
    FirebaseAuth fAuth;
    Dialog pdialog;
    FirebaseFirestore fStore;
    String userID;
    @Override
    public void onBackPressed() {

    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        signup_login=findViewById(R.id.signup_login);
        signup_button=findViewById(R.id.signup_button);
        signup_name=findViewById(R.id.signup_name);
        signup_password=findViewById(R.id.signup_password);
        signup_email=findViewById(R.id.signup_email);
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        pdialog=new Dialog(RegisterUser.this);
        pdialog.setCanceledOnTouchOutside(false);
        pdialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        pdialog.setContentView(R.layout.progress_bar);
        //FirebaseAuth.getInstance().signOut();//logout
        signup_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = signup_email.getText().toString();
                String password = signup_password.getText().toString();
                final String fullName = signup_name.getText().toString();

                if(TextUtils.isEmpty(email)){
                    signup_email.setError("Email is Required.");
                    signup_email.requestFocus();
                    return;
                }

                if(TextUtils.isEmpty(password)){
                    signup_password.setError("Password is Required.");
                    signup_password.requestFocus();
                    return;
                }

                if(password.length() < 6){
                    signup_password.setError("Password Must be >= 6 Characters");
                    signup_password.requestFocus();
                    return;
                }
                if(TextUtils.isEmpty(fullName)){
                    signup_name.setError("Name field cannot be empty");
                    signup_name.requestFocus();
                    return;
                }
                pdialog.show();
                fAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            FirebaseUser fuser = fAuth.getCurrentUser();
                            String id = fuser.getUid();
                            Log.d("uid",id+" "+email);
                            fuser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d("verify","check");
                                    Toast.makeText(RegisterUser.this, "Check Your Email for Verification Link.", Toast.LENGTH_LONG).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG, "onFailure: Email not sent " + e.getMessage());
                                }
                            });
                            userID = fAuth.getCurrentUser().getUid();
                            Log.d("user",userID);
                            DocumentReference documentReference = fStore.collection("users").document(userID);
                            Map<String,Object> user = new HashMap<>();
                            ArrayList<String> bookwall = new ArrayList<>();
                            user.put("fullName",fullName);
                            user.put("email",email);
                            user.put("bookmark_list",bookwall);
                            documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG, "onSuccess: user Profile is created for "+ userID);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG, "onFailure: " + e.toString());
                                }
                            });
                            FirebaseAuth.getInstance().signOut();//logout
                            startActivity(new Intent(getApplicationContext(),LoginUser.class));
                            finish();

                        }else {
                            Toast.makeText(RegisterUser.this, "Sorry!! " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            pdialog.dismiss();
                        }
                    }
                });
            }
        });


        signup_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterUser.this,LoginUser.class));
                finish();
            }
        });



    }
}
