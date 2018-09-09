package com.phonedeals.ascom.phonestrore.ui.activity;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ShareCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.phonedeals.ascom.phonestrore.R;
import com.phonedeals.ascom.phonestrore.data.prefs.CacheUtils;
import com.phonedeals.ascom.phonestrore.interfaces.OnImageClickListener;
import com.phonedeals.ascom.phonestrore.ui.dialog.AppDialog;
import com.phonedeals.ascom.phonestrore.ui.fragment.PreviewFragment;
import com.phonedeals.ascom.phonestrore.ui.fragment.SlideshowDialogFragment;
import com.phonedeals.ascom.phonestrore.util.AppUtils;
import com.phonedeals.ascom.phonestrore.util.DialogUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;



public class PhoneProfileActivity extends AppCompatActivity implements OnImageClickListener {

    private TextView phoneName,phoneColor,phoneType,phoneDesc,type,phoneTypeDetails,color;
    private TextView cityName,views,city,phoneStatus,status,phonePrice,price,storageSize,storage,phoneModel,model;
    private Button sendMessage,call;
    private ImageView share,fav;
    MyPagerAdapter adapterViewPager;
    private TextView[] dots;
    private LinearLayout dotsLayout;
    private static ArrayList<String> imageList;
    private String adId,userId,user,userPhone,share_link;
    public String mobile_id="";
    private Toolbar toolbar;
    private boolean isFavourite=false;
    private ViewPager vpPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_profile);
        initView();
    }

    private void initView() {
        mobile_id=getIntent().getStringExtra("id");
        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(R.drawable.ic_back);
        user= CacheUtils.getUserName(this,"user");

        dotsLayout= findViewById(R.id.dots);

        vpPager = findViewById(R.id.vpPager);

        share=findViewById(R.id.share);
        fav=findViewById(R.id.add_fav);

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isUserRegistered()){
                    share();
                }else {
                    showUserNotRegisteredDialog();
                }
            }
        });

        fav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isUserRegistered()){
                    if (isFavourite){
                        removeItemFromFavourite(mobile_id);
                    }else {
                        addFavourite(mobile_id);
                    }
                }else {
                    showUserNotRegisteredDialog();
                }
            }
        });
        phoneName=findViewById(R.id.phone_name);
        phoneType=findViewById(R.id.phone_type);
        phoneDesc=findViewById(R.id.phone_desc);
        views=findViewById(R.id.number_views);

        type=findViewById(R.id.type);
        phoneTypeDetails=findViewById(R.id.phone_type_details);
        color=findViewById(R.id.color);

        phoneColor=findViewById(R.id.phone_color);
        model=findViewById(R.id.model);
        phoneModel=findViewById(R.id.phone_model);

        storage=findViewById(R.id.storage);
        storageSize=findViewById(R.id.storage_size);
        price=findViewById(R.id.price);

        phonePrice=findViewById(R.id.phone_price);
        status=findViewById(R.id.status);
        phoneStatus=findViewById(R.id.phone_status);

        city=findViewById(R.id.city);
        cityName=findViewById(R.id.city_name);


        sendMessage=findViewById(R.id.send_message_btn);
        sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isUserRegistered()){
                    if (userId.equalsIgnoreCase(CacheUtils.getUserId(PhoneProfileActivity.this,"user"))){

                    }else {
                        sendMessage();
                    }
                }else {
                    showUserNotRegisteredDialog();
                }
            }
        });
        call=findViewById(R.id.call_btn);
        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isUserRegistered()){
                    if (userId.equalsIgnoreCase(CacheUtils.getUserId(PhoneProfileActivity.this,"user"))){

                    }else {
                        call();
                    }
                }else {
                    showUserNotRegisteredDialog();
                }
            }
        });


        AppUtils.applyMediumFont(phoneName,phoneDesc,sendMessage,call,phoneTypeDetails,phoneColor,phoneStatus,cityName);
        AppUtils.applyBoldFont(type,color,model,storage,price,status,city);

        if (mobile_id!=null && !mobile_id.equals("")){
            loadPhoneDetails(mobile_id);
        }else {
            showErrorDialog();
        }

    }

    public void sendMessage() {
        Intent intent=new Intent(PhoneProfileActivity.this, ChatRoomActivity.class);
        intent.putExtra("itemId",adId);
        startActivity(intent);
    }

    public void call() {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + userPhone));
        try {
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addBottomDots(int position) {

        dots = new TextView[imageList.size()];
        int colorActive = getResources().getColor(R.color.nav_background_color);
        int colorInactive = getResources().getColor(R.color.colorPrimaryDark);
        dotsLayout.removeAllViews();
        for(int i=0; i<dots.length; i++)
        {
            dots[i]=new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(35);
            dots[i].setTextColor(colorInactive);
            dotsLayout.addView(dots[i]);
        }
        if(dots.length>0)
            dots[position].setTextColor(colorActive);
    }

    public void share() {
        ShareCompat.IntentBuilder.from(this)
                .setType("text/plain")
                .setChooserTitle(R.string.share_via)
                .setText(share_link)
                .startChooser();
    }

    public void addFavourite(String id) {

       if (id!=null){
           AppUtils.showProgressDialog(this);
           AndroidNetworking.post("http://athelapps.com/phone/api/favorites/add")
                   .addBodyParameter("api_key", "BUNgDZlDqosZXWJtjYUgdx5aWbqKC2gr7wRxzVFh8Pg")
                   .addBodyParameter("token",CacheUtils.getUserToken(PhoneProfileActivity.this,"user"))
                   .addBodyParameter("item_id",id)
                   .setPriority(Priority.MEDIUM)
                   .build()
                   .getAsString(new StringRequestListener() {
                       @Override
                       public void onResponse(String response) {
                           AppUtils.dismissProgressDialog();
                           try {
                               if (new JSONObject(response).getString("code").toString().equalsIgnoreCase("201")) {
                                   AppUtils.showSuccessToast(PhoneProfileActivity.this,getString(R.string.phone_add_to_favourite));
                                   isFavourite=true;
                                   fav.setImageResource(R.drawable.favorite_red);
                               }else {
                                   AppUtils.showErrorToast(PhoneProfileActivity.this,getString(R.string.phone_not_add_to_favourite));
                               }
                           } catch (JSONException e) {
                               AppUtils.showErrorToast(PhoneProfileActivity.this,getString(R.string.phone_not_add_to_favourite));
                           }
                       }
                       @Override
                       public void onError(ANError anError) {
                           AppUtils.dismissProgressDialog();
                           AppUtils.showErrorToast(PhoneProfileActivity.this,getString(R.string.phone_not_add_to_favourite));
                       }
                   });
       } else {
           AppUtils.showErrorToast(PhoneProfileActivity.this,getString(R.string.phone_not_add_to_favourite));
       }
    }

    @Override
    public void onClick() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        SlideshowDialogFragment newFragment = SlideshowDialogFragment.newInstance(imageList);
        newFragment.show(ft, "slideshow");
    }

    public static class MyPagerAdapter extends FragmentPagerAdapter {

        public MyPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        // Returns total number of pages
        @Override
        public int getCount() {
            if (imageList==null){
                return 0;
            }
            return imageList.size();
        }

        // Returns the fragment to display for that page
        @Override
        public Fragment getItem(int position) {
            return PreviewFragment.newInstance(imageList.get(position));
        }

        // Returns the page title for the top indicator
        @Override
        public CharSequence getPageTitle(int position) {
            return "Page " + position;
        }

    }

    private void loadPhoneDetails(String id){
        AppUtils.showProgressDialog(this);
        AndroidNetworking.post("http://athelapps.com/phone/api/items")
                .addBodyParameter("api_key", "BUNgDZlDqosZXWJtjYUgdx5aWbqKC2gr7wRxzVFh8Pg")
                .addBodyParameter("item_id",id)
                .setPriority(Priority.HIGH)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("eeeeeeeeee",response);
                        try {
                            if (new JSONObject(response).getString("code").toString().equalsIgnoreCase("200")) {
                                parseData(response);
                            }else {
                                AppUtils.dismissProgressDialog();
                                showCanNotLoadDataDialog();
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
                        loadPhoneDetails(mobile_id);
                    }
                }, R.string.dialog_cancel, new AppDialog.Action2ButtonListener() {
                    @Override
                    public void onAction2ButtonClick(Dialog dialog) {
                        finish();
                    }
                }, false);
    }

    private void showErrorDialog() {
        DialogUtils.showTwoActionButtonsDialog(this, R.string.error_dialog,
                R.string.dialog_ok, new AppDialog.Action1ButtonListener() {
                    @Override
                    public void onAction1ButtonClick(Dialog dialog) {
                        dialog.dismiss();
                        startActivity(new Intent(PhoneProfileActivity.this,HomeActivity.class));
                        finish();
                    }
                }, R.string.dialog_cancel, new AppDialog.Action2ButtonListener() {
                    @Override
                    public void onAction2ButtonClick(Dialog dialog) {
                        finish();
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
            imageList= new ArrayList<>();
            for (int i=0;i<array.length();i++){
                imageList.add(array.getJSONObject(i).getString("photo"));
            }

            adId=object.getString("id");
            userId=object.getString("user_id");
            phoneName.setText(object.getString("title"));
            phoneType.setText(object.getString("category_name"));
            phoneDesc.setText(object.getString("detail"));
            phoneTypeDetails.setText(object.getString("category_name"));
            phoneModel.setText(object.getString("type_name"));
            phonePrice.setText(object.getString("price"));
            phoneStatus.setText(object.getString("status"));
            phoneColor.setText(object.getString("color"));
            cityName.setText(object.getString("city_name"));
            storageSize.setText(object.getString("memory_name"));
            views.setText(object.getString("num_views"));
            userPhone=object.getJSONObject("user").getString("phone");
            share_link=object.getString("share_url");
            if (isUserRegistered()){
                checkIfFavourite(mobile_id);
            }else {
                AppUtils.dismissProgressDialog();
            }
        } catch (JSONException e) {
            showErrorDialog();
        }

        addBottomDots(0);




        adapterViewPager = new MyPagerAdapter(getSupportFragmentManager());
        vpPager.setAdapter(adapterViewPager);

        vpPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                addBottomDots(position);
            }

            @Override
            public void onPageSelected(int position) {
                addBottomDots(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void checkIfFavourite(String id){
       if (id!=null){
           AndroidNetworking.post("http://athelapps.com/phone/api/favorites/check")
                   .addBodyParameter("api_key", "BUNgDZlDqosZXWJtjYUgdx5aWbqKC2gr7wRxzVFh8Pg")
                   .addBodyParameter("token",CacheUtils.getUserToken(PhoneProfileActivity.this,"user"))
                   .addBodyParameter("item_id",id)
                   .setPriority(Priority.MEDIUM)
                   .build()
                   .getAsString(new StringRequestListener() {
                       @Override
                       public void onResponse(String response) {
                           AppUtils.dismissProgressDialog();
                           try {
                               if (new JSONObject(response).getString("code").toString().equalsIgnoreCase("200")) {
                                   parseResult(response);
                               }
                           } catch (JSONException e) {
                           }
                       }
                       @Override
                       public void onError(ANError anError) {
                           AppUtils.dismissProgressDialog();
                       }
                   });
       }
    }

    private void parseResult(String response) {
        try {
            if (new JSONObject(response).getString("data").equalsIgnoreCase("false")){
                isFavourite=false;
            }else {
                isFavourite=true;
                fav.setImageResource(R.drawable.favorite_red);
            }
        } catch (JSONException e) {
            AppUtils.showErrorToast(PhoneProfileActivity.this,getString(R.string.your_request_not_done));
        }
    }

    private void removeItemFromFavourite(String id){
        if (id!=null){
            AppUtils.showProgressDialog(this);
            AndroidNetworking.post("http://athelapps.com/phone/api/favorites/remove")
                    .addBodyParameter("api_key", "BUNgDZlDqosZXWJtjYUgdx5aWbqKC2gr7wRxzVFh8Pg")
                    .addBodyParameter("token",CacheUtils.getUserToken(PhoneProfileActivity.this,"user"))
                    .addBodyParameter("item_id",id)
                    .setPriority(Priority.MEDIUM)
                    .build()
                    .getAsString(new StringRequestListener() {
                        @Override
                        public void onResponse(String response) {
                            AppUtils.dismissProgressDialog();
                            try {
                                if (new JSONObject(response).getString("code").toString().equalsIgnoreCase("200")) {
                                    AppUtils.showSuccessToast(PhoneProfileActivity.this,getString(R.string.deleted_from_favourite));
                                    isFavourite=false;
                                    fav.setImageResource(R.drawable.phone_profile_fav);
                                }else {
                                    AppUtils.showErrorToast(PhoneProfileActivity.this,getString(R.string.favourite_not_deleted));
                                }
                            } catch (JSONException e) {
                                AppUtils.showErrorToast(PhoneProfileActivity.this,getString(R.string.favourite_not_deleted));
                            }
                        }
                        @Override
                        public void onError(ANError anError) {
                            AppUtils.dismissProgressDialog();
                            AppUtils.showErrorToast(PhoneProfileActivity.this,getString(R.string.favourite_not_deleted));
                        }
                    });
        } else {
            AppUtils.showErrorToast(PhoneProfileActivity.this,getString(R.string.favourite_not_deleted));
        }
    }

    public boolean isUserRegistered() {
        if (user==null){
            return false;
        }else {
            return true;
        }
    }

    public void showUserNotRegisteredDialog() {
        DialogUtils.showUserNotRegisterDialog(this);
    }

}
