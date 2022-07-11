package com.example.wallpaperapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class SplashScreenActivity extends AppCompatActivity {

    SharedPreferences onboardingscreen ;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.splashscreen);
        ImageView image = findViewById(R.id.imageView);
        image.setAnimation(AnimationUtils.loadAnimation(SplashScreenActivity.this, R.anim.bottom_animation));
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                onboardingscreen = getSharedPreferences("onboardingscreen",MODE_PRIVATE);
                boolean isFirstTime = onboardingscreen.getBoolean("firstTime",true);

                if(isFirstTime){

                    SharedPreferences.Editor editor = onboardingscreen.edit();
                    editor.putBoolean("firstTime",false);
                    editor.commit();
                    startActivity(new Intent(SplashScreenActivity.this,OnBoardingMain.class));
                    finish();
                }else {
                    startActivity(new Intent(SplashScreenActivity.this,LoginUser.class));
                    finish();
                }
            }
        },3000);
    }
}
