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
import com.phonedeals.ascom.phonestrore.data.prefs.CacheUtils;
import com.phonedeals.ascom.phonestrore.util.AppUtils;
import com.phonedeals.ascom.phonestrore.util.ValidateUtils;

import org.json.JSONException;
import org.json.JSONObject;


public class ConfirmCreateAccountActivity extends AppCompatActivity {

    private EditText checkCode;
    private Button checkButton;
    private TextView textView;
    private String token, phone;
    private String userType="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_create_account);
        initView();
        token=getIntent().getStringExtra("token");

    }

    private void initView() {
        checkCode=findViewById(R.id.confirm_code_txt);
        checkButton=findViewById(R.id.confirm_code_btn);
        textView=findViewById(R.id.sms_text);
        AppUtils.setHtmlText(R.string.sms_code,textView);
        AppUtils.applyMediumFont(textView);
        AppUtils.applyBoldFont(checkButton);
    }

    public void confirmCreateAccount(View view) {
        confirm();
    }

    private void confirm(){
        final String passCode = AppUtils.getTextContent(checkCode);
        if (ValidateUtils.missingInputs(passCode)) {
            AppUtils.showInfoDialog(this,R.string.error_dialog_missing_inputs);
            return;
        }
        if (!AppUtils.isInternetAvailable(this)) {
            AppUtils.showNoInternetDialog(this);
            return;
        }

        if (token==null){
            AppUtils.showInfoDialog(this,R.string.check_code_again);
            return;
        }

        AppUtils.showProgressDialog(this);

        AndroidNetworking.post("http://athelapps.com/phone/api/user/verify")
                .addBodyParameter("token", token)
                .addBodyParameter("api_key", "BUNgDZlDqosZXWJtjYUgdx5aWbqKC2gr7wRxzVFh8Pg")
                .addBodyParameter("sms_code",passCode)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("hhhhhhhh",response);
                        try {
                            if (new JSONObject(response).getString("code").toString().equals("200")) {
                                saveUser(response);
                            }else {
                                AppUtils.dismissProgressDialog();
                                AppUtils.showInfoToast(ConfirmCreateAccountActivity.this,getString(R.string.wrong_code));
                            }
                        } catch (Exception e) {
                            AppUtils.dismissProgressDialog();
                            AppUtils.showInfoToast(ConfirmCreateAccountActivity.this,getString(R.string.wrong_code));
                        }


                    }
                    @Override
                    public void onError(ANError anError) {
                        AppUtils.dismissProgressDialog();
                        AppUtils.showInfoToast(ConfirmCreateAccountActivity.this,getString(R.string.dialog_error_no_internet));
                    }
                });
    }

    public void sendCode(View view) {

        phone=getIntent().getStringExtra("phone");

        if (phone==null){
            AppUtils.showInfoDialog(this,R.string.please_return_and_enter_phone_again);
            return;
        }
        AppUtils.showProgressDialog(this);

        AndroidNetworking.post("http://athelapps.com/phone/api/user/send_sms")
                .addBodyParameter("phone", "966"+phone)
                .addBodyParameter("api_key", "BUNgDZlDqosZXWJtjYUgdx5aWbqKC2gr7wRxzVFh8Pg")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("fffffff",response);
                        AppUtils.dismissProgressDialog();
                        try {
                            if (new JSONObject(response).getString("code").toString().equals("200")) {
                                token=new JSONObject(response).getJSONObject("data").getString("token");
                                AppUtils.showSuccessToast(ConfirmCreateAccountActivity.this,getString(R.string.code_send_seccuss));
                            }else {
                                AppUtils.showInfoToast(ConfirmCreateAccountActivity.this,getString(R.string.code_not_send));
                            }
                        } catch (Exception e) {
                            AppUtils.showErrorToast(ConfirmCreateAccountActivity.this,getString(R.string.code_not_send));
                        }

                    }
                    @Override
                    public void onError(ANError anError) {
                        AppUtils.dismissProgressDialog();
                        AppUtils.showErrorToast(ConfirmCreateAccountActivity.this,getString(R.string.dialog_error_no_internet));
                    }
                });
    }

    private void saveUser(String response) {

        try {
            JSONObject save_object=new JSONObject();
            JSONObject rec_object = new JSONObject(response);
            JSONObject object = rec_object.getJSONObject("data");

            userType=object.getString("role_id");

            save_object.put("id",object.getString("id"));
            save_object.put("name",object.getString("name"));
            save_object.put("phone",object.getString("phone"));
            save_object.put("email",object.getString("email"));
            save_object.put("token",object.getString("token"));
            save_object.put("gender",object.getString("gender"));
            save_object.put("city",object.getString("city_id"));
            save_object.put("role_id",object.getString("role_id"));
            save_object.put("photo",object.getString("photo"));
            if (userType.equalsIgnoreCase("4") || userType.equalsIgnoreCase("5")){
                save_object.put("latitude",object.getJSONObject("location").getString("latitude"));
                save_object.put("longtude",object.getJSONObject("location").getString("longtude"));
            }

            String user=save_object.toString();
            CacheUtils.getSharedPreferences(ConfirmCreateAccountActivity.this).edit().putString("user", user).apply();

            AppUtils.dismissProgressDialog();

            AppUtils.showSuccessToast(this,getString(R.string.phone_confirm));
            Intent intent=new Intent(ConfirmCreateAccountActivity.this, HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        } catch (JSONException e) {
            AppUtils.dismissProgressDialog();
            AppUtils.showInfoDialog(getBaseContext(),R.string.error_dialog_cannot_register);
        }

    }

}
