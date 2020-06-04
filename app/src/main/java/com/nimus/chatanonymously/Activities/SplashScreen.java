package com.nimus.chatanonymously.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import com.nimus.chatanonymously.R;

public class SplashScreen extends AppCompatActivity {

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAndRemoveTask();
        finishAffinity();
        System.exit(0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash_screen);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashScreen.this,ActivityLogin.class);
                startActivity(intent);
                finish();
            }
        },3000);
    }
}
