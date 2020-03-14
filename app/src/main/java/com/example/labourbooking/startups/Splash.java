package com.example.labourbooking.startups;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import com.example.labourbooking.R;
import com.example.labourbooking.controlers.Constants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Splash extends AppCompatActivity {
    private final static int SPLASH_TIME = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        setSplashTime();
    }

    boolean isLogined() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null && user.isEmailVerified()) {
            return true;
        } else {
            return false;
        }
    }

    void setSplashTime() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent login = new Intent(Splash.this, Login.class);
                Intent dashboard = new Intent(Splash.this, Dashboard.class);
                if (isLogined()) {
                    startActivity(dashboard);
                } else {
                    startActivity(login);
                }
                finish();
            }
        }, SPLASH_TIME);
    }
}
