package com.phonedeals.ascom.phonestrore.ui.fragment;


import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.androidnetworking.interfaces.StringRequestListener;
import com.androidnetworking.interfaces.UploadProgressListener;
import com.phonedeals.ascom.phonestrore.interfaces.OnDeleteAdsImageItemClick;
import com.phonedeals.ascom.phonestrore.R;
import com.phonedeals.ascom.phonestrore.adapters.SpinnerItemsAdapter;
import com.phonedeals.ascom.phonestrore.adapters.AdsImagesAdapter;
import com.phonedeals.ascom.phonestrore.data.prefs.CacheUtils;
import com.phonedeals.ascom.phonestrore.ui.activity.HomeActivity;
import com.phonedeals.ascom.phonestrore.ui.dialog.AppDialog;
import com.phonedeals.ascom.phonestrore.util.AppUtils;
import com.phonedeals.ascom.phonestrore.util.DialogUtils;
import com.phonedeals.ascom.phonestrore.util.ValidateUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import id.zelory.compressor.Compressor;

import static android.app.Activity.RESULT_OK;
import static android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;


public class AddFragment extends Fragment implements OnDeleteAdsImageItemClick {

    private AdsImagesAdapter adsImagesAdapter;
    private Spinner select_type,select_model,select_city,select_storage,select_color;
    private List<String> adsImagesUrl = new ArrayList<>(0);
    private TextView selectType,selectModel,selectCity,selectStorage,selectColor;
    private EditText phonePrice;
    private ImageView newPhone,usedPhone;
    private String editableAdsId;
    private String mobile_status="new";
    private EditText about_phone_content;
    private Button add_ads;
    private boolean isModelLoaded,isStorageLoaded,isCitiesLoaded;
    private int selectedType,selectedModel,selectedStorage,selectedCity,selectedColor;
    private int indecator=0;
    private List<String> typeIds=new ArrayList<>();
    private List<String> modelIds=new ArrayList<>();
    private List<String> memoryIds=new ArrayList<>();
    private List<String> cityIds=new ArrayList<>();
    private String type_id="",model_id="",memory_id="",city_id="",color_value="";

    public AddFragment() {
    }

