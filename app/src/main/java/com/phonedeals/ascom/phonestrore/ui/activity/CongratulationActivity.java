
package com.phonedeals.ascom.phonestrore.ui.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.phonedeals.ascom.phonestrore.R;
import com.phonedeals.ascom.phonestrore.util.AppUtils;

public class CongratulationActivity extends AppCompatActivity {

    private TextView congratulation,success;
    private Button store;
    private Toolbar toolbar;
    private String tag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_congratulation);

        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(R.drawable.ic_back);

        congratulation=findViewById(R.id.congratulation);
        success=findViewById(R.id.success);
        tag=getIntent().getStringExtra("tag");

        store=findViewById(R.id.go_to_store);

        if (tag!=null){
            success.setText(R.string.your_offer_send);
        }
        AppUtils.applyBoldFont(store);
        AppUtils.applyMediumFont(congratulation,success);
    }

    public void goToStore(View view) {
        startActivity(new Intent(CongratulationActivity.this,HomeActivity.class));
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
