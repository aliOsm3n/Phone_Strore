package com.phonedeals.ascom.phonestrore.app;


import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.preference.PreferenceManager;
import android.support.multidex.MultiDex;

import com.androidnetworking.AndroidNetworking;
import com.phonedeals.ascom.phonestrore.R;

import java.util.Locale;


public class phoneStore extends Application{

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
    @Override
    public void onCreate() {
        super.onCreate();
        AndroidNetworking.initialize(getApplicationContext());
    }
}