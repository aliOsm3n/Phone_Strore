package com.phonedeals.ascom.phonestrore.ui.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.phonedeals.ascom.phonestrore.interfaces.OnAcceptUseAgreementListener;
import com.phonedeals.ascom.phonestrore.R;
import com.phonedeals.ascom.phonestrore.adapters.SpinnerItemsAdapter;
import com.phonedeals.ascom.phonestrore.data.prefs.CacheUtils;
import com.phonedeals.ascom.phonestrore.ui.dialog.AppDialog;
import com.phonedeals.ascom.phonestrore.ui.dialog.UseAgreementDialog;
import com.phonedeals.ascom.phonestrore.util.DialogUtils;
import com.phonedeals.ascom.phonestrore.util.ValidateUtils;
import com.phonedeals.ascom.phonestrore.util.AppUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;


public class SignUpActivity extends AppCompatActivity implements OnAcceptUseAgreementListener {

    private EditText name;
    private EditText phoneInput;
    private EditText email;
    private EditText password;
    private TextView agreeShroot,location;
    private Button nextButton;
    private ImageView agreeShrootImage;
    private boolean acceptAgreement;
    private Spinner citySpinner;
    private Spinner genderSpinner;
    private LinearLayout layout;
    private String latitude="",longitude="",type;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        initView();
    }

    private void loadShroot(){
        AppUtils.showProgressDialog(this);
        AndroidNetworking.post("http://athelapps.com/phone/api/content/get")
                .addBodyParameter("api_key", "BUNgDZlDqosZXWJtjYUgdx5aWbqKC2gr7wRxzVFh8Pg")
                .addBodyParameter("slug","terms")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        AppUtils.dismissProgressDialog();
                        try {
                            if (new JSONObject(response).getString("code").toString().equals("200")) {
                                setShroot(new JSONObject(response).getString("data"));
                            }else if (new JSONObject(response).getString("code").toString().equals("401")){
                                AppUtils.showInfoToast(SignUpActivity.this,getString(R.string.shroot_not_upload));
                            }
                            else if (new JSONObject(response).getString("code").toString().equals("403")){
                                AppUtils.showInfoToast(SignUpActivity.this,getString(R.string.shroot_not_upload));
                            }else {
                                AppUtils.showInfoToast(SignUpActivity.this,getString(R.string.shroot_not_upload));
                            }
                        } catch (Exception e) {
                            AppUtils.showInfoToast(SignUpActivity.this,getString(R.string.shroot_not_upload));
                        }

                    }
                    @Override
                    public void onError(ANError anError) {
                        AppUtils.dismissProgressDialog();
                        showCanNotLoadDataDialog();
                    }
                });
    }

    public static List<String> getGenderNames(Context context) {
        String[] genderArray = context.getResources().getStringArray(R.array.gender_names);
        final List<String> genders = new ArrayList<>(genderArray.length);
        genders.add(genderArray[0]);
        genders.add(genderArray[1]);
        genders.add(genderArray[2]);
        return genders;
    }

    private void initView() {

        acceptAgreement=false;
        citySpinner=findViewById(R.id.city_spinner);
        genderSpinner=findViewById(R.id.gender_spinner);
        setGenderSpinner(getGenderNames(this));

        layout=findViewById(R.id.location_user);

        name=findViewById(R.id.sign_up_name);
        phoneInput = findViewById(R.id.sign_up_number);
        email = findViewById(R.id.sign_up_email);
        password = findViewById(R.id.sign_up_password);
        agreeShroot = findViewById(R.id.agree_shroot);
        location = findViewById(R.id.select_location);
        agreeShrootImage=findViewById(R.id.agree_shroot_imageView);
        agreeShrootImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadShroot();
            }
        });
        agreeShroot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadShroot();
            }
        });
        AppUtils.setHtmlText(R.string.agree_shroot_html, agreeShroot);
        nextButton = findViewById(R.id.nextButton);
        AppUtils.applyMediumFont(name,password,agreeShroot,location);
        AppUtils.applyBoldFont(nextButton);

        if (AppUtils.isInternetAvailable(this)){
            loadCities();
        }else {
            AppUtils.showNoInternetDialog(this);
        }
    }

    private void setShroot(String shroot) {
        new UseAgreementDialog(SignUpActivity.this,SignUpActivity.this, shroot).showDialog();
    }

    private void showCanNotLoadDataDialog() {
        DialogUtils.showTwoActionButtonsDialog(SignUpActivity.this, R.string.dialog_error_no_internet,
                R.string.dialog_ok, new AppDialog.Action1ButtonListener() {
                    @Override
                    public void onAction1ButtonClick(Dialog dialog) {
                        dialog.dismiss();
                        loadCities();
                    }
                }, R.string.dialog_cancel, new AppDialog.Action2ButtonListener() {
                    @Override
                    public void onAction2ButtonClick(Dialog dialog) {
                        finish();
                    }
                }, false);
    }

    public void setCitySpinner(List<String> list){
        SpinnerItemsAdapter adapter=new SpinnerItemsAdapter(list);
        citySpinner.setAdapter(adapter);
    }

    public void setGenderSpinner(List<String> list){
        SpinnerItemsAdapter adapter=new SpinnerItemsAdapter(list);
        genderSpinner.setAdapter(adapter);
    }

    public void next(View view) {
        if (!acceptAgreement){
            AppUtils.showInfoDialog(this,R.string.error_toast_accept_agreement);
        }else {
            registerUser();
        }
    }

    private void loadCities() {
        AppUtils.showProgressDialog(this);
        AndroidNetworking.post("http://athelapps.com/phone/api/list/cities")
                .addBodyParameter("api_key", "BUNgDZlDqosZXWJtjYUgdx5aWbqKC2gr7wRxzVFh8Pg")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        AppUtils.dismissProgressDialog();
                        try {
                            if (new JSONObject(response).getString("code").toString().equalsIgnoreCase("200")) {

                                JSONObject obj = new JSONObject(response);

                                JSONArray object = obj.getJSONArray("data");

                                ArrayList<String> list = new ArrayList<>();
                                list.add(getString(R.string.select_city));
                                for (int i = 0; i < object.length(); i++) {
                                    list.add(new JSONObject(object.getString(i)).getString("name"));
                                }
                                setCitySpinner(list);
                            } else {
                                showCanNotLoadDataDialog();
                            }
                        } catch (JSONException e) {
                            showCanNotLoadDataDialog();
                        }
                    }
                    @Override
                    public void onError(ANError anError) {
                        AppUtils.dismissProgressDialog();
                        showCanNotLoadDataDialog();
                    }
                });
    }

    private void registerUser() {

        final int selectedCity = citySpinner.getSelectedItemPosition();
        int selectedGender = genderSpinner.getSelectedItemPosition();
        final String user_name=AppUtils.getTextContent(name);
        final String user_phone=AppUtils.getTextContent(phoneInput);
        final String user_email=AppUtils.getTextContent(email);
        final String user_password=AppUtils.getTextContent(password);
        if (ValidateUtils.missingInputs(user_name, user_phone, user_email, user_password)) {
            AppUtils.showInfoDialog(this,R.string.error_dialog_missing_inputs);
            return;
        }

        if (type==null) {
            AppUtils.showInfoDialog(this,R.string.error_dialog_missing_user_type);
            return;
        }

        if (!type.equalsIgnoreCase("2")){
            if (latitude==null || longitude==null) {
                AppUtils.showInfoDialog(this,R.string.error_dialog_missing_location);
                return;
            }
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

        if (selectedCity == 0 || selectedCity == -1) {
            // no city selected
            AppUtils.showInfoDialog(this,R.string.dialog_error_select_city);
            return;
        }
        if (selectedGender == 0) {
            AppUtils.showInfoDialog(this,R.string.error_dialog_seclec_gender);
            return;
        }
        String gender=genderSpinner.getSelectedItem().toString();

        String device_id=AppUtils.getFirebaseToken();

        if (device_id== null) {
            AppUtils.showInfoDialog(this,R.string.device_id_null);
            return;
        }
        AppUtils.showProgressDialog(this);

        AndroidNetworking.post("http://athelapps.com/phone/api/user/register")//283976452
                .addBodyParameter("api_key", "BUNgDZlDqosZXWJtjYUgdx5aWbqKC2gr7wRxzVFh8Pg")
                .addBodyParameter("name",user_name)
                .addBodyParameter("phone", "966"+user_phone)
                .addBodyParameter("email", user_email)
                .addBodyParameter("password", user_password)
                .addBodyParameter("city_id", String.valueOf(selectedCity))
                .addBodyParameter("gender", gender)
                .addBodyParameter("device_id", device_id)
                .addBodyParameter("role_id",type)
                .addBodyParameter("location_lat",latitude)
                .addBodyParameter("location_lng",longitude)
                .setPriority(Priority.HIGH)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("ggggggg",response);
                        AppUtils.dismissProgressDialog();
                        try {
                            if (new JSONObject(response).getString("code").equalsIgnoreCase("200")){

                                String token = new JSONObject(response).getJSONObject("data").getString("token");
                                Intent intent=new Intent(SignUpActivity.this, ConfirmCreateAccountActivity.class);
                                intent.putExtra("token",token);
                                intent.putExtra("phone",user_phone);
                                startActivity(intent);
                            } else if (new JSONObject(response).getString("code").equalsIgnoreCase("205")){
                                AppUtils.showErrorToast(SignUpActivity.this,new JSONObject(response).getString("message"));
                            } else {
                                AppUtils.showInfoDialog(SignUpActivity.this,R.string.error_dialog_cannot_register);
                            }
                        } catch (JSONException e) {
                            AppUtils.showInfoDialog(SignUpActivity.this,R.string.error_dialog_cannot_register);
                        }
                    }
                    @Override
                    public void onError(ANError anError) {
                        AppUtils.dismissProgressDialog();
                        AppUtils.showInfoDialog(SignUpActivity.this,R.string.error_dialog_cannot_register);
                    }
                });
    }

    @Override
    public void acceptUseAgreement() {
        acceptAgreement = true;
        agreeShrootImage.setImageResource(R.drawable.ic_agreement_check);
    }

    public void onRadioButtonClicked(View view) {

        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.user:
                if (checked)
                    layout.setVisibility(View.GONE);
                    type="2";
                    break;
            case R.id.company:
                if (checked)
                    layout.setVisibility(View.VISIBLE);
                    type="4";
                    break;
            case R.id.fair:
                if (checked)
                    layout.setVisibility(View.VISIBLE);
                    type="5";
                    break;
        }
    }


    public void selectLocation(View view) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION }, 0);
        }else {
            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                // Logic to handle location object
                                float l = (float) location.getLatitude();
                                float l2 = (float) location.getLongitude();
                                Intent intent = new Intent(SignUpActivity.this, IntroduceOfferActivity.class);
                                intent.putExtra("latitude", l);
                                intent.putExtra("longitude", l2);
                                startActivityForResult(intent,105);
                            }
                        }
                    });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 105) {
            if (resultCode == RESULT_OK) {
                 latitude=data.getStringExtra("latitude");
                 longitude=data.getStringExtra("longitude");

                Log.e("wwwwwwwwww",latitude+"   "+longitude);
            }
        }

    }

    private FusedLocationProviderClient mFusedLocationClient;

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 0) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

                mFusedLocationClient.getLastLocation()
                        .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                // Got last known location. In some rare situations this can be null.
                                if (location != null) {
                                    // Logic to handle location object
                                    float l = (float) location.getLatitude();
                                    float l2 = (float) location.getLongitude();
                                    Intent intent = new Intent(SignUpActivity.this, IntroduceOfferActivity.class);
                                    intent.putExtra("latitude", l);
                                    intent.putExtra("longitude", l2);
                                    startActivityForResult(intent,105);
                                } else {
                                    askForGpsPermissions();
                                }
                            }
                        });
            }
        }
    }

    private void askForGpsPermissions(){
        final AlertDialog.Builder builder =  new AlertDialog.Builder(this);
        final String action = Settings.ACTION_LOCATION_SOURCE_SETTINGS;
        DialogUtils.showTwoActionButtonsDialog(this, R.string.gps,
                R.string.dialog_ok, new AppDialog.Action1ButtonListener() {
                    @Override
                    public void onAction1ButtonClick(Dialog dialog) {
                        dialog.dismiss();
                        startActivity(new Intent(action));
                    }
                }, R.string.dialog_cancel, new AppDialog.Action2ButtonListener() {
                    @Override
                    public void onAction2ButtonClick(Dialog dialog) {
                        dialog.dismiss();
                    }
                }, false);
    }
}