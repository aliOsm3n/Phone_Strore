package com.phonedeals.ascom.phonestrore.ui.activity;

import android.app.Dialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.phonedeals.ascom.phonestrore.R;
import com.phonedeals.ascom.phonestrore.data.prefs.CacheUtils;
import com.phonedeals.ascom.phonestrore.ui.dialog.AppDialog;
import com.phonedeals.ascom.phonestrore.util.AppUtils;
import com.phonedeals.ascom.phonestrore.util.DialogUtils;
import com.phonedeals.ascom.phonestrore.util.ValidateUtils;

import org.json.JSONObject;

import static com.phonedeals.ascom.phonestrore.util.AppUtils.getTextContent;
import static com.phonedeals.ascom.phonestrore.util.AppUtils.showInfoDialog;

public class ResetPasswordActivity extends AppCompatActivity {

    private EditText newPassword;
    private EditText confirmNewPassword;
    private Button resetPassword;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        initView();
    }

    private void initView() {
        newPassword=findViewById(R.id.new_password);
        confirmNewPassword=findViewById(R.id.confirm_new_password);
        resetPassword=findViewById(R.id.reset_password);
        AppUtils.applyBoldFont(resetPassword);
        token=getIntent().getStringExtra("token");

    }

    public void goToHomeActivity(View view) {
        if (token!=null){
            setNewPassword();
        } else {
            changePassword();
        }
    }

    private void setNewPassword() {
        String new_Password = getTextContent(newPassword);
        String retypedNewPassword = getTextContent(confirmNewPassword);
        if (ValidateUtils.missingInputs(new_Password, retypedNewPassword)) {
            showInfoDialog(this,R.string.error_dialog_missing_inputs);
            return;
        }
        if (!ValidateUtils.validPassword(new_Password)) {
            showInfoDialog(this,R.string.error_dialog_invalid_password);
            return;
        }
        if (!new_Password.equals(retypedNewPassword)) {
            showInfoDialog(this,R.string.error_dialog_passwords_not_match);
            return;
        }

        AppUtils.showProgressDialog(this);
        AndroidNetworking.post("http://athelapps.com/phone/api/user/set_pass")
                .addBodyParameter("new_password", new_Password)
                .addBodyParameter("token",token)
                .addBodyParameter("api_key", "BUNgDZlDqosZXWJtjYUgdx5aWbqKC2gr7wRxzVFh8Pg")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        AppUtils.dismissProgressDialog();
                        try {
                            if (new JSONObject(response).getString("code").toString().equals("200")) {
                                AppUtils.showSuccessToast(ResetPasswordActivity.this,getString(R.string.password_change_done));
                                startActivity(new Intent(ResetPasswordActivity.this,SignInActivity.class));
                                finish();
                            }else {
                                AppUtils.showInfoToast(ResetPasswordActivity.this,getString(R.string.password_not_changed));
                            }
                        } catch (Exception e) {
                            AppUtils.showInfoToast(ResetPasswordActivity.this,getString(R.string.password_not_changed));
                        }

                    }
                    @Override
                    public void onError(ANError anError) {
                        AppUtils.dismissProgressDialog();
                        AppUtils.showInfoToast(ResetPasswordActivity.this,getString(R.string.dialog_error_no_internet));
                    }
                });
    }

    private void changePassword() {
        String new_Password = getTextContent(newPassword);
        String retypedNewPassword = getTextContent(confirmNewPassword);
        if (ValidateUtils.missingInputs(new_Password, retypedNewPassword)) {
            showInfoDialog(this,R.string.error_dialog_missing_inputs);
            return;
        }
        if (!ValidateUtils.validPassword(new_Password)) {
            showInfoDialog(this,R.string.error_dialog_invalid_password);
            return;
        }
        if (!new_Password.equals(retypedNewPassword)) {
            showInfoDialog(this,R.string.error_dialog_passwords_not_match);
            return;
        }

        AppUtils.showProgressDialog(this);
        AndroidNetworking.post("http://athelapps.com/phone/api/user/set_pass")
                .addBodyParameter("new_password", new_Password)
                .addBodyParameter("token",CacheUtils.getUserToken(this,"user"))
                .addBodyParameter("api_key", "BUNgDZlDqosZXWJtjYUgdx5aWbqKC2gr7wRxzVFh8Pg")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        AppUtils.dismissProgressDialog();
                        try {
                            if (new JSONObject(response).getString("code").toString().equals("200")) {
                                AppUtils.showSuccessToast(ResetPasswordActivity.this,getString(R.string.password_change_done));
                                onBackPressed();
                            }else {
                                AppUtils.showInfoToast(ResetPasswordActivity.this,getString(R.string.password_not_changed));
                            }
                        } catch (Exception e) {
                            AppUtils.showInfoToast(ResetPasswordActivity.this,getString(R.string.password_not_changed));
                        }

                    }
                    @Override
                    public void onError(ANError anError) {
                        AppUtils.dismissProgressDialog();
                        AppUtils.showInfoToast(ResetPasswordActivity.this,getString(R.string.dialog_error_no_internet));
                    }
                });
    }

}
