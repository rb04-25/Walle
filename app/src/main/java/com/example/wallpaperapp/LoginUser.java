package com.example.wallpaperapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginUser extends AppCompatActivity {
    public static final String TAG = "TAG";
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    TextView login_signup,login_forgot;
    EditText login_email , login_password ;
    AppCompatButton login_button ;
    FirebaseUser user;
    Dialog pdialog  ;

    @Override
    public void onBackPressed() {

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        login_signup=findViewById(R.id.login_signup);
        login_forgot=findViewById(R.id.login_forgot);
        login_email=findViewById(R.id.login_email);
        login_password=findViewById(R.id.login_password);
        pdialog=new Dialog(LoginUser.this);
        pdialog.setCanceledOnTouchOutside(false);
        pdialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        pdialog.setContentView(R.layout.progress_bar);
        login_button=findViewById(R.id.login_button);
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        user = fAuth.getCurrentUser();
        if(user != null){
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
            finish();
        }

        login_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginUser.this, RegisterUser.class));
                finish();
            }
        });

        login_forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pdialog.dismiss();
                AlertDialog.Builder builder1 = new AlertDialog.Builder(LoginUser.this);
                View view1 = LayoutInflater.from(LoginUser.this).inflate(R.layout.alertdialog_reset, null);
                EditText edit_resetal = view1.findViewById(R.id.edit_resetal);
                builder1.setCancelable(false);
                builder1.setView(view1);

                builder1.setPositiveButton("Send Link", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // extract the email and send reset link



                    }
                });

                builder1.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // close the dialog
                    }
                });
                final AlertDialog dialog = builder1.create();
                dialog.show();
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String mail = edit_resetal.getText().toString();
                        if(TextUtils.isEmpty(mail)){
                            edit_resetal.setError("Email is Required");
                            edit_resetal.requestFocus();
                        }else {
                            dialog.dismiss();
                            fAuth.sendPasswordResetEmail(mail).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(LoginUser.this, "Reset Link Sent To Your Email.", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(LoginUser.this, "Sorry!! Reset Link is Not Sent . Enter Valid Email.", Toast.LENGTH_SHORT).show();
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

            }
        });
        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = login_email.getText().toString().trim();
                String password = login_password.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    login_email.setError("Email is Required.");
                    login_email.requestFocus();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    login_password.setError("Password is Required.");
                    login_password.requestFocus();
                    return;
                }





                pdialog.show();

                fAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            FirebaseUser user1 = fAuth.getCurrentUser();
                            if (user1.isEmailVerified()) {
                                Toast.makeText(LoginUser.this, "Logged in Successfully", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                            }else  {
                                verifyEmail();
                                return ;
                            }

                        }else {
                            Toast.makeText(LoginUser.this, "Sorry!! " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            pdialog.dismiss();
                        }

                    }
                });
            }
        });
    }

    private void verifyEmail() {

        pdialog.dismiss();
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginUser.this);

        View view = LayoutInflater.from(LoginUser.this).inflate(R.layout.alert_dialog, null);
        builder.setCancelable(false);

        builder.setPositiveButton("Verify Email", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                FirebaseUser fuser = fAuth.getCurrentUser();
                fuser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("verify","check");
                        Toast.makeText(LoginUser.this, "Check Your Email for Verification Link.", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: Email not sent " + e.getMessage());
                    }
                });
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        builder.setView(view);
        builder.show();
    }
}

