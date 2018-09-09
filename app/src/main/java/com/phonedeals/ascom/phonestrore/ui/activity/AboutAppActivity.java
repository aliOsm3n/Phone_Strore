package com.phonedeals.ascom.phonestrore.ui.activity;

import android.app.Dialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.format.DateUtils;
import android.util.Log;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.phonedeals.ascom.phonestrore.R;
import com.phonedeals.ascom.phonestrore.ui.dialog.AppDialog;
import com.phonedeals.ascom.phonestrore.util.AppUtils;
import com.phonedeals.ascom.phonestrore.util.DialogUtils;

import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class AboutAppActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TextView title,content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
    }

    private void initViews() {
        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(R.drawable.ic_back);

        title=findViewById(R.id.toolbar_title);
        content=findViewById(R.id.about_text);
        AppUtils.applyMediumFont(title,content);

        if (!AppUtils.isInternetAvailable(this)) {
            showCanNotLoadDataDialog();
        }else {
            loadAboutApp();
        }

    }

    private void loadAboutApp(){
        AppUtils.showProgressDialog(this);
        AndroidNetworking.post("http://athelapps.com/phone/api/content/get")
                .addBodyParameter("api_key", "BUNgDZlDqosZXWJtjYUgdx5aWbqKC2gr7wRxzVFh8Pg")
                .addBodyParameter("slug","about")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        AppUtils.dismissProgressDialog();
                        try {
                            if (new JSONObject(response).getString("code").toString().equals("200")) {
                                content.setText(new JSONObject(response).getString("data"));
                            }else if (new JSONObject(response).getString("code").toString().equals("401")){
                                AppUtils.showInfoToast(getBaseContext(),getString(R.string.do_not_load_about_app));
                            }
                            else if (new JSONObject(response).getString("code").toString().equals("403")){
                                AppUtils.showInfoToast(getBaseContext(),getString(R.string.do_not_load_about_app));
                            }else {
                                AppUtils.showInfoToast(getBaseContext(),getString(R.string.do_not_load_about_app));
                            }
                        } catch (Exception e) {
                            AppUtils.showInfoToast(getBaseContext(),getString(R.string.do_not_load_about_app));
                        }

                    }
                    @Override
                    public void onError(ANError anError) {
                        AppUtils.dismissProgressDialog();
                        showCanNotLoadDataDialog();
                    }
                });
    }

    private void showCanNotLoadDataDialog() {
        DialogUtils.showTwoActionButtonsDialog(AboutAppActivity.this, R.string.dialog_error_no_internet,
                R.string.dialog_ok, new AppDialog.Action1ButtonListener() {
                    @Override
                    public void onAction1ButtonClick(Dialog dialog) {
                        dialog.dismiss();
                        loadAboutApp();
                    }
                }, R.string.dialog_cancel, new AppDialog.Action2ButtonListener() {
                    @Override
                    public void onAction2ButtonClick(Dialog dialog) {
                        finish();
                    }
                }, false);
    }

}
