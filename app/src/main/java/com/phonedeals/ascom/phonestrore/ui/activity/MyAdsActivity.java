package com.phonedeals.ascom.phonestrore.ui.activity;

import android.app.Dialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.phonedeals.ascom.phonestrore.interfaces.IMyAdsClickHandler;
import com.phonedeals.ascom.phonestrore.R;
import com.phonedeals.ascom.phonestrore.adapters.MyAdsAdapter;
import com.phonedeals.ascom.phonestrore.data.model.MyAds;
import com.phonedeals.ascom.phonestrore.data.prefs.CacheUtils;
import com.phonedeals.ascom.phonestrore.ui.dialog.AppDialog;
import com.phonedeals.ascom.phonestrore.util.AppUtils;
import com.phonedeals.ascom.phonestrore.util.DialogUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MyAdsActivity extends AppCompatActivity implements IMyAdsClickHandler {

    private List<MyAds> myAdsList = new ArrayList<>();
    private MyAdsAdapter mAdapter;
    private RecyclerView recyclerView;
    private Toolbar toolbar;
    private TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_ads);
        initViews();
    }

    private void initViews() {

        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(R.drawable.ic_back);
        title=findViewById(R.id.toolbar_title);
        AppUtils.applyMediumFont(title);

        recyclerView = findViewById(R.id.recycler_view);
        mAdapter = new MyAdsAdapter(MyAdsActivity.this,myAdsList);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);


        if (!AppUtils.isInternetAvailable(this)) {
            showCanNotLoadDataDialog();
        }else {
            loadData();
        }
    }

    public void loadData(){

        AppUtils.showProgressDialog(this);
        AndroidNetworking.post("http://athelapps.com/phone/api/my-items")
                .addBodyParameter("api_key", "BUNgDZlDqosZXWJtjYUgdx5aWbqKC2gr7wRxzVFh8Pg")
                .addBodyParameter("token", CacheUtils.getUserToken(MyAdsActivity.this,"user"))
                .setPriority(Priority.HIGH)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        AppUtils.dismissProgressDialog();

                            try {
                                if (new JSONObject(response).getString("code").toString().equals("200")){
                                    JSONObject jsonObject=new JSONObject(response);
                                    JSONArray jsonArray=jsonObject.getJSONArray("data");


                                    if (jsonArray.length()==0){
                                        AppUtils.showInfoToast(MyAdsActivity.this,getString(R.string.no_ads));
                                    }
                                    if (jsonArray.length()!=0) {
                                        for (int i = 0; i < jsonArray.length(); i++) {
                                            JSONObject object = jsonArray.getJSONObject(i);
                                            MyAds myAds = new MyAds(object.getString("id"), object.getString("title"), object.getJSONObject("city").getString("name"), object.getString("status"), object.getString("num_views"), object.getString("photo"),object.getString("visible"));
                                            myAdsList.add(myAds);
                                        }
                                        mAdapter.notifyDataSetChanged();
                                    } else {
                                        AppUtils.showInfoToast(MyAdsActivity.this,getString(R.string.no_ads));
                                    }
                                } else {
                                    AppUtils.showInfoToast(MyAdsActivity.this,getString(R.string.no_ads));
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

    @Override
    public void edit(String id) {
        Intent intent=new Intent(MyAdsActivity.this,HomeActivity.class);
        intent.putExtra("id",id);
        startActivity(intent);
    }

    @Override
    public void messages(String id) {
        Intent intent=new Intent(MyAdsActivity.this,ChatActivity.class);
        intent.putExtra("id",id);
        startActivity(intent);
    }

    @Override
    public void stop(String id,String visible) {
        if (visible.equals("1")){
            stopAd(id);
        }else {
            startAd(id);
        }
    }

    @Override
    public void delete(String id) {
        AppUtils.showProgressDialog(this);
        AndroidNetworking.post("http://athelapps.com/phone/api/item/delete")
                .addBodyParameter("api_key", "BUNgDZlDqosZXWJtjYUgdx5aWbqKC2gr7wRxzVFh8Pg")
                .addBodyParameter("item_id", id)
                .addBodyParameter("token", CacheUtils.getUserToken(MyAdsActivity.this,"user"))
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        AppUtils.dismissProgressDialog();

                            try {
                                if (new JSONObject(response).getString("code").toString().equals("200")){
                                    AppUtils.showInfoToast(MyAdsActivity.this,getString(R.string.your_ad_deleted));
                                    myAdsList.clear();
                                    mAdapter.notifyDataSetChanged();
                                    loadData();
                                } else {
                                    AppUtils.showInfoToast(MyAdsActivity.this,getString(R.string.your_ad_not_deleted));
                                }
                            } catch (JSONException e) {
                                AppUtils.showInfoToast(MyAdsActivity.this,getString(R.string.your_ad_not_deleted));
                            }
                    }
                    @Override
                    public void onError(ANError anError) {
                        AppUtils.dismissProgressDialog();
                        AppUtils.showInfoToast(MyAdsActivity.this,getString(R.string.your_ad_not_deleted));
                    }
                });
    }

    private void showCanNotLoadDataDialog() {
        DialogUtils.showTwoActionButtonsDialog(this, R.string.dialog_error_no_internet,
                R.string.dialog_ok, new AppDialog.Action1ButtonListener() {
                    @Override
                    public void onAction1ButtonClick(Dialog dialog) {
                        dialog.dismiss();
                        loadData();
                    }
                }, R.string.dialog_cancel, new AppDialog.Action2ButtonListener() {
                    @Override
                    public void onAction2ButtonClick(Dialog dialog) {
                        finish();
                    }
                }, false);
    }

    private void stopAd(String id){
        AppUtils.showProgressDialog(this);
        AndroidNetworking.post("http://athelapps.com/phone/api/item/unpublish")
                .addBodyParameter("api_key", "BUNgDZlDqosZXWJtjYUgdx5aWbqKC2gr7wRxzVFh8Pg")
                .addBodyParameter("item_id", id)
                .addBodyParameter("token", CacheUtils.getUserToken(MyAdsActivity.this,"user"))
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        AppUtils.dismissProgressDialog();
                            try {
                                if (new JSONObject(response).getString("code").toString().equals("200")){
                                    AppUtils.showInfoToast(MyAdsActivity.this,getString(R.string.your_ad_stop));
                                    myAdsList.clear();
                                    mAdapter.notifyDataSetChanged();
                                    loadData();
                                } else {
                                    AppUtils.showInfoToast(MyAdsActivity.this,getString(R.string.your_ad_not_stop));
                                }
                            } catch (JSONException e) {
                                AppUtils.showInfoToast(MyAdsActivity.this,getString(R.string.your_ad_not_stop));
                            }
                    }
                    @Override
                    public void onError(ANError anError) {
                        AppUtils.dismissProgressDialog();
                        AppUtils.showErrorToast(MyAdsActivity.this,getString(R.string.your_ad_not_stop));
                    }
                });
    }

    private void startAd(String id){
        AppUtils.showProgressDialog(this);
        AndroidNetworking.post("http://athelapps.com/phone/api/item/publish")
                .addBodyParameter("api_key", "BUNgDZlDqosZXWJtjYUgdx5aWbqKC2gr7wRxzVFh8Pg")
                .addBodyParameter("item_id", id)
                .addBodyParameter("token", CacheUtils.getUserToken(MyAdsActivity.this,"user"))
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        AppUtils.dismissProgressDialog();

                            try {
                                if (new JSONObject(response).getString("code").toString().equals("200")){
                                    AppUtils.showInfoToast(MyAdsActivity.this,getString(R.string.your_ad_on));
                                    myAdsList.clear();
                                    mAdapter.notifyDataSetChanged();
                                    loadData();
                                } else {
                                    AppUtils.showInfoToast(MyAdsActivity.this,getString(R.string.your_ad_not_work));
                                }
                            } catch (JSONException e) {
                                AppUtils.showInfoToast(MyAdsActivity.this,getString(R.string.your_ad_not_work));
                            }
                    }
                    @Override
                    public void onError(ANError anError) {
                        AppUtils.dismissProgressDialog();
                        AppUtils.showErrorToast(MyAdsActivity.this,getString(R.string.your_ad_not_work));

                    }
                });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent=new Intent(MyAdsActivity.this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

}
