package com.phonedeals.ascom.phonestrore.ui.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.phonedeals.ascom.phonestrore.R;
import com.phonedeals.ascom.phonestrore.adapters.SpinnerItemsAdapter;
import com.phonedeals.ascom.phonestrore.data.prefs.CacheUtils;
import com.phonedeals.ascom.phonestrore.ui.dialog.AppDialog;
import com.phonedeals.ascom.phonestrore.util.AppUtils;
import com.phonedeals.ascom.phonestrore.util.DialogUtils;
import com.phonedeals.ascom.phonestrore.util.ValidateUtils;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

public class UserProfileActivity extends AppCompatActivity {

    private EditText name;
    private EditText phoneInput;
    private EditText countryKey;
    private EditText email;
    private Button nextButton;
    private Spinner citySpinner;
    private Spinner genderSpinner;
    private TextView changePassword,location;
    private Bitmap bitmap;
    private ImageView profilePicture;
    private LinearLayout layout;
    private String userType="";
    private String latitude="",longitude="";
    private FusedLocationProviderClient mFusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        initView();
    }

    private void initView() {

        profilePicture = findViewById(R.id.profile_picture);

        citySpinner = findViewById(R.id.city_spinner);
        genderSpinner = findViewById(R.id.gender_spinner);
        layout=findViewById(R.id.location_user);

        name = findViewById(R.id.sign_up_name);
        name.setText(CacheUtils.getUserName(this,"user"));
        phoneInput = findViewById(R.id.sign_up_number);
        phoneInput.setText(CacheUtils.getUserPhone(this,"user").substring(3));
        email = findViewById(R.id.sign_up_email);
        email.setText(CacheUtils.getUserEmail(this,"user"));
        changePassword = findViewById(R.id.change_password_textView);
        countryKey = findViewById(R.id.country_code);

        userType=CacheUtils.getUserType(this,"user");
        location = findViewById(R.id.select_location);

        if (userType.equalsIgnoreCase("4") || userType.equalsIgnoreCase("5")){
            latitude=CacheUtils.getUserLatitude(this,"user");
            longitude=CacheUtils.getUserLongtude(this,"user");
            Log.e("wwwwwwwww",latitude+" hh "+longitude+"   "+userType);
        }
        if (CacheUtils.getUserPhoto(this,"user")!=null){
            try {
                Picasso.with(this).load(CacheUtils.getUserPhoto(this,"user")).placeholder(R.drawable.man).into(profilePicture);
            }catch (Exception e){

            }
        }
        AppUtils.setHtmlText(R.string.change_password_html, changePassword);
        nextButton = findViewById(R.id.nextButton);
        AppUtils.applyMediumFont(name, changePassword,location);
        AppUtils.applyBoldFont(nextButton);

        if (userType.equalsIgnoreCase("4")){
            layout.setVisibility(View.VISIBLE);
        } else if (userType.equalsIgnoreCase("5")){
            layout.setVisibility(View.VISIBLE);
        }

        if (AppUtils.isInternetAvailable(this)) {
            loadCities();
        } else {
            AppUtils.showNoInternetDialog(this);
        }

        setGenderSpinner(getGenderNames(this));
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
                            }
                        } catch (JSONException e) {
                            AppUtils.showErrorToast(UserProfileActivity.this,getString(R.string.cities_not_loaded));
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        AppUtils.dismissProgressDialog();
                        showCanNotLoadDataDialog();
                    }
                });
    }

    public void setCitySpinner(List<String> list) {
        SpinnerItemsAdapter adapter = new SpinnerItemsAdapter(list);
        citySpinner.setAdapter(adapter);
    }

    private void showCanNotLoadDataDialog() {
        DialogUtils.showTwoActionButtonsDialog(this, R.string.dialog_error_no_internet,
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

    public void setGenderSpinner(List<String> list) {
        SpinnerItemsAdapter adapter = new SpinnerItemsAdapter(list);
        genderSpinner.setAdapter(adapter);
    }

    public static List<String> getGenderNames(Context context) {
        String[] genderArray = context.getResources().getStringArray(R.array.gender_names);
        final List<String> genders = new ArrayList<>(genderArray.length);

        genders.add(genderArray[0]);
        genders.add(genderArray[1]);
        genders.add(genderArray[2]);
        return genders;
    }

    private void updateUser() {

        final int selectedCity = citySpinner.getSelectedItemPosition();
        int selectedGender = genderSpinner.getSelectedItemPosition();
        final String user_name = AppUtils.getTextContent(name);
        final String user_phone = AppUtils.getTextContent(phoneInput);
        final String user_email = AppUtils.getTextContent(email);
        if (ValidateUtils.missingInputs(user_name, user_phone, user_email)) {
            AppUtils.showInfoDialog(this, R.string.error_dialog_missing_inputs);
            return;
        }

        if (!ValidateUtils.validPhone(user_phone)) {
            AppUtils.showInfoDialog(this, R.string.error_dialog_invalid_phone);
            return;
        }

        if (!AppUtils.isInternetAvailable(this)) {
            AppUtils.showNoInternetDialog(this);
            return;
        }

        if (selectedCity == 0 || selectedCity == -1) {
            // no city selected
            AppUtils.showInfoDialog(this, R.string.dialog_error_select_city);
            return;
        }
        if (selectedGender == 0) {
            AppUtils.showInfoDialog(this, R.string.error_dialog_seclec_gender);
            return;
        }
        String gender = genderSpinner.getSelectedItem().toString();

        final String device_id = AppUtils.getFirebaseToken();

        if (device_id == null) {
            AppUtils.showInfoDialog(this, R.string.device_id_null);
            return;
        }

        AppUtils.showProgressDialog(this);
        AndroidNetworking.post("http://athelapps.com/phone/api/update-information")
                .addBodyParameter("api_key", "BUNgDZlDqosZXWJtjYUgdx5aWbqKC2gr7wRxzVFh8Pg")
                .addBodyParameter("city_id", String.valueOf(selectedCity))
                .addBodyParameter("name", user_name)
                .addBodyParameter("phone", "966"+user_phone)
                .addBodyParameter("email", user_email)
                .addBodyParameter("token", CacheUtils.getUserToken(UserProfileActivity.this, "user"))
                .addBodyParameter("gender", gender)
                .addBodyParameter("location_lat",latitude)
                .addBodyParameter("location_lng",longitude)
                .setPriority(Priority.HIGH)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("qqqqqqqqq",response);
                        try {
                            if (new JSONObject(response).getString("code").equalsIgnoreCase("200")) {
                                AppUtils.showSuccessToast(UserProfileActivity.this, getString(R.string.your_data_update));
                                saveUser(response);
                            } else {
                                AppUtils.dismissProgressDialog();
                                AppUtils.showErrorToast(UserProfileActivity.this, getString(R.string.your_data_not_update));
                            }
                        } catch (JSONException e) {
                            AppUtils.dismissProgressDialog();
                            AppUtils.showErrorToast(UserProfileActivity.this, getString(R.string.your_data_not_update));
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        AppUtils.dismissProgressDialog();
                        AppUtils.showErrorToast(UserProfileActivity.this, getString(R.string.your_data_not_update));
                    }
                });
    }

    public void updateProfile(View view) {
        updateUser();
    }

    public void changePassword(View view) {
        startActivity(new Intent(UserProfileActivity.this, ResetPasswordActivity.class));
    }

    public void editPhoto(View view) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE }, 0);
        }else {
            Intent intent = new Intent(Intent.ACTION_PICK, EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, 15);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 15 && resultCode == RESULT_OK) {
            Uri path = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), path);
                profilePicture.setImageBitmap(bitmap);

                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};

                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String picturePath = cursor.getString(columnIndex);
                cursor.close();
                AppUtils.showProgressDialog(UserProfileActivity.this);
                AndroidNetworking.upload("http://athelapps.com/phone/api/update-photo")
                        .addMultipartFile("photo", new File(picturePath))
                        .addMultipartParameter("token", CacheUtils.getUserToken(UserProfileActivity.this, "user"))
                        .addMultipartParameter("api_key", "BUNgDZlDqosZXWJtjYUgdx5aWbqKC2gr7wRxzVFh8Pg")
                        .setPriority(Priority.HIGH)
                        .build()
                        .getAsString(new StringRequestListener() {
                            @Override
                            public void onResponse(String response) {
                                Log.e("tttttttttttttttt",response);

                                try {
                                    if (new JSONObject(response).getString("code").toString().equalsIgnoreCase("200")) {
                                        AppUtils.showSuccessToast(UserProfileActivity.this,getString(R.string.image_update_done));
                                        saveUser(response);
                                    }else {
                                        AppUtils.dismissProgressDialog();
                                        AppUtils.showErrorToast(UserProfileActivity.this,getString(R.string.image_not_updated));
                                    }
                                } catch (JSONException e) {
                                    AppUtils.dismissProgressDialog();
                                    AppUtils.showErrorToast(UserProfileActivity.this,getString(R.string.image_not_updated));
                                }

                            }

                            @Override
                            public void onError(ANError anError) {
                                AppUtils.dismissProgressDialog();
                                AppUtils.showErrorToast(UserProfileActivity.this,getString(R.string.image_not_updated));
                            }
                        });
            } catch (Exception e) {
                e.printStackTrace();
            }


            }

        if (requestCode == 105) {
            if (resultCode == RESULT_OK) {
                latitude=data.getStringExtra("latitude");
                longitude=data.getStringExtra("longitude");

                Log.e("sssssssssss",latitude+"   "+longitude);
            }
        }
        }

    private void saveUser(String response) {

        try {
            JSONObject save_object = new JSONObject();
            JSONObject rec_object = new JSONObject(response);
            JSONObject object = rec_object.getJSONObject("data");

            save_object.put("id", object.getString("id"));
            save_object.put("name", object.getString("name"));
            save_object.put("phone", object.getString("phone"));
            save_object.put("email", object.getString("email"));
            save_object.put("token", object.getString("token"));
            save_object.put("gender", object.getString("gender"));
            save_object.put("city", object.getString("city_id"));
            save_object.put("role_id",object.getString("role_id"));
            save_object.put("photo", object.getString("photo"));
            if (userType.equalsIgnoreCase("4") || userType.equalsIgnoreCase("5")){
                save_object.put("latitude",object.getJSONObject("location").getString("latitude").toString());
                save_object.put("longtude",object.getJSONObject("location").getString("longtude").toString());
                Log.e("pppppppppppp",object.getJSONObject("location").getString("latitude")+"nnn");
            }

            String user = save_object.toString();
            CacheUtils.getSharedPreferences(UserProfileActivity.this).edit().putString("user", user).apply();

            AppUtils.dismissProgressDialog();
            startActivity(new Intent(UserProfileActivity.this, HomeActivity.class));
            finish();
        } catch (JSONException e) {
            AppUtils.dismissProgressDialog();
            e.printStackTrace();
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
                                Intent intent = new Intent(UserProfileActivity.this, IntroduceOfferActivity.class);
                                intent.putExtra("latitude", l);
                                intent.putExtra("longitude", l2);
                                startActivityForResult(intent,105);
                            }
                        }
                    });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 0) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(Intent.ACTION_PICK, EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 15);
            }
        }


        if (requestCode == 120) {
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
                                    Intent intent = new Intent(UserProfileActivity.this, IntroduceOfferActivity.class);
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
