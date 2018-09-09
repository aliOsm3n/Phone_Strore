package com.phonedeals.ascom.phonestrore.ui.activity;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
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
import com.phonedeals.ascom.phonestrore.interfaces.IShowPriceClickHandler;
import com.phonedeals.ascom.phonestrore.R;
import com.phonedeals.ascom.phonestrore.adapters.ShowPricesAdapter;
import com.phonedeals.ascom.phonestrore.data.model.ShowPrice;
import com.phonedeals.ascom.phonestrore.data.prefs.CacheUtils;
import com.phonedeals.ascom.phonestrore.ui.dialog.AppDialog;
import com.phonedeals.ascom.phonestrore.util.AppUtils;
import com.phonedeals.ascom.phonestrore.util.DialogUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ShowPriceActivity extends AppCompatActivity implements IShowPriceClickHandler {

    private List<ShowPrice> showPriceList = new ArrayList<>();
    private ShowPricesAdapter mAdapter;
    private RecyclerView recyclerView;
    public String offerId;
    private Toolbar toolbar;
    private TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_price);
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

        mAdapter = new ShowPricesAdapter(this,showPriceList);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        offerId=getIntent().getStringExtra("id");
        if (!AppUtils.isInternetAvailable(this)) {
            AppUtils.showNoInternetDialog(this);
        }else {
            if (offerId!=null){
                loadData(offerId);
            }else {
                AppUtils.showInfoToast(ShowPriceActivity.this,getString(R.string.error));
            }
        }
    }

    public void loadData(String id){
        AppUtils.showProgressDialog(this);
        AndroidNetworking.post("http://athelapps.com/phone/api/order-offer")
                .addBodyParameter("api_key", "BUNgDZlDqosZXWJtjYUgdx5aWbqKC2gr7wRxzVFh8Pg")
                .addBodyParameter("token", CacheUtils.getUserToken(ShowPriceActivity.this,"user"))
                .addBodyParameter("order_id",id)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        AppUtils.dismissProgressDialog();
                        Log.e("zzzzzzzzzzz",response);
                            try {
                                if (new JSONObject(response).getString("code").toString().equals("200")) {

                                    JSONObject jsonObject = new JSONObject(response);
                                    JSONArray jsonArray = jsonObject.getJSONArray("data");

                                    if (jsonArray.length()==0){
                                        AppUtils.showInfoToast(ShowPriceActivity.this,getString(R.string.not_found_offers));
                                    }
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject object = jsonArray.getJSONObject(i);

                                        ShowPrice showPrice = new ShowPrice(object.getString("id"), object.getJSONObject("order").getString("title"),
                                                object.getJSONObject("order").getJSONObject("city").getString("name"),
                                                object.getJSONObject("order").getString("status"), object.getString("price"),
                                                object.getJSONObject("user").getJSONObject("location").getString("latitude"),
                                                object.getJSONObject("user").getJSONObject("location").getString("longtude"),
                                                object.getJSONObject("user").getString("phone"));

                                        showPriceList.add(showPrice);
                                    }

                                    mAdapter.notifyDataSetChanged();


                                } else {
                                    AppUtils.showInfoToast(ShowPriceActivity.this,getString(R.string.not_found_offers));
                                    }
                            } catch (JSONException e) {
                                AppUtils.showErrorToast(ShowPriceActivity.this,getString(R.string.error));
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
    public void call(String number) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + number));
        try {
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void location(String lat,String lon) {

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:<lat>,<long>?q=<"+lat+">,<"+lon+">(عنوان المتجر)"));
        intent.setPackage("com.google.android.apps.maps");
        if (intent.resolveActivity(getPackageManager())!=null){
            startActivity(intent);
        }
    }

    private void showCanNotLoadDataDialog() {
        DialogUtils.showTwoActionButtonsDialog(this, R.string.dialog_error_no_internet,
                R.string.dialog_ok, new AppDialog.Action1ButtonListener() {
                    @Override
                    public void onAction1ButtonClick(Dialog dialog) {
                      dialog.dismiss();
                      loadData(offerId);
                    }
                }, R.string.dialog_cancel, new AppDialog.Action2ButtonListener() {
                    @Override
                    public void onAction2ButtonClick(Dialog dialog) {
                        finish();
                    }
                }, false);
    }

}
