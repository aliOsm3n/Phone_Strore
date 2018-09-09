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
import com.phonedeals.ascom.phonestrore.interfaces.IMyAllOrderHandler;
import com.phonedeals.ascom.phonestrore.R;
import com.phonedeals.ascom.phonestrore.adapters.MyOrdersAdapter;
import com.phonedeals.ascom.phonestrore.app.phoneStore;
import com.phonedeals.ascom.phonestrore.data.model.MyOrderPrice;
import com.phonedeals.ascom.phonestrore.data.prefs.CacheUtils;
import com.phonedeals.ascom.phonestrore.ui.dialog.AppDialog;
import com.phonedeals.ascom.phonestrore.util.AppUtils;
import com.phonedeals.ascom.phonestrore.util.DialogUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MyAllOrdersActivity extends AppCompatActivity implements IMyAllOrderHandler {

    private List<MyOrderPrice> myOrderPriceList = new ArrayList<>();
    private MyOrdersAdapter mAdapter;
    private RecyclerView recyclerView;
    private Toolbar toolbar;
    private TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_prices);
        initViews();
    }

    private void initViews() {
        title=findViewById(R.id.toolbar_title);
        AppUtils.applyMediumFont(title);
        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(R.drawable.ic_back);
        recyclerView = findViewById(R.id.recycler_view);

        mAdapter = new MyOrdersAdapter(MyAllOrdersActivity.this,myOrderPriceList);

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
        AndroidNetworking.post("http://athelapps.com/phone/api/my-orders")
                .addBodyParameter("api_key", "BUNgDZlDqosZXWJtjYUgdx5aWbqKC2gr7wRxzVFh8Pg")
                .addBodyParameter("token", CacheUtils.getUserToken(MyAllOrdersActivity.this,"user"))
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
                                        AppUtils.showInfoToast(MyAllOrdersActivity.this,getString(R.string.order_offer_not_found));
                                    }
                                    for (int i=0;i<jsonArray.length();i++){
                                        JSONObject object=jsonArray.getJSONObject(i);

                                        MyOrderPrice myOrderPrice=new MyOrderPrice(object.getString("id"),object.getString("title"),new JSONObject(object.getString("city")).getString("name"),object.getString("status"),object.getString("offers_count"));

                                    myOrderPriceList.add(myOrderPrice);

                                }

                                    mAdapter.notifyDataSetChanged();
                                }else {
                                    AppUtils.showInfoToast(MyAllOrdersActivity.this,getString(R.string.order_offer_not_found));
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

    @Override
    public void onClick(String id) {
        Intent intent=new Intent(MyAllOrdersActivity.this,ShowPriceActivity.class);
        intent.putExtra("id",id);
        startActivity(intent);
    }

    @Override
    public void delete(String id) {
        AppUtils.showProgressDialog(this);
        AndroidNetworking.post("http://athelapps.com/phone/api/order/delete")
                .addBodyParameter("api_key", "BUNgDZlDqosZXWJtjYUgdx5aWbqKC2gr7wRxzVFh8Pg")
                .addBodyParameter("token", CacheUtils.getUserToken(MyAllOrdersActivity.this,"user"))
                .addBodyParameter("order_id",id)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        AppUtils.dismissProgressDialog();

                            try {
                                if (new JSONObject(response).getString("code").toString().equals("200")){
                                    AppUtils.showSuccessToast(MyAllOrdersActivity.this,getString(R.string.your_request_deleted));
                                    myOrderPriceList.clear();
                                    mAdapter.notifyDataSetChanged();
                                    loadData();
                                } else {
                                    AppUtils.showInfoToast(MyAllOrdersActivity.this,getString(R.string.your_request_not_deleted));
                                }

                            } catch (JSONException e) {
                                AppUtils.showInfoToast(MyAllOrdersActivity.this,getString(R.string.your_request_not_deleted));
                            }

                    }
                    @Override
                    public void onError(ANError anError) {
                        AppUtils.dismissProgressDialog();
                        AppUtils.showErrorToast(MyAllOrdersActivity.this,getString(R.string.your_request_not_deleted));
                    }
                });
    }

}
