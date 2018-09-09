package com.phonedeals.ascom.phonestrore.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.phonedeals.ascom.phonestrore.R;
import com.phonedeals.ascom.phonestrore.data.prefs.CacheUtils;
import com.phonedeals.ascom.phonestrore.util.ValidateUtils;
import com.phonedeals.ascom.phonestrore.util.AppUtils;


import org.json.JSONObject;

import static com.phonedeals.ascom.phonestrore.util.AppUtils.getTextContent;
import static com.phonedeals.ascom.phonestrore.util.AppUtils.isInternetAvailable;
import static com.phonedeals.ascom.phonestrore.util.AppUtils.showInfoDialog;
import static com.phonedeals.ascom.phonestrore.util.AppUtils.showNoInternetDialog;

public class CheckCodeActivity extends AppCompatActivity {

    private EditText checkCode;
    private Button checkButton;
    private String phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_code);
        initView();
    }

    private void initView() {
        phone=getIntent().getStringExtra("phone");
        checkCode=findViewById(R.id.check_code);
        checkButton=findViewById(R.id.nextBtn);
        AppUtils.applyBoldFont(checkButton);
    }

    public void next(View view) {
        if (phone!=null){
            verifyPasswordCode();
        }else {
            AppUtils.showInfoToast(CheckCodeActivity.this,getString(R.string.please_return_and_enter_phone_again));
        }
    }

    private void verifyPasswordCode() {

        final String passCode = getTextContent(checkCode);
        if (ValidateUtils.missingInputs(passCode)) {
            showInfoDialog(this,R.string.error_dialog_missing_inputs);
            return;
        }
        if (!isInternetAvailable(this)) {
            showNoInternetDialog(this);
            return;
        }

        AppUtils.showProgressDialog(this);

        AndroidNetworking.post("http://athelapps.com/phone/api/user/reset_verify")
                .addBodyParameter("phone", "966"+phone)
                .addBodyParameter("sms_code",passCode)
                .addBodyParameter("api_key", "BUNgDZlDqosZXWJtjYUgdx5aWbqKC2gr7wRxzVFh8Pg")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("jjjjjjj",response);
                        AppUtils.dismissProgressDialog();
                        try {
                            if (new JSONObject(response).getString("code").toString().equals("200")) {
                                Intent intent=new Intent(CheckCodeActivity.this,ResetPasswordActivity.class);
                                intent.putExtra("token",new JSONObject(response).getJSONObject("data").getString("token"));
                                startActivity(intent);

                                finish();
                            }else {
                                AppUtils.showErrorToast(CheckCodeActivity.this,getString(R.string.wrong_code));
                            }
                        } catch (Exception e) {
                            AppUtils.showErrorToast(CheckCodeActivity.this,getString(R.string.wrong_code));
                        }

                    }
                    @Override
                    public void onError(ANError anError) {
                        AppUtils.dismissProgressDialog();
                        AppUtils.showErrorToast(CheckCodeActivity.this,getString(R.string.dialog_error_no_internet));
                    }
                });

    }

}
