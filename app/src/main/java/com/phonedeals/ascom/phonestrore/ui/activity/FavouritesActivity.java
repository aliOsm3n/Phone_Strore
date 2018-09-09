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
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.phonedeals.ascom.phonestrore.R;
import com.phonedeals.ascom.phonestrore.adapters.CategoryAdapter;
import com.phonedeals.ascom.phonestrore.adapters.FavouriteAdapter;
import com.phonedeals.ascom.phonestrore.data.model.Category;
import com.phonedeals.ascom.phonestrore.data.prefs.CacheUtils;
import com.phonedeals.ascom.phonestrore.interfaces.IMyOrderClickHandler;
import com.phonedeals.ascom.phonestrore.ui.dialog.AppDialog;
import com.phonedeals.ascom.phonestrore.util.AppUtils;
import com.phonedeals.ascom.phonestrore.util.DialogUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FavouritesActivity extends AppCompatActivity  implements IMyOrderClickHandler {

    private List<Category> orderPrices = new ArrayList<>();
    private FavouriteAdapter mAdapter;
    private RecyclerView recyclerView;
    private Toolbar toolbar;
    private TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourites);
        initViews();
    }

    private void initViews() {
        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(R.drawable.ic_back);
        mAdapter = new FavouriteAdapter(FavouritesActivity.this,orderPrices);
        title=findViewById(R.id.toolbar_title);

        AppUtils.applyMediumFont(title);
        recyclerView=findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        if (!AppUtils.isInternetAvailable(this)) {
            showCanNotLoadDataDialog();
        }else {
            loadData();
        }


    }

    private void loadData() {
        AppUtils.showProgressDialog(this);
        AndroidNetworking.post("http://athelapps.com/phone/api/my-favorites")
                .addBodyParameter("api_key", "BUNgDZlDqosZXWJtjYUgdx5aWbqKC2gr7wRxzVFh8Pg")
                .addBodyParameter("token", CacheUtils.getUserToken(this,"user"))
                .setPriority(Priority.HIGH)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        AppUtils.dismissProgressDialog();
                        Log.e("aaa",response);
                        try {

                            if (new JSONObject(response).getString("code").toString().equals("200")) {

                                JSONObject jsonObject = new JSONObject(response);
                                JSONArray jsonArray = jsonObject.getJSONArray("data");

                                if (jsonArray.length() != 0) {
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject object = jsonArray.getJSONObject(i);

                                        Category myOrderPrice = new Category(object.getJSONObject("item").getString("id"), object.getJSONObject("item").getString("title"), object.getJSONObject("item").getJSONObject("city").getString("name"), object.getJSONObject("item").getString("status"), object.getJSONObject("item").getString("num_views"), object.getJSONObject("item").getString("photo"), object.getJSONObject("item").getString("price"));

                                        orderPrices.add(myOrderPrice);

                                    }
                                    mAdapter.notifyDataSetChanged();
                                } else {
                                    AppUtils.showInfoToast(FavouritesActivity.this, getString(R.string.favourite_not_found));
                                }
                            }
                        } catch (Exception e) {
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
    public void onClick(String id,String userId) {
        Intent intent=new Intent(FavouritesActivity.this,PhoneProfileActivity.class);
        intent.putExtra("id",id);
        startActivity(intent);
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

}