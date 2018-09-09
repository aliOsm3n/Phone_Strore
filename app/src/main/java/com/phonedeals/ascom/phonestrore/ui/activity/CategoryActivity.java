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

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.phonedeals.ascom.phonestrore.interfaces.IMyOrderClickHandler;
import com.phonedeals.ascom.phonestrore.R;
import com.phonedeals.ascom.phonestrore.adapters.CategoryAdapter;
import com.phonedeals.ascom.phonestrore.data.model.Category;
import com.phonedeals.ascom.phonestrore.ui.dialog.AppDialog;
import com.phonedeals.ascom.phonestrore.util.AppUtils;
import com.phonedeals.ascom.phonestrore.util.DialogUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CategoryActivity extends AppCompatActivity implements IMyOrderClickHandler {

    private List<Category> orderPrices = new ArrayList<>();
    private CategoryAdapter mAdapter;
    private RecyclerView recyclerView;
    private String category;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show);
        initViews();

    }

    private void initViews() {
        recyclerView = findViewById(R.id.recycler_view);
        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(R.drawable.ic_back);
        mAdapter = new CategoryAdapter(CategoryActivity.this,orderPrices);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);


        String json=getIntent().getStringExtra("json");
        category=getIntent().getStringExtra("category");
        if (json!=null){
            parseData(json);
        }

        if (category!=null){
            loadData(category);
        }
    }

    private void parseData(String json) {
        try {

            JSONObject jsonObject=new JSONObject(json);
            JSONArray jsonArray=jsonObject.getJSONArray("data");

            if (jsonArray.length()!=0) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject object = jsonArray.getJSONObject(i);

                    Category myOrderPrice = new Category(object.getString("id"), object.getString("title"), object.getJSONObject("city").getString("name"), object.getString("status"), object.getString("created_at"), object.getString("photo"), object.getString("price"));

                    orderPrices.add(myOrderPrice);
                }
                mAdapter.notifyDataSetChanged();
            } else {
                AppUtils.showInfoToast(CategoryActivity.this,getString(R.string.did_not_find_your_request));
            }
        } catch (Exception e) {
            AppUtils.showInfoToast(CategoryActivity.this,getString(R.string.did_not_find_your_request));
        }
    }

    private void loadData(String category) {
        AppUtils.showProgressDialog(this);
        AndroidNetworking.post("http://athelapps.com/phone/api/items/category")
                .addBodyParameter("api_key", "BUNgDZlDqosZXWJtjYUgdx5aWbqKC2gr7wRxzVFh8Pg")
                .addBodyParameter("category_id",category)
                .setPriority(Priority.HIGH)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        AppUtils.dismissProgressDialog();
                        try {
                            JSONObject jsonObject=new JSONObject(response);
                            JSONArray jsonArray=jsonObject.getJSONArray("data");

                            if (jsonArray.length()!=0) {
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject object = jsonArray.getJSONObject(i);

                                    Category myOrderPrice = new Category(object.getString("id"), object.getString("title"), object.getJSONObject("city").getString("name"), object.getString("status"), object.getString("created_at"), object.getString("photo"), object.getString("price"));

                                    orderPrices.add(myOrderPrice);

                                }
                                mAdapter.notifyDataSetChanged();
                            } else {
                                AppUtils.showInfoToast(CategoryActivity.this,getString(R.string.did_not_find_your_request));
                            }
                        } catch (Exception e) {
                            AppUtils.showErrorToast(CategoryActivity.this,getString(R.string.did_not_find_your_request));
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
        Intent intent=new Intent(CategoryActivity.this,PhoneProfileActivity.class);
        intent.putExtra("id",id);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==R.id.filter){
            startActivity(new Intent(this,FilterActivity.class));
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    private void showCanNotLoadDataDialog() {
        DialogUtils.showTwoActionButtonsDialog(this, R.string.dialog_error_no_internet,
                R.string.dialog_ok, new AppDialog.Action1ButtonListener() {
                    @Override
                    public void onAction1ButtonClick(Dialog dialog) {
                        dialog.dismiss();
                        loadData(category);
                    }
                }, R.string.dialog_cancel, new AppDialog.Action2ButtonListener() {
                    @Override
                    public void onAction2ButtonClick(Dialog dialog) {
                        finish();
                    }
                }, false);
    }

}
