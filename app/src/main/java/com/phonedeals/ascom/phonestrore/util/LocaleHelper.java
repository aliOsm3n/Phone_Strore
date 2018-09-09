package com.phonedeals.ascom.phonestrore.util;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.util.DisplayMetrics;

import com.phonedeals.ascom.phonestrore.data.prefs.CacheUtils;
import com.phonedeals.ascom.phonestrore.ui.activity.SignInActivity;

import java.util.Locale;

public class LocaleHelper {


    public static void setLocale(Context context, String lang) {
        Locale locale = new Locale(lang);

        Locale.setDefault(locale);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {

            Resources resources = context.getResources();

            Configuration configuration = resources.getConfiguration();

            DisplayMetrics displayMetrics = resources.getDisplayMetrics();

            configuration.setLocale(locale);

            resources.updateConfiguration(configuration, displayMetrics);

        } else {

            Configuration config = new Configuration();

            config.locale = locale;

            context.getResources().updateConfiguration(config,

                    context.getResources().getDisplayMetrics());

        }


    }


//    public static void saveLocale(Context context, String locale) {
//        PrefsUtils.save(context, "language", locale);
//    }
//
//
//    public static String getLocale(Context context) {
//        String language = PrefsUtils.get(context, "language");
//        if (language.isEmpty()){
//            return "en";
//        }
//        return language;
//    }


}
