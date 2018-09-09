package com.phonedeals.ascom.phonestrore.ui.activity;

import android.app.Dialog;
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
import com.phonedeals.ascom.phonestrore.ui.dialog.AppDialog;
import com.phonedeals.ascom.phonestrore.util.DialogUtils;
import com.phonedeals.ascom.phonestrore.util.ValidateUtils;
import com.phonedeals.ascom.phonestrore.util.AppUtils;
import org.json.JSONException;
import org.json.JSONObject;

public class SignInActivity extends AppCompatActivity {

    private EditText phoneInput,passwordInput;
    private TextView forgetPasswordTextView,try_use_app;
    private Button registerButton;
    private String userType="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        initView();
    }

    private void initView() {
        phoneInput = findViewById(R.id.sign_in_phone_number);
        passwordInput = findViewById(R.id.password_input);
        forgetPasswordTextView = findViewById(R.id.forget_password_textView);
        try_use_app = findViewById(R.id.try_use_app);
        AppUtils.setHtmlText(R.string.forget_password_html, forgetPasswordTextView);
        AppUtils.setHtmlText(R.string.try_use_app,try_use_app);
        registerButton = findViewById(R.id.sign_up_button);
        Button loginButton = findViewById(R.id.sign_in_button);
        AppUtils.applyMediumFont(forgetPasswordTextView,try_use_app);
        AppUtils.applyBoldFont(registerButton,loginButton);
    }

    public void createNewAccount(View view) {
        if (AppUtils.isInternetAvailable(this)){
            startActivity(new Intent(SignInActivity.this,SignUpActivity.class));
        }else {
            AppUtils.showInfoDialog(SignInActivity.this,R.string.device_id_null);
        }
    }

    public void signIn(View view) {
        if (AppUtils.isInternetAvailable(this)) {
            loginUser();
        }else {
            AppUtils.showInfoDialog(SignInActivity.this,R.string.device_id_null);
        }
    }

    public void forgetPassword(View view) {
        if (AppUtils.isInternetAvailable(this)) {
            startActivity(new Intent(SignInActivity.this, RetrievePasswordActivity.class));
        }else {
            AppUtils.showInfoDialog(SignInActivity.this,R.string.device_id_null);
        }
    }

    private void loginUser() {

        final String user_phone=AppUtils.getTextContent(phoneInput);
        final String user_password=AppUtils.getTextContent(passwordInput);
        if (ValidateUtils.missingInputs(user_phone, user_password)) {
            AppUtils.showInfoDialog(this,R.string.error_dialog_missing_inputs);
            return;
        }

        if (!ValidateUtils.validPhone(user_phone)) {
            AppUtils.showInfoDialog(this,R.string.error_dialog_invalid_phone);
            return;
        }

        if (!ValidateUtils.validPassword(user_password)) {
            AppUtils.showInfoDialog(this,R.string.error_dialog_invalid_password);
            return;
        }

        if (!AppUtils.isInternetAvailable(this)) {
            AppUtils.showNoInternetDialog(this);
            return;
        }


        final String device_id=AppUtils.getFirebaseToken();

        if (device_id== null) {
            AppUtils.showInfoDialog(this,R.string.device_id_null);
            return;
        }

        AppUtils.showProgressDialog(this);

        AndroidNetworking.post("http://athelapps.com/phone/api/user/login")
                .addBodyParameter("phone", "966"+user_phone)
                .addBodyParameter("password", user_password)
                .addBodyParameter("device_id",AppUtils.getFirebaseToken())
                .addBodyParameter("api_key", "BUNgDZlDqosZXWJtjYUgdx5aWbqKC2gr7wRxzVFh8Pg")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("ggggggg",response);
                        try {

                            if (new JSONObject(response).getString("code").toString().equals("200")) {
                                saveUser(response);
                            } else if (new JSONObject(response).getString("code").toString().equals("401")){
                                AppUtils.dismissProgressDialog();
                                AppUtils.showErrorToast(SignInActivity.this,getString(R.string.error_dialog_user_not_found));
                            } else if (new JSONObject(response).getString("code").toString().equals("403")){
                                AppUtils.dismissProgressDialog();
                                verifyPhone(user_phone);
                            } else {
                                AppUtils.dismissProgressDialog();
                                AppUtils.showErrorToast(SignInActivity.this,getString(R.string.error_dialog_user_not_found));
                            }
                        } catch (Exception e) {
                            AppUtils.dismissProgressDialog();
                            AppUtils.showErrorToast(SignInActivity.this,getString(R.string.error));
                        }

                    }
                    @Override
                    public void onError(ANError anError) {
                        AppUtils.dismissProgressDialog();
                        showCanNotLoadDataDialog();                    }
                });
    }

    private void showCanNotLoadDataDialog() {
        DialogUtils.showTwoActionButtonsDialog(this, R.string.dialog_error_no_internet,
                R.string.dialog_ok, new AppDialog.Action1ButtonListener() {
                    @Override
                    public void onAction1ButtonClick(Dialog dialog) {
                        dialog.dismiss();
                    }
                }, R.string.dialog_cancel, new AppDialog.Action2ButtonListener() {
                    @Override
                    public void onAction2ButtonClick(Dialog dialog) {
                        finish();
                    }
                }, false);
    }

    private void verifyPhone(String phone) {
        DialogUtils.showTwoActionButtonsDialog(SignInActivity.this, R.string.verify_code,
                R.string.dialog_ok, new AppDialog.Action1ButtonListener() {
                    @Override
                    public void onAction1ButtonClick(Dialog dialog) {
                        dialog.dismiss();
                        Intent intent=new Intent(SignInActivity.this, ConfirmCreateAccountActivity.class);
                        intent.putExtra("phone",phone);
                        startActivity(intent);
                        finish();
                    }
                }, R.string.dialog_cancel, new AppDialog.Action2ButtonListener() {
                    @Override
                    public void onAction2ButtonClick(Dialog dialog) {
                        finish();
                    }
                }, false);
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
            CacheUtils.getSharedPreferences(SignInActivity.this).edit().putString("user", user).apply();
            AppUtils.dismissProgressDialog();
            Intent intent=new Intent(SignInActivity.this, HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        } catch (JSONException e) {
            AppUtils.dismissProgressDialog();
            AppUtils.showErrorToast(SignInActivity.this,getString(R.string.error));
        }

    }

    public void findOutApp(View view) {
        if (AppUtils.isInternetAvailable(this)) {
            startActivity(new Intent(SignInActivity.this, HomeActivity.class));
        }else {
            AppUtils.showInfoDialog(SignInActivity.this,R.string.device_id_null);
        }
    }
}