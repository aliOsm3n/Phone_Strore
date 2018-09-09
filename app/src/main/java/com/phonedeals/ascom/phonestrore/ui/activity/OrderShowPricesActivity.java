package com.phonedeals.ascom.phonestrore.ui.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.phonedeals.ascom.phonestrore.interfaces.IMyOrderClickHandler;
import com.phonedeals.ascom.phonestrore.R;
import com.phonedeals.ascom.phonestrore.adapters.OrderPriceAdapter;
import com.phonedeals.ascom.phonestrore.data.model.OrderPrice;
import com.phonedeals.ascom.phonestrore.data.prefs.CacheUtils;
import com.phonedeals.ascom.phonestrore.ui.dialog.AppDialog;
import com.phonedeals.ascom.phonestrore.util.AppUtils;
import com.phonedeals.ascom.phonestrore.util.DialogUtils;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.phonedeals.ascom.phonestrore.util.ValidateUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

public class OrderShowPricesActivity extends AppCompatActivity implements IMyOrderClickHandler {

    private List<OrderPrice> orderPriceList = new ArrayList<>();
    private OrderPriceAdapter mAdapter;
    private RecyclerView recyclerView;
    private Toolbar toolbar;
    private TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_show_prices);
        initViews();

    }

    private void initViews() {

        toolbar = findViewById(R.id.toolbar);
        title = findViewById(R.id.toolbar_title);
        AppUtils.applyMediumFont(title);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(R.drawable.ic_back);
        recyclerView = findViewById(R.id.recycler_view);

        mAdapter = new OrderPriceAdapter(this, orderPriceList);


        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);


        if (!AppUtils.isInternetAvailable(this)) {
            showCanNotLoadDataDialog();
        } else {
            loadData();
        }
    }

    public void loadData() {

        AppUtils.showProgressDialog(this);
        AndroidNetworking.post("http://athelapps.com/phone/api/order/all")
                .addBodyParameter("api_key", "BUNgDZlDqosZXWJtjYUgdx5aWbqKC2gr7wRxzVFh8Pg")
                .addBodyParameter("token", CacheUtils.getUserToken(OrderShowPricesActivity.this, "user"))
                .setPriority(Priority.HIGH)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        AppUtils.dismissProgressDialog();

                        try {
                            if (new JSONObject(response).getString("code").toString().equals("200")) {

                                JSONObject jsonObject = new JSONObject(response);
                                JSONArray jsonArray = jsonObject.getJSONArray("data");

                                if (jsonArray.length() == 0) {
                                    AppUtils.showInfoToast(OrderShowPricesActivity.this, getString(R.string.no_offers));
                                }
                                if (jsonArray.length() != 0) {
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject object = jsonArray.getJSONObject(i);
                                        OrderPrice orderPrice = new OrderPrice(object.getString("id"), object.getString("title"), object.getJSONObject("city").getString("name"), object.getString("status"), object.getString("user_id"));

                                        orderPriceList.add(orderPrice);
                                        mAdapter.notifyDataSetChanged();
                                    }
                                }
                            } else {
                                AppUtils.showInfoToast(OrderShowPricesActivity.this, getString(R.string.no_offers));
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
    public void onClick(String id, String userId) {
        if (CacheUtils.getUserId(OrderShowPricesActivity.this, "user").equals(userId)) {
            AppUtils.showInfoToast(OrderShowPricesActivity.this, getString(R.string.you_can_not_make_offer_on_your_request));
        } else {
            takePrice(id);
        }
    }

    private void takePrice(String id) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.custom_dialog, null);
        dialogBuilder.setView(dialogView);

        TextView textView=dialogView.findViewById(R.id.title);
        final EditText edt = dialogView.findViewById(R.id.edit1);
        Button button=dialogView.findViewById(R.id.btn_take_price);
        AppUtils.applyMediumFont(textView);
        AppUtils.applyBoldFont(button);
        AlertDialog b = dialogBuilder.create();
        b.show();
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                b.hide();
                addOffer(id,edt.getText()+"");
            }
        });

    }

    private void addOffer(String id,String price) {
        if (!AppUtils.isInternetAvailable(this)) {
            AppUtils.showNoInternetDialog(this);
            return;
        }

        if (ValidateUtils.missingInputs(price)) {
            AppUtils.showInfoDialog(this,R.string.error_dialog_missing_inputs);
            return;
        }

        if (!ValidateUtils.validPrice(price)) {
            AppUtils.showInfoDialog(this,R.string.invalide_price);
            return;
        }

        AppUtils.showProgressDialog(this);
        AndroidNetworking.post("http://athelapps.com/phone/api/offer/create")
                .addBodyParameter("api_key", "BUNgDZlDqosZXWJtjYUgdx5aWbqKC2gr7wRxzVFh8Pg")
                .addBodyParameter("price", price)
                .addBodyParameter("order_id", id)
                .addBodyParameter("token", CacheUtils.getUserToken(this,"user"))
                .setPriority(Priority.HIGH)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        AppUtils.dismissProgressDialog();

                        try {
                            if (new JSONObject(response).getString("code").toString().equals("200")){
                                Intent intent=new Intent(OrderShowPricesActivity.this,CongratulationActivity.class);
                                intent.putExtra("tag","tag");
                                startActivity(intent);
                            } else {
                                AppUtils.showInfoToast(OrderShowPricesActivity.this,getString(R.string.your_offer_not_send));
                            }
                        } catch (JSONException e) {
                            AppUtils.showInfoToast(OrderShowPricesActivity.this,getString(R.string.your_offer_not_send));
                        }
                    }
                    @Override
                    public void onError(ANError anError) {
                        AppUtils.dismissProgressDialog();
                        AppUtils.showErrorToast(OrderShowPricesActivity.this,getString(R.string.your_offer_not_send));
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

}
