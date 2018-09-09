package com.phonedeals.ascom.phonestrore.ui.fragment;


import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.phonedeals.ascom.phonestrore.R;
import com.phonedeals.ascom.phonestrore.adapters.SpinnerItemsAdapter;
import com.phonedeals.ascom.phonestrore.data.prefs.CacheUtils;
import com.phonedeals.ascom.phonestrore.ui.activity.CongratulationActivity;
import com.phonedeals.ascom.phonestrore.ui.dialog.AppDialog;
import com.phonedeals.ascom.phonestrore.util.AppUtils;
import com.phonedeals.ascom.phonestrore.util.DialogUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class OrderFragment extends Fragment {

    private Spinner select_type,select_model,select_city,select_storage;
    private ImageView newPhone,usedPhone;
    private String mobile_status="new";
    private boolean isModelLoaded,isStorageLoaded,isCityLoaded;
    private List<String> typeIds=new ArrayList<>();
    private List<String> modelIds=new ArrayList<>();
    private List<String> memoryIds=new ArrayList<>();
    private List<String> cityIds=new ArrayList<>();
    private String type_id,model_id,memory_id,city_id;
    private int selectedType,selectedModel,selectedStorage,selectedCity;

    public OrderFragment() {
    }

    public static OrderFragment newInstance() {
        OrderFragment fragment = new OrderFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_order, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        TextView type=view.findViewById(R.id.type);
        final TextView selectType=view.findViewById(R.id.or_fr_select_type);
        final TextView selectModel=view.findViewById(R.id.or_fr_select_model);
        final TextView selectCity=view.findViewById(R.id.or_fr_select_city);
        final TextView selectStorage=view.findViewById(R.id.or_fr_select_storage);

        select_type=view.findViewById(R.id.spinner_select_type);

        select_model=view.findViewById(R.id.spinner_select_model);

        select_city=view.findViewById(R.id.spinner_select_city);

        select_storage=view.findViewById(R.id.spinner_select_storage);

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
        TextView model=view.findViewById(R.id.model);

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
                           Log.e("iiiiiiiiiiiiiiiiiiiiii",model_id);
                           loadStorage(model_id);
                       }

                       @Override
                       public void onNothingSelected(AdapterView<?> parent) {

                       }
                   });
               }else {
                   AppUtils.showInfoToast(getActivity(),getString(R.string.you_should_select_category));
               }
            }
        });
        final TextView status=view.findViewById(R.id.phone_status);
        TextView new_phone=view.findViewById(R.id.new_phone);
        TextView used_phone=view.findViewById(R.id.used_phone);

        newPhone=view.findViewById(R.id.check_box_new);
        usedPhone=view.findViewById(R.id.check_box_used);

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
        TextView city=view.findViewById(R.id.city);


        selectCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isCityLoaded){
                    select_city.performClick();
                    select_city.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            selectCity.setText(parent.getItemAtPosition(position).toString());
                            city_id=cityIds.get(position);
                            Log.e("iiiiiiiiiiiiiiiiiiiiii",city_id);

                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });
                }else {
                    AppUtils.showInfoToast(getActivity(),getString(R.string.you_should_select_memory));
                }
            }
        });

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
                            loadCities();
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });
                }else {
                    AppUtils.showInfoToast(getActivity(),getString(R.string.you_should_select_model));
                }
            }
        });
        Button order_show_price=view.findViewById(R.id.add_order_price);
        order_show_price.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addShowPrice();
            }
        });

        loadTypes();
        TextView memory=view.findViewById(R.id.storage);
        AppUtils.applyBoldFont(order_show_price);
        AppUtils.applyLightFont(type,selectType,model,selectModel,status,new_phone,used_phone,city,selectCity,selectStorage,memory);
    }

    private void loadStorage(String type) {
        AppUtils.showProgressDialog(getActivity());
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
                                list.add(getString(R.string.mobile_select_memory));
                                memoryIds.add("0");
                                for (int i = 0; i < object.length(); i++) {
                                    list.add(new JSONObject(object.getString(i)).getString("title"));
                                    memoryIds.add(new JSONObject(object.getString(i)).getString("id"));
                                }
                                isStorageLoaded=true;
                                setStorageSpinner(list);
                            }
                        } catch (JSONException e) {
                            AppUtils.showErrorToast(getActivity(),getString(R.string.error));
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

    private void loadTypes() {
        AppUtils.showProgressDialog(getActivity());

        AndroidNetworking.post("http://athelapps.com/phone/api/list/categories")
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
                                list.add(getString(R.string.select_type));
                                typeIds.add("0");
                                for (int i = 0; i < object.length(); i++) {
                                    list.add(new JSONObject(object.getString(i)).getString("name"));
                                    typeIds.add(new JSONObject(object.getString(i)).getString("id"));
                                }
                                setTypeSpinner(list);
                            }
                        } catch (JSONException e) {
                            AppUtils.showErrorToast(getActivity(),getString(R.string.error));
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        AppUtils.dismissProgressDialog();
                        showCanNotLoadDataDialog();
                    }
                });
    }

    private void loadModel(String model) {
        AppUtils.showProgressDialog(getActivity());
        AndroidNetworking.post("http://athelapps.com/phone/api/list/type")
                .addBodyParameter("api_key", "BUNgDZlDqosZXWJtjYUgdx5aWbqKC2gr7wRxzVFh8Pg")
                .addBodyParameter("category_id",model)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        AppUtils.dismissProgressDialog();
                        Log.e("loadModel",response+"hhhh");
                        try {
                            if (new JSONObject(response).getString("code").toString().equalsIgnoreCase("200")) {

                                JSONObject obj = new JSONObject(response);

                                JSONArray object = obj.getJSONArray("data");

                                ArrayList<String> list = new ArrayList<>();
                                list.add(getString(R.string.select_model));
                                modelIds.add("0");
                                for (int i = 0; i < object.length(); i++) {
                                    list.add(new JSONObject(object.getString(i)).getString("title"));
                                    modelIds.add(new JSONObject(object.getString(i)).getString("id"));
                                }
                                isModelLoaded=true;
                                setModelSpinner(list);
                            }
                        } catch (JSONException e) {
                            AppUtils.showErrorToast(getActivity(),getString(R.string.error));
                        }
                    }
                    @Override
                    public void onError(ANError anError) {
                        AppUtils.dismissProgressDialog();
                        showCanNotLoadDataDialog();
                    }
                });
    }

    private void loadCities() {
        AppUtils.showProgressDialog(getActivity());
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
                                cityIds.add("0");
                                for (int i = 0; i < object.length(); i++) {
                                    list.add(new JSONObject(object.getString(i)).getString("name"));
                                    cityIds.add(new JSONObject(object.getString(i)).getString("id"));
                                }
                                isCityLoaded=true;
                                setCitySpinner(list);
                            }
                        } catch (JSONException e) {
                            AppUtils.showErrorToast(getActivity(),getString(R.string.error));
                        }
                    }
                    @Override
                    public void onError(ANError anError) {
                        AppUtils.dismissProgressDialog();
                        showCanNotLoadDataDialog();
                    }
                });
    }

    private void setTypeSpinner(ArrayList<String> list) {
        SpinnerItemsAdapter adapter=new SpinnerItemsAdapter(list);
        select_type.setAdapter(adapter);
    }

    public void setCitySpinner(List<String> list){
        SpinnerItemsAdapter adapter=new SpinnerItemsAdapter(list);
        select_city.setAdapter(adapter);
    }

    private void setModelSpinner(ArrayList<String> list) {
        SpinnerItemsAdapter adapter=new SpinnerItemsAdapter(list);
        select_model.setAdapter(adapter);
    }

    private void showCanNotLoadDataDialog() {
        DialogUtils.showTwoActionButtonsDialog(getActivity(), R.string.dialog_error_no_internet,
                R.string.dialog_ok, new AppDialog.Action1ButtonListener() {
                    @Override
                    public void onAction1ButtonClick(Dialog dialog) {
                        dialog.dismiss();
                        loadTypes();
                    }
                }, R.string.dialog_cancel, new AppDialog.Action2ButtonListener() {
                    @Override
                    public void onAction2ButtonClick(Dialog dialog) {
                        getActivity().onBackPressed();
                    }
                }, false);
    }

    private void addShowPrice() {

        selectedType = select_type.getSelectedItemPosition();
        selectedModel = select_model.getSelectedItemPosition();
        selectedStorage = select_storage.getSelectedItemPosition();
        selectedCity = select_city.getSelectedItemPosition();

        if (!AppUtils.isInternetAvailable(getActivity())) {
            AppUtils.showNoInternetDialog(getActivity());
            return;
        }
        if (selectedType == 0 || selectedType == -1) {
            // no city selected
            AppUtils.showInfoDialog(getActivity(),R.string.dialog_error_select_category);
            return;
        }
        if (selectedModel == 0 || selectedModel == -1) {
            AppUtils.showInfoDialog(getActivity(),R.string.dialog_error_select_model);
            return;
        }
        if (selectedStorage == 0 || selectedStorage == -1) {
            AppUtils.showInfoDialog(getActivity(),R.string.dialog_error_select_storage);
            return;
        }

        if (selectedCity == 0 || selectedCity == -1) {
            AppUtils.showInfoDialog(getActivity(),R.string.dialog_error_select_city);
            return;
        }

        AppUtils.showProgressDialog(getActivity());
        AndroidNetworking.post("http://athelapps.com/phone/api/order/create")
                .addBodyParameter("api_key", "BUNgDZlDqosZXWJtjYUgdx5aWbqKC2gr7wRxzVFh8Pg")
                .addBodyParameter("token", CacheUtils.getUserToken(getActivity(),"user"))
                .addBodyParameter("status", mobile_status)
                .addBodyParameter("city_id", city_id)
                .addBodyParameter("category_id", type_id)
                .addBodyParameter("type_id", model_id)
                .addBodyParameter("memory_id",memory_id)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        AppUtils.dismissProgressDialog();
                        try {

                            if (new JSONObject(response).getString("code").toString().equalsIgnoreCase("200")) {
                                AppUtils.showSuccessToast(getActivity(),getString(R.string.your_request_done));
                                getActivity().startActivity(new Intent(getActivity(), CongratulationActivity.class));
                            }else if (new JSONObject(response).getString("code").toString().equalsIgnoreCase("401")){
                                AppUtils.showErrorToast(getActivity(),getString(R.string.your_request_not_done));
                            }else {
                                AppUtils.showErrorToast(getActivity(),getString(R.string.your_request_not_done));
                            }

                        } catch (Exception e) {
                            AppUtils.showErrorToast(getActivity(),getString(R.string.your_request_not_done));
                        }

                    }
                    @Override
                    public void onError(ANError anError) {
                        AppUtils.dismissProgressDialog();
                        AppUtils.showErrorToast(getActivity(),getString(R.string.your_request_not_done));
                    }
                });
    }

}
