package com.phonedeals.ascom.phonestrore.ui.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


import android.content.Intent;
import android.os.Handler;

import com.phonedeals.ascom.phonestrore.util.LocaleHelper;
import com.phonedeals.ascom.phonestrore.R;
import com.phonedeals.ascom.phonestrore.data.prefs.CacheUtils;


public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        LocaleHelper.setLocale(this, CacheUtils.getUserLanguage(this,"language"));
        Handler handler=new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                checkCurrentUser();
            }
        },4000);
    }

    public void checkCurrentUser(){
            String user = CacheUtils.getUserName(this,"user");
            if (user==null) {
                startActivity(new Intent(SplashScreenActivity.this, SignInActivity.class));
                finish();
            } else {
                startActivity(new Intent(SplashScreenActivity.this, HomeActivity.class));
                finish();
            }
    }

}

