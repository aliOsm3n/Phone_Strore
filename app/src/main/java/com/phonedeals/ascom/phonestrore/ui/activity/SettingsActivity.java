package com.phonedeals.ascom.phonestrore.ui.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.phonedeals.ascom.phonestrore.util.LocaleHelper;
import com.phonedeals.ascom.phonestrore.R;
import com.phonedeals.ascom.phonestrore.data.prefs.CacheUtils;
import com.phonedeals.ascom.phonestrore.util.AppUtils;

public class SettingsActivity extends AppCompatActivity {

    private TextView title,language;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        title = findViewById(R.id.toolbar_title);
        language = findViewById(R.id.lang);
        AppUtils.applyMediumFont(title, language);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(R.drawable.ic_back);
        Switch simpleSwitch = findViewById(R.id.simpleSwitch);
        if (CacheUtils.getUserLanguage(SettingsActivity.this, "language").equalsIgnoreCase("en")) {
            simpleSwitch.setChecked(true);
            simpleSwitch.setText(R.string.english);
        } else {
            simpleSwitch.setChecked(false);
            simpleSwitch.setText(R.string.arabic);
        }

        simpleSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (simpleSwitch.isChecked()) {
                    LocaleHelper.setLocale(SettingsActivity.this, "en");
                    CacheUtils.getSharedPreferences(SettingsActivity.this).edit().putString("language", "en").apply();
                    simpleSwitch.setText("English");
                    recreate();
                } else {
                    LocaleHelper.setLocale(SettingsActivity.this, "ar");
                    CacheUtils.getSharedPreferences(SettingsActivity.this).edit().putString("language", "ar").apply();
                    simpleSwitch.setText("Arabic");
                    recreate();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this,HomeActivity.class));
        finish();
    }

}
