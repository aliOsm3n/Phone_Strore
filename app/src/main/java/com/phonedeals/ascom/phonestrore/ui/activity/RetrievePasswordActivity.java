package com.phonedeals.ascom.phonestrore.ui.activity;

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
import com.phonedeals.ascom.phonestrore.util.ValidateUtils;
import com.phonedeals.ascom.phonestrore.util.AppUtils;

import org.json.JSONException;
import org.json.JSONObject;

import static com.phonedeals.ascom.phonestrore.util.AppUtils.getTextContent;
import static com.phonedeals.ascom.phonestrore.util.AppUtils.isInternetAvailable;
import static com.phonedeals.ascom.phonestrore.util.AppUtils.showInfoDialog;
import static com.phonedeals.ascom.phonestrore.util.AppUtils.showNoInternetDialog;

public class RetrievePasswordActivity extends AppCompatActivity {

    private EditText phoneNumber;
    private Button retButton;
    private EditText countryCode;
    private TextView message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retrieve_password);
        initView();
    }

    private void initView() {
        phoneNumber= findViewById(R.id.phone_number);
        countryCode= findViewById(R.id.country_code);
        retButton= findViewById(R.id.btn_ret_password);
        message= findViewById(R.id.message);
        AppUtils.applyMediumFont(message);
        AppUtils.applyBoldFont(retButton);
    }

    public void CheckCode(View view) {
        forgetPassword();
    }

    private void forgetPassword() {
        String phone_Number = getTextContent(phoneNumber);
        if (!ValidateUtils.validPhone(phone_Number)) {
            showInfoDialog(this,R.string.error_dialog_invalid_phone);
            return;
        }
        if (!isInternetAvailable(this)) {
            showNoInternetDialog(this);
            return;
        }

        AppUtils.showProgressDialog(this);

        AndroidNetworking.post("http://athelapps.com/phone/api/user/pass_reset")
                .addBodyParameter("api_key", "BUNgDZlDqosZXWJtjYUgdx5aWbqKC2gr7wRxzVFh8Pg")
                .addBodyParameter("phone", "966"+phone_Number)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("jjjjjjj",response);
                        AppUtils.dismissProgressDialog();
                        try {
                            if (new JSONObject(response).getString("code").toString().equalsIgnoreCase("200")) {
                                AppUtils.showSuccessToast(RetrievePasswordActivity.this,getString(R.string.code_send_success));
                                Intent intent=new Intent(RetrievePasswordActivity.this, CheckCodeActivity.class);
                                intent.putExtra("phone",phone_Number);
                                startActivity(intent);
                                finish();
                            } else if ( new JSONObject(response).getString("code").toString().equalsIgnoreCase("401")) {
                                AppUtils.showInfoToast(RetrievePasswordActivity.this,getString(R.string.code_not_send));
                            }
                        } catch (JSONException e) {
                            AppUtils.showErrorToast(RetrievePasswordActivity.this,getString(R.string.code_not_send));
                        }
                    }
                    @Override
                    public void onError(ANError anError) {
                        AppUtils.dismissProgressDialog();
                        AppUtils.showErrorToast(RetrievePasswordActivity.this,getString(R.string.dialog_error_no_internet));
                    }
                });
    }
}
