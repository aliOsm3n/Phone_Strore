package com.phonedeals.ascom.phonestrore.ui.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.phonedeals.ascom.phonestrore.R;
import com.phonedeals.ascom.phonestrore.adapters.SpinnerItemsAdapter;
import com.phonedeals.ascom.phonestrore.ui.dialog.AppDialog;
import com.phonedeals.ascom.phonestrore.util.AppUtils;
import com.phonedeals.ascom.phonestrore.util.DialogUtils;
import com.phonedeals.ascom.phonestrore.util.ValidateUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class FilterActivity extends AppCompatActivity {

    private Spinner select_type,select_model,select_city,select_color,select_storage;
    private ImageView newPhone,usedPhone;
    private EditText phonePrice;
    private Toolbar toolbar;
    private TextView title;
    private boolean isStorageLoaded,isModelLoaded;
    private List<String> typeIds=new ArrayList<>();
    private List<String> modelIds=new ArrayList<>();
    private List<String> memoryIds=new ArrayList<>();
    private List<String> cityIds=new ArrayList<>();
    private String type_id="",model_id="",memory_id="",city_id="",color_value="",price_value="",mobile_status="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);
        initView();
    }

    private void initView() {

        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(R.drawable.ic_back);
        title=findViewById(R.id.toolbar_title);
        AppUtils.applyMediumFont(title);

        TextView type=findViewById(R.id.type);
        final TextView selectType=findViewById(R.id.select_type);
        select_type=findViewById(R.id.spinner_select_type);
        selectType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                select_type.performClick();
                select_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        selectType.setText(parent.getItemAtPosition(position).toString());
                        type_id=typeIds.get(position);
                        loadModel(type_id);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
            }
        });

        TextView model=findViewById(R.id.model);
        final TextView selectModel=findViewById(R.id.select_model);
        select_model=findViewById(R.id.spinner_select_model);
        selectModel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isModelLoaded){
                    select_model.performClick();
                    select_model.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            selectModel.setText(parent.getItemAtPosition(position).toString());
                            model_id=modelIds.get(position);
                            loadStorage(model_id);
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });
                }else {
                    AppUtils.showInfoToast(FilterActivity.this,getString(R.string.select_type));
                }
            }
        });

        TextView price=(TextView)findViewById(R.id.phone_price);
        phonePrice=findViewById(R.id.add_price);

        TextView status= findViewById(R.id.phone_status);
        newPhone= findViewById(R.id.check_box_new);
        usedPhone= findViewById(R.id.check_box_used);
        TextView new_phone= findViewById(R.id.new_phone);
        TextView used_phone= findViewById(R.id.used_phone);

        TextView color= findViewById(R.id.color);
        final TextView selectColor= findViewById(R.id.select_color);
        select_color= findViewById(R.id.spinner_select_color);
        selectColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                select_color.performClick();
                select_color.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        selectColor.setText(parent.getItemAtPosition(position).toString());
                        color_value=parent.getItemAtPosition(position).toString();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
            }
        });

        TextView storage=findViewById(R.id.storage);
        final TextView selectStorage=(TextView)findViewById(R.id.select_storage);
        select_storage=findViewById(R.id.spinner_select_storage);
        selectStorage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isStorageLoaded){
                    select_storage.performClick();
                    select_storage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            selectStorage.setText(parent.getItemAtPosition(position).toString());
                            memory_id=memoryIds.get(position);
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });
                }else {
                    AppUtils.showInfoToast(FilterActivity.this,getString(R.string.select_model));
                }
            }
        });

        TextView city=findViewById(R.id.city);
        final TextView selectCity=findViewById(R.id.select_city);
        select_city=findViewById(R.id.spinner_select_city);
        selectCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                select_city.performClick();
                select_city.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        selectCity.setText(parent.getItemAtPosition(position).toString());
                        city_id=cityIds.get(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
            }
        });

        newPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newPhone.setImageResource(R.drawable.check_box_bg_image);
                usedPhone.setImageResource(R.drawable.uncheck_box_bg_image);
                mobile_status="new";
            }
        });

        usedPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                usedPhone.setImageResource(R.drawable.check_box_bg_image);
                newPhone.setImageResource(R.drawable.uncheck_box_bg_image);
                mobile_status="used";
            }
        });



        Button search=findViewById(R.id.search);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search();
            }
        });
        AppUtils.showProgressDialog(this);
        loadTypes();
        loadCities();
        setColorSpinner(getColors(this));
        AppUtils.applyBoldFont(search);
        AppUtils.applyLightFont(type,selectType,model,selectModel,price,status,new_phone,used_phone,color,selectColor,storage,selectStorage,city,selectCity);
    }

    private void search() {

        if (!AppUtils.isInternetAvailable(this)) {
            AppUtils.showNoInternetDialog(this);
            return;
        }

        price_value=AppUtils.getTextContent(phonePrice);

        AppUtils.showProgressDialog(this);

        AndroidNetworking.post("http://athelapps.com/phone/api/item/search")
                .addBodyParameter("api_key", "BUNgDZlDqosZXWJtjYUgdx5aWbqKC2gr7wRxzVFh8Pg")
                .addBodyParameter("price", price_value)
                .addBodyParameter("color", color_value)
                .addBodyParameter("status", mobile_status)
                .addBodyParameter("category_id", type_id)
                .addBodyParameter("type_id", model_id)
                .addBodyParameter("memory_id", memory_id)
                .addBodyParameter("city_id", city_id)
                .setPriority(Priority.HIGH)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        AppUtils.dismissProgressDialog();
                        try {
                            if (new JSONObject(response).getString("code").equalsIgnoreCase("200")) {
                                Intent intent=new Intent(FilterActivity.this, CategoryActivity.class);
                                intent.putExtra("json",response);
                                startActivity(intent);
                                finish();
                            } else if ( new JSONObject(response).getString("code").toString().equalsIgnoreCase("401")) {
                                AppUtils.showErrorToast(FilterActivity.this,getString(R.string.your_request_not_done));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            AppUtils.showErrorToast(FilterActivity.this,getString(R.string.error));
                        }
                    }
                    @Override
                    public void onError(ANError anError) {
                        AppUtils.dismissProgressDialog();
                        AppUtils.showErrorToast(FilterActivity.this,getString(R.string.dialog_error_no_internet));
                    }
                });
    }

    private void loadTypes() {
        AndroidNetworking.post("http://athelapps.com/phone/api/list/categories")
                .addBodyParameter("api_key", "BUNgDZlDqosZXWJtjYUgdx5aWbqKC2gr7wRxzVFh8Pg")
                .setPriority(Priority.HIGH)
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
                                list.add(getString(R.string.select_type));
                                typeIds.add("0");
                                for (int i = 0; i < object.length(); i++) {
                                    list.add(new JSONObject(object.getString(i)).getString("name"));
                                    typeIds.add(new JSONObject(object.getString(i)).getString("id"));
                                }
                                setTypeSpinner(list);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            AppUtils.showErrorToast(FilterActivity.this,getString(R.string.error));

                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        AppUtils.dismissProgressDialog();
                        showCanNotLoadDataDialog();
                    }
                });
    }

    public static List<String> getColors(Context context) {
        String[] colorArray = context.getResources().getStringArray(R.array.color_names);
        final List<String> colors = new ArrayList<>(colorArray.length);
        colors.add(colorArray[0]);
        colors.add(colorArray[1]);
        colors.add(colorArray[2]);
        colors.add(colorArray[3]);
        colors.add(colorArray[4]);
        colors.add(colorArray[5]);
        colors.add(colorArray[6]);
        return colors;
    }

    private void setTypeSpinner(ArrayList<String> list) {
        SpinnerItemsAdapter adapter=new SpinnerItemsAdapter(list);
        select_type.setAdapter(adapter);
    }

    private void showCanNotLoadDataDialog() {
        DialogUtils.showTwoActionButtonsDialog(this, R.string.dialog_error_no_internet,
                R.string.dialog_ok, new AppDialog.Action1ButtonListener() {
                    @Override
                    public void onAction1ButtonClick(Dialog dialog) {
                        dialog.dismiss();
                        loadTypes();
                    }
                }, R.string.dialog_cancel, new AppDialog.Action2ButtonListener() {
                    @Override
                    public void onAction2ButtonClick(Dialog dialog) {
                        finish();
                    }
                }, false);
    }

    private void loadStorage(String type) {
        AppUtils.showProgressDialog(this);
        AndroidNetworking.post("http://athelapps.com/phone/api/list/memory")
                .addBodyParameter("api_key", "BUNgDZlDqosZXWJtjYUgdx5aWbqKC2gr7wRxzVFh8Pg")
                .addBodyParameter("type_id",type)
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
                                list.add(getString(R.string.select_storage));
                                memoryIds.add("0");
                                for (int i = 0; i < object.length(); i++) {
                                    list.add(new JSONObject(object.getString(i)).getString("title"));
                                    memoryIds.add(new JSONObject(object.getString(i)).getString("id"));
                                }
                                isStorageLoaded=true;
                                setStorageSpinner(list);
                            }else {
                                AppUtils.showErrorToast(FilterActivity.this,getString(R.string.storage_not_loaded));
                            }
                        } catch (JSONException e) {
                            AppUtils.showErrorToast(FilterActivity.this,getString(R.string.error));
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        AppUtils.dismissProgressDialog();
                        showCanNotLoadDataDialog();
                    }
                });
    }

    private void setStorageSpinner(ArrayList<String> list) {
        SpinnerItemsAdapter adapter=new SpinnerItemsAdapter(list);
        select_storage.setAdapter(adapter);
    }

    private void setColorSpinner(List<String> list) {
        SpinnerItemsAdapter adapter=new SpinnerItemsAdapter(list);
        select_color.setAdapter(adapter);
    }

    private void loadModel(String model) {
        AppUtils.showProgressDialog(this);
        AndroidNetworking.post("http://athelapps.com/phone/api/list/type")
                .addBodyParameter("api_key", "BUNgDZlDqosZXWJtjYUgdx5aWbqKC2gr7wRxzVFh8Pg")
                .addBodyParameter("category_id",model)
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
                                list.add(getString(R.string.sel_model));
                                modelIds.add("0");
                                for (int i = 0; i < object.length(); i++) {
                                    list.add(new JSONObject(object.getString(i)).getString("title"));
                                    modelIds.add(new JSONObject(object.getString(i)).getString("id"));
                                }
                                isModelLoaded=true;
                                setModelSpinner(list);
                            }else {
                                AppUtils.showErrorToast(FilterActivity.this,getString(R.string.model_not_loaded));
                            }
                        } catch (JSONException e) {
                            AppUtils.showErrorToast(FilterActivity.this,getString(R.string.error));
                        }
                    }
                    @Override
                    public void onError(ANError anError) {
                        AppUtils.dismissProgressDialog();
                        showCanNotLoadDataDialog();
                    }
                });
    }

    private void setModelSpinner(ArrayList<String> list) {
        SpinnerItemsAdapter adapter=new SpinnerItemsAdapter(list);
        select_model.setAdapter(adapter);
    }

    private void loadCities() {
        AndroidNetworking.post("http://athelapps.com/phone/api/list/cities")
                .addBodyParameter("api_key", "BUNgDZlDqosZXWJtjYUgdx5aWbqKC2gr7wRxzVFh8Pg")
                .setPriority(Priority.HIGH)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            if (new JSONObject(response).getString("code").toString().equalsIgnoreCase("200")) {

                                JSONObject obj = new JSONObject(response);

                                JSONArray object = obj.getJSONArray("data");

                                ArrayList<String> list = new ArrayList<>();
                                list.add(getString(R.string.select_city));
                                cityIds.add("0");
                                for (int i = 0; i < object.length(); i++) {
                                    list.add(new JSONObject(object.getString(i)).getString("name"));
                                    cityIds.add(new JSONObject(object.getString(i)).getString("id"));
                                }
                                setCitySpinner(list);
                            }else {
                                AppUtils.showErrorToast(FilterActivity.this,getString(R.string.cities_not_loaded));
                            }
                        } catch (JSONException e) {
                            AppUtils.showErrorToast(FilterActivity.this,getString(R.string.error));
                        }
                    }
                    @Override
                    public void onError(ANError anError) {
                        AppUtils.dismissProgressDialog();
                        AppUtils.showErrorToast(FilterActivity.this,getString(R.string.cities_not_loaded));
                    }
                });
    }

    public void setCitySpinner(List<String> list){
        SpinnerItemsAdapter adapter=new SpinnerItemsAdapter(list);
        select_city.setAdapter(adapter);
    }

}