    public static AddFragment newInstance(String id) {
        AddFragment fragment = new AddFragment();
        Bundle args = new Bundle();
        args.putString("id",id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if (arguments != null) {
            //for edit ads
            editableAdsId = arguments.getString("id");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_add, container, false);

        if (editableAdsId.equals("1")){
            initView(view);
        }else {
            initView(view);
            loadData(editableAdsId);
        }

        return view;
    }

    //load ad details to edit it
    private void loadData(String id) {
        AndroidNetworking.post("http://athelapps.com/phone/api/items")
                .addBodyParameter("api_key", "BUNgDZlDqosZXWJtjYUgdx5aWbqKC2gr7wRxzVFh8Pg")
                .addBodyParameter("item_id",id)
                .setPriority(Priority.HIGH)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            if (new JSONObject(response).getString("code").toString().equalsIgnoreCase("200")) {
                                parseData(response);
                            }else {
                                showCanNotLoadEditDataDialog();
                            }
                        } catch (JSONException e) {
                            showCanNotLoadEditDataDialog();
                        }
                    }
                    @Override
                    public void onError(ANError anError) {
                        AppUtils.dismissProgressDialog();
                        showCanNotLoadEditDataDialog();
                    }
                });
    }

    private void showCanNotLoadEditDataDialog() {
        DialogUtils.showTwoActionButtonsDialog(getActivity(), R.string.can_not_load_data,
                R.string.dialog_ok, new AppDialog.Action1ButtonListener() {
                    @Override
                    public void onAction1ButtonClick(Dialog dialog) {
                        dialog.dismiss();
                        loadData(editableAdsId);

                    }
                }, R.string.dialog_cancel, new AppDialog.Action2ButtonListener() {
                    @Override
                    public void onAction2ButtonClick(Dialog dialog) {
                        getActivity().onBackPressed();
                    }
                }, false);
    }

    private void parseData(String json){

        JSONObject jsonObject = null;
        JSONObject object = null;
        try {
            jsonObject = new JSONObject(json);
            object = jsonObject.getJSONObject("data");
            JSONArray array = new JSONArray(object.getString("photos"));
            for (int i=0;i<array.length();i++){
                adsImagesAdapter.addImage(array.getJSONObject(i).getString("photo"));
                indecator=i+1;
            }

            selectType.setText(object.getString("category_name"));
            selectModel.setText(object.getString("type_name"));
            selectCity.setText(object.getString("city_name"));
            phonePrice.setText(object.getString("price"));
            selectStorage.setText(object.getString("memory_name"));
            about_phone_content.setText(object.getString("detail"));
            selectColor.setText(object.getString("color"));
            selectedType=Integer.parseInt(object.getString("category_id"));
            selectedModel=Integer.parseInt(object.getString("type_id"));
            selectedStorage=Integer.parseInt(object.getString("memory_id"));
            selectedCity=Integer.parseInt(object.getString("city_id"));

            type_id=object.getString("category_id");
            model_id=object.getString("type_id");
            memory_id=object.getString("memory_id");
            city_id=object.getString("city_id");
            color_value=object.getString("color");
            selectColor.setText(color_value);
            if (object.getString("status").equalsIgnoreCase("new")){
                mobile_status="new";
                newPhone.setImageResource(R.drawable.check_box_bg_image);
            }else {
                mobile_status="used";
                usedPhone.setImageResource(R.drawable.check_box_bg_image);
                newPhone.setImageResource(R.drawable.uncheck_box_bg_image);
            }
            add_ads.setText(getString(R.string.edit_ads));

        } catch (JSONException e) {
            showCanNotLoadEditDataDialog();
        }

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

    private void loadModel(String model) {
        Log.e("model",model);
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
                        AppUtils.showErrorToast(getActivity(),getString(R.string.error));
                    }
                });
    }

    private void loadCities() {
        AndroidNetworking.post("http://athelapps.com/phone/api/list/cities")
                .addBodyParameter("api_key", "BUNgDZlDqosZXWJtjYUgdx5aWbqKC2gr7wRxzVFh8Pg")
                .setPriority(Priority.MEDIUM)
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
                                isCitiesLoaded=true;
                                setCitySpinner(list);
                            }
                        } catch (JSONException e) {
                            AppUtils.showErrorToast(getActivity(),getString(R.string.error));
                        }
                    }
                    @Override
                    public void onError(ANError anError) {
                        AppUtils.dismissProgressDialog();
                        AppUtils.showErrorToast(getActivity(),getString(R.string.error));
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

    private void initView(View view) {

        select_type= view.findViewById(R.id.spinner_select_type);
        select_model= view.findViewById(R.id.spinner_select_model);
        select_city= view.findViewById(R.id.spinner_select_city);
        select_storage= view.findViewById(R.id.spinner_select_storage);
        TextView type= view.findViewById(R.id.type);
        selectType= view.findViewById(R.id.add_fr_select_type);

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


        TextView model=(TextView)view.findViewById(R.id.model);
        selectModel=(TextView)view.findViewById(R.id.add_fr_select_model);
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
                           loadStorage(model_id);                       }

                       @Override
                       public void onNothingSelected(AdapterView<?> parent) {

                       }
                   });
               }else {
                   AppUtils.showInfoToast(getActivity(),getString(R.string.you_should_select_category));
               }
            }
        });
        TextView price=(TextView)view.findViewById(R.id.phone_price);
        TextView status=(TextView)view.findViewById(R.id.phone_status);
        TextView new_phone=(TextView)view.findViewById(R.id.new_phone);
        TextView used_phone=(TextView)view.findViewById(R.id.used_phone);

        phonePrice=view.findViewById(R.id.add_fr_from);
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
        selectCity=view.findViewById(R.id.add_fr_select_city);
        selectCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isCitiesLoaded) {
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
                } else {
                    AppUtils.showInfoToast(getActivity(),getString(R.string.you_should_select_memory));
                }
            }
        });

        selectStorage=view.findViewById(R.id.add_fr_select_storage);

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
        TextView add_image=view.findViewById(R.id.add_image_txt);

        TextView add_phone_image=view.findViewById(R.id.add_phone_image);

        selectColor= view.findViewById(R.id.select_color);

        select_color= view.findViewById(R.id.spinner_select_color);
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


        TextView about_phone=view.findViewById(R.id.about_phone);

        about_phone_content=view.findViewById(R.id.about_phone_content);

        add_ads= view.findViewById(R.id.add_fr_add_ads);

        add_ads.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editableAdsId.equals("1")){
                    addAds();
                }else {
                    editAds();
                }
            }
        });

        RecyclerView adsImages = view.findViewById(R.id.ads_images);
        adsImagesAdapter = new AdsImagesAdapter(this);
        adsImages.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        adsImages.setAdapter(adsImagesAdapter);
        TextView color=view.findViewById(R.id.color);
        TextView memory=view.findViewById(R.id.storage);
        AppUtils.applyBoldFont(add_ads);
        AppUtils.applyLightFont(selectStorage,type,selectType,model,selectColor,color,memory,selectModel,price,status,new_phone,used_phone,city,selectCity,add_image,about_phone,about_phone_content,memory,color);
        loadTypes();
        setColorSpinner(getColors(getActivity()));
        add_phone_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(), new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE }, 0);
                }else {
                    Intent intent = new Intent(Intent.ACTION_PICK, EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, 15);
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 0) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(Intent.ACTION_PICK, EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 15);
            }
        }
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
                                list.add(getString(R.string.select_storage));
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
                        AppUtils.showErrorToast(getActivity(),getString(R.string.error));
                    }
                });
    }

    private void setStorageSpinner(ArrayList<String> list) {
        SpinnerItemsAdapter adapter=new SpinnerItemsAdapter(list);
        select_storage.setAdapter(adapter);
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

    private void setColorSpinner(List<String> list) {
        SpinnerItemsAdapter adapter=new SpinnerItemsAdapter(list);
        select_color.setAdapter(adapter);
    }

    @Override
    public void deleteImageItemClick(String imageUrl) {
        if (indecator==1){
            AppUtils.showInfoToast(getActivity(),getString(R.string.you_should_upload_one_image));
            return;
        }

        if (imageUrl.startsWith("http")) {
            deleteAdImage(imageUrl);
            adsImagesAdapter.removeImage(imageUrl);
            adsImagesUrl.remove(imageUrl);
        } else {
            // delete image from list only because image not uploaded yet to server.
            adsImagesUrl.remove(imageUrl);
            adsImagesAdapter.removeImage(imageUrl);
            indecator=indecator-1;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 15&& resultCode == RESULT_OK) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            Cursor cursor = getActivity().getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            if (adsImagesUrl.size() == 5 || indecator == 5) {
                AppUtils.showInfoToast(getActivity(),getString(R.string.toast_max_length_image5));
                return;
            }

            adsImagesAdapter.addImage(picturePath);
            adsImagesUrl.add(picturePath);
            indecator = indecator+ 1;
        }
    }

    private void addAds() {

        selectedType = select_type.getSelectedItemPosition();
        selectedModel = select_model.getSelectedItemPosition();
        selectedStorage = select_storage.getSelectedItemPosition();
        selectedCity = select_city.getSelectedItemPosition();
        final String phone_price=AppUtils.getTextContent(phonePrice);
        final String details=AppUtils.getTextContent(about_phone_content);
        selectedColor = select_color.getSelectedItemPosition();

        if (!AppUtils.isInternetAvailable(getActivity())) {
            AppUtils.showNoInternetDialog(getActivity());
            return;
        }

        if (selectedType == 0 || selectedType == -1) {
            // no city selected
            AppUtils.showInfoDialog(getActivity(),R.string.dialog_error_select_city);
            return;
        }
        if (selectedModel == 0 || selectedModel == -1) {
            AppUtils.showInfoDialog(getActivity(),R.string.error_dialog_seclec_gender);
            return;
        }
        if (selectedStorage == 0 || selectedStorage == -1) {
            AppUtils.showInfoDialog(getActivity(),R.string.dialog_error_select_storage);
            return;
        }
        if (selectedCity == 0 || selectedCity == -1) {
            AppUtils.showInfoDialog(getActivity(),R.string.error_dialog_seclec_gender);
            return;
        }
        if (selectedColor == 0 || selectedColor == -1) {
            AppUtils.showInfoDialog(getActivity(),R.string.dialog_error_select_color);
            return;
        }
        if (ValidateUtils.missingInputs(phone_price,details)) {
            AppUtils.showInfoDialog(getActivity(),R.string.error_dialog_missing_inputs);
            return;
        }

        if (!ValidateUtils.validPrice(phone_price)) {
            AppUtils.showInfoDialog(getActivity(),R.string.invalide_price);
            return;
        }

        if (adsImagesUrl.size() == 0) {
            AppUtils.showErrorToast(getActivity(),getString(R.string.you_should_upload_one_image));
            return;
        }

        AppUtils.showProgressDialog(getActivity());

        Map<String, File> map=new HashMap<>();
//
//        if (adsImagesUrl.size()==5){
//            map.put("images[0]",CompressImage(adsImagesUrl.get(0)));
//            map.put("images[1]",CompressImage(adsImagesUrl.get(1)));
//            map.put("images[2]",CompressImage(adsImagesUrl.get(2)));
//            map.put("images[3]",CompressImage(adsImagesUrl.get(3)));
//            map.put("images[4]",CompressImage(adsImagesUrl.get(4)));
//        } else if (adsImagesUrl.size()==4){
//            map.put("images[0]",CompressImage(adsImagesUrl.get(0)));
//            map.put("images[1]",CompressImage(adsImagesUrl.get(1)));
//            map.put("images[2]",CompressImage(adsImagesUrl.get(2)));
//            map.put("images[3]",CompressImage(adsImagesUrl.get(3)));
//        } else if (adsImagesUrl.size()==3){
//            map.put("images[0]",CompressImage(adsImagesUrl.get(0)));
//            map.put("images[1]",CompressImage(adsImagesUrl.get(1)));
//            map.put("images[2]",CompressImage(adsImagesUrl.get(2)));
//        } else if (adsImagesUrl.size()==2){
//            map.put("images[0]",CompressImage(adsImagesUrl.get(0)));
//            map.put("images[1]",CompressImage(adsImagesUrl.get(1)));
//        } else if (adsImagesUrl.size()==1){
//            map.put("images[0]",CompressImage(adsImagesUrl.get(0)));
//        } else {
//            AppUtils.showErrorToast(getActivity(),getString(R.string.you_should_upload_one_image));
//            return;
//        }

        if (adsImagesUrl.size()==5){
            map.put("images[0]",new File(adsImagesUrl.get(0)));
            map.put("images[1]",new File(adsImagesUrl.get(1)));
            map.put("images[2]",new File(adsImagesUrl.get(2)));
            map.put("images[3]",new File(adsImagesUrl.get(3)));
            map.put("images[4]",new File(adsImagesUrl.get(4)));
        } else if (adsImagesUrl.size()==4){
            map.put("images[0]",new File(adsImagesUrl.get(0)));
            map.put("images[1]",new File(adsImagesUrl.get(1)));
            map.put("images[2]",new File(adsImagesUrl.get(2)));
            map.put("images[3]",new File(adsImagesUrl.get(3)));
        } else if (adsImagesUrl.size()==3){
            map.put("images[0]",new File(adsImagesUrl.get(0)));
            map.put("images[1]",new File(adsImagesUrl.get(1)));
            map.put("images[2]",new File(adsImagesUrl.get(2)));
        } else if (adsImagesUrl.size()==2){
            map.put("images[0]",new File(adsImagesUrl.get(0)));
            map.put("images[1]",new File(adsImagesUrl.get(1)));
        } else if (adsImagesUrl.size()==1){
            map.put("images[0]",new File(adsImagesUrl.get(0)));
        } else {
            AppUtils.showErrorToast(getActivity(),getString(R.string.you_should_upload_one_image));
            return;
        }


        AndroidNetworking.upload("http://athelapps.com/phone/api/item/create")
                .addMultipartFile(map)

                .addMultipartParameter("token", CacheUtils.getUserToken(getActivity(), "user"))
                .addMultipartParameter("api_key", "BUNgDZlDqosZXWJtjYUgdx5aWbqKC2gr7wRxzVFh8Pg")
                .addMultipartParameter("title", select_type.getSelectedItem().toString()+" "+select_storage.getSelectedItem().toString())
                .addMultipartParameter("status", mobile_status)
                .addMultipartParameter("detail", details)
                .addMultipartParameter("color", color_value)
                .addMultipartParameter("price", phone_price)
                .addMultipartParameter("category_id", type_id)
                .addMultipartParameter("type_id", model_id)
                .addMultipartParameter("memory_id", memory_id)
                .addMultipartParameter("city_id", city_id)
                .setPriority(Priority.HIGH)
                .build()
                .setUploadProgressListener(new UploadProgressListener() {
                    @Override
                    public void onProgress(long bytesUploaded, long totalBytes) {
                        // do anything with progress

                    }
                })
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        AppUtils.dismissProgressDialog();
                        try {
                            if (new JSONObject(String.valueOf(response)).getString("code").equals("200")) {
                                AppUtils.showSuccessToast(getActivity(),getString(R.string.your_ads_add_success));
                                startActivity(new Intent(getActivity(), HomeActivity.class));
                            }else {
                                AppUtils.showSuccessToast(getActivity(),getString(R.string.your_ad_not_add));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    @Override
                    public void onError(ANError error) {
                        AppUtils.dismissProgressDialog();
                        Log.e("ffffffffff",error.getMessage()+"");
                        AppUtils.showErrorToast(getActivity(),getString(R.string.your_ad_not_add));
                    }
                });

    }

    public  File CompressImage(String path) {

        File file=null;

        if (path!=null) {
            try {
                file = new Compressor(getActivity())

                        .setQuality(60)
                        .setCompressFormat(Bitmap.CompressFormat.PNG)
                        .setDestinationDirectoryPath(Environment.getExternalStoragePublicDirectory(
                                Environment.DIRECTORY_PICTURES).getAbsolutePath())
                        .compressToFile(new File(path));
            } catch (Exception e) {
                AppUtils.showErrorToast(getActivity(),getString(R.string.image_wrong));
                Log.e("llllllllll",e.getMessage()+"");
            }
            return file;

        }else {
            return new File("");
        }

    }

    private void editAds() {

        final String phone_price=AppUtils.getTextContent(phonePrice);
        final String details=AppUtils.getTextContent(about_phone_content);

        if (!AppUtils.isInternetAvailable(getActivity())) {
            AppUtils.showNoInternetDialog(getActivity());
            return;
        }
        if (selectedType == 0 || selectedType == -1) {
            // no city selected
            AppUtils.showInfoDialog(getActivity(),R.string.dialog_error_select_type);
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
        if (ValidateUtils.missingInputs(phone_price,details)) {
            AppUtils.showInfoDialog(getActivity(),R.string.error_dialog_missing_inputs);
            return;
        }

        Map<String, File> map=new HashMap<>();


        AppUtils.showProgressDialog(getActivity());
        if (adsImagesUrl.size()==5){
            map.put("images[0]",new File(adsImagesUrl.get(0)));
            map.put("images[1]",new File(adsImagesUrl.get(1)));
            map.put("images[2]",new File(adsImagesUrl.get(2)));
            map.put("images[3]",new File(adsImagesUrl.get(3)));
            map.put("images[4]",new File(adsImagesUrl.get(4)));
        } else if (adsImagesUrl.size()==4){
            map.put("images[0]",new File(adsImagesUrl.get(0)));
            map.put("images[1]",new File(adsImagesUrl.get(1)));
            map.put("images[2]",new File(adsImagesUrl.get(2)));
            map.put("images[3]",new File(adsImagesUrl.get(3)));
        } else if (adsImagesUrl.size()==3){
            map.put("images[0]",new File(adsImagesUrl.get(0)));
            map.put("images[1]",new File(adsImagesUrl.get(1)));
            map.put("images[2]",new File(adsImagesUrl.get(2)));
        } else if (adsImagesUrl.size()==2){
            map.put("images[0]",new File(adsImagesUrl.get(0)));
            map.put("images[1]",new File(adsImagesUrl.get(1)));
        } else if (adsImagesUrl.size()==1){
            map.put("images[0]",new File(adsImagesUrl.get(0)));
        }

        AppUtils.showProgressDialog(getActivity());

        AndroidNetworking.upload("http://athelapps.com/phone/api/item/update")
                .addMultipartFile(map)

                .addMultipartParameter("token", CacheUtils.getUserToken(getActivity(), "user"))
                .addMultipartParameter("api_key", "BUNgDZlDqosZXWJtjYUgdx5aWbqKC2gr7wRxzVFh8Pg")
                .addMultipartParameter("item_id" ,editableAdsId)
                .addMultipartParameter("title", selectType.getText().toString()+" "+selectStorage.getText().toString())
                .addMultipartParameter("status", mobile_status)
                .addMultipartParameter("detail", details)
                .addMultipartParameter("price", phone_price)
                .addMultipartParameter("color", color_value)
                .addMultipartParameter("category_id", type_id)
                .addMultipartParameter("type_id", model_id)
                .addMultipartParameter("memory_id", memory_id)
                .addMultipartParameter("city_id", city_id)
                .setPriority(Priority.HIGH)
                .build()
                .setUploadProgressListener(new UploadProgressListener() {
                    @Override
                    public void onProgress(long bytesUploaded, long totalBytes) {
                        // do anything with progress

                    }
                })
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        AppUtils.dismissProgressDialog();
                        Log.e("ىىىىىىىىىى",response+"");

                        try {
                            if (new JSONObject(String.valueOf(response)).getString("code").equals("200")) {
                                AppUtils.showSuccessToast(getActivity(),getString(R.string.your_ad_update_done));
                                startActivity(new Intent(getActivity(), HomeActivity.class));
                            }else {
                                AppUtils.showErrorToast(getActivity(),getString(R.string.your_ad_not_update));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                    @Override
                    public void onError(ANError error) {
                        // handle error
                        AppUtils.dismissProgressDialog();

                        Log.e("ffffffffff",error.getMessage()+"");

                        AppUtils.showErrorToast(getActivity(),getString(R.string.your_ad_not_update));
                        AppUtils.dismissProgressDialog();
                    }
                });

    }

    private void showCanNotLoadDataDialog() {
        DialogUtils.showTwoActionButtonsDialog(getActivity(), R.string.can_not_load_data,
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

    private void deleteAdImage(final String imageUrl) {

        if (!AppUtils.isInternetAvailable(getActivity())) {
            AppUtils.showNoInternetDialog(getActivity());
            return;
        }

        AppUtils.showProgressDialog(getActivity());
        AndroidNetworking.post("http://athelapps.com/phone/api/item/delete_photo")
                .addBodyParameter("api_key", "BUNgDZlDqosZXWJtjYUgdx5aWbqKC2gr7wRxzVFh8Pg")
                .addBodyParameter("url", imageUrl)
                .addBodyParameter("item_id",editableAdsId)
                .addBodyParameter("token", CacheUtils.getUserToken(getActivity(),"user"))
                .setPriority(Priority.HIGH)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        AppUtils.dismissProgressDialog();

                        try {
                            if (new JSONObject(response).getString("code").toString().equals("200")){
                                AppUtils.showInfoToast(getActivity(),getString(R.string.image_delete_done));
                                indecator=indecator-1;

                            } else {
                                AppUtils.showInfoToast(getActivity(),getString(R.string.image_not_deleted));
                            }
                        } catch (JSONException e) {
                            AppUtils.showErrorToast(getActivity(),getString(R.string.image_not_deleted));
                        }
                    }
                    @Override
                    public void onError(ANError anError) {
                        AppUtils.dismissProgressDialog();
                        AppUtils.showErrorToast(getActivity(),getString(R.string.image_not_deleted));
                    }
                });


    }

}
