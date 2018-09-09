package com.phonedeals.ascom.phonestrore.ui.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.internal.NavigationMenuView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.phonedeals.ascom.phonestrore.util.CustomTypefaceSpan;
import com.phonedeals.ascom.phonestrore.interfaces.ICategoryHandler;
import com.phonedeals.ascom.phonestrore.R;
import com.phonedeals.ascom.phonestrore.data.prefs.CacheUtils;
import com.phonedeals.ascom.phonestrore.ui.dialog.AppDialog;
import com.phonedeals.ascom.phonestrore.ui.fragment.AddFragment;
import com.phonedeals.ascom.phonestrore.ui.fragment.MainFragment;
import com.phonedeals.ascom.phonestrore.ui.fragment.OrderFragment;
import com.phonedeals.ascom.phonestrore.util.AppUtils;
import com.phonedeals.ascom.phonestrore.util.DialogUtils;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,ICategoryHandler {

    private Toolbar toolbar;
    ImageView shopping,add,money;
    public String user,user_phone,user_photo;
    private DrawerLayout drawer;
    private TextView userName,userPhone,toolbar_title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (CacheUtils.getUserType(this,"user")==null){
            setContentView(R.layout.modified_layout);
        }else if (CacheUtils.getUserType(this,"user").equals("4") || CacheUtils.getUserType(this,"user").equals("5")){
            setContentView(R.layout.activity_home);
        } else {
            setContentView(R.layout.modified_layout);
        }
        initView();
    }

    private void initView() {
        toolbar_title=findViewById(R.id.toolbar_title);
        user= CacheUtils.getUserName(this,"user");
        user_photo= CacheUtils.getUserPhoto(this,"user");
        user_phone= CacheUtils.getUserPhone(this,"user");

        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        AppUtils.applyMediumFont(toolbar_title);
        shopping=findViewById(R.id.shopping);
        add=findViewById(R.id.add_ads);
        money=findViewById(R.id.money);

        String ad_id=getIntent().getStringExtra("id");
        if (ad_id==null){
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.frame_layout, MainFragment.newInstance());
            transaction.commit();
        }else {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.frame_layout, AddFragment.newInstance(ad_id));
            transaction.commit();
            toolbar_title.setText(R.string.edit_ads);
            AppUtils.applyMediumFont(toolbar_title);
            shopping.setImageResource(R.drawable.gray_shopping);
            money.setImageResource(R.drawable.gray_smartphone);
        }

        shopping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.frame_layout, MainFragment.newInstance());
                transaction.commit();

                toolbar_title.setText("");
                shopping.setImageResource(R.drawable.shopping);
                money.setImageResource(R.drawable.gray_smartphone);

            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isUserRegistered()) {
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.frame_layout, AddFragment.newInstance("1"));
                    transaction.commit();

                    toolbar_title.setText(R.string.add_ads_);
                    AppUtils.applyMediumFont(toolbar_title);
                    shopping.setImageResource(R.drawable.gray_shopping);
                    money.setImageResource(R.drawable.gray_smartphone);
                } else {
                    showUserNotRegisteredDialog();
                }
            }
        });

        money.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isUserRegistered()) {
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.frame_layout, OrderFragment.newInstance());
                    transaction.commit();
                    toolbar_title.setText(R.string.request_offer);
                    AppUtils.applyMediumFont(toolbar_title);

                    shopping.setImageResource(R.drawable.gray_shopping);
                    money.setImageResource(R.drawable.smartphone);
                } else {
                    showUserNotRegisteredDialog();
                }
            }
        });

        drawer = findViewById(R.id.drawer_layout);


        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        View header = navigationView.getHeaderView(0);
        userName = header.findViewById(R.id.user_name);
        AppUtils.applyMediumFont(userName);
        userPhone = header.findViewById(R.id.user_phone);
        CircleImageView photo=header.findViewById(R.id.user_photo);

        if (isUserRegistered()){
            userName.setText(user);
            userPhone.setText(user_phone);
            try {
                Picasso.with(this).load(user_photo).placeholder(R.drawable.man).into(photo);
            }catch (Exception e){

            }
        }else{
            userPhone.setText(getString(R.string.phone_number));
            userName.setText(R.string.user_name);
        }

        Menu m = navigationView.getMenu();
        for (int i=0;i<m.size();i++) {
            MenuItem mi = m.getItem(i);

            //for aapplying a font to subMenu ...
            SubMenu subMenu = mi.getSubMenu();
            if (subMenu!=null && subMenu.size() >0 ) {
                for (int j=0; j <subMenu.size();j++) {
                    MenuItem subMenuItem = subMenu.getItem(j);
                    subMenuItem.setVisible(false);
                    applyFontToMenuItem(subMenuItem);
                }
            }

            applyFontToMenuItem(mi);
        }
        navigationView.setNavigationItemSelectedListener(this);
        disableNavigationViewScrollbars(navigationView);

        toolbar.setNavigationIcon(R.drawable.main_menu);
    }

    private void applyFontToMenuItem(MenuItem mi) {
        Typeface font = Typeface.createFromAsset(getAssets(), "GE_SS_Two_Medium.otf");
        SpannableString mNewTitle = new SpannableString(mi.getTitle());
        mNewTitle.setSpan(new CustomTypefaceSpan("" , font), 0 , mNewTitle.length(),  Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        mi.setTitle(mNewTitle);
    }

    private void disableNavigationViewScrollbars(NavigationView navigationView) {
        if (navigationView != null) {
            NavigationMenuView navigationMenuView = (NavigationMenuView) navigationView.getChildAt(0);
            if (navigationMenuView != null) {
                navigationMenuView.setVerticalScrollBarEnabled(false);
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            showExitDialog();
        }
    }

    public boolean isUserRegistered() {
        if (user==null){
            return false;
        }else {
            return true;
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        switch (id){
            case R.id.profile:
                // my account click.
                if (isUserRegistered()) {
                    startActivity(new Intent(HomeActivity.this,UserProfileActivity.class));
                } else {
                    showUserNotRegisteredDialog();
                }
                break;

            case R.id.ads:
                if (isUserRegistered()) {
                    startActivity(new Intent(HomeActivity.this,MyAdsActivity.class));
                } else {
                    showUserNotRegisteredDialog();
                }
                break;

            case R.id.orders:
                if (isUserRegistered()) {
                    startActivity(new Intent(HomeActivity.this,MyAllOrdersActivity.class));
                } else {
                    showUserNotRegisteredDialog();
                }
                break;

            case R.id.show_price:
                if (isUserRegistered()) {
                    startActivity(new Intent(HomeActivity.this,OrderShowPricesActivity.class));
                } else {
                    showUserNotRegisteredDialog();
                }
                break;

            case R.id.chats:
                if (isUserRegistered()) {
                    startActivity(new Intent(HomeActivity.this,ChatActivity.class));
                } else {
                    showUserNotRegisteredDialog();
                }
                break;

            case R.id.favourite:
                if (isUserRegistered()) {
                    startActivity(new Intent(HomeActivity.this,FavouritesActivity.class));
                } else {
                    showUserNotRegisteredDialog();
                }
                break;

            case R.id.conditions:
                    startActivity(new Intent(HomeActivity.this,ShrootActivity.class));
                break;

            case R.id.about_app:
                    startActivity(new Intent(HomeActivity.this,AboutAppActivity.class));
                break;

            case R.id.rate_app:
                    final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                    try {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                    } catch (android.content.ActivityNotFoundException anfe) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                    }
                break;

            case R.id.setting_app:
                if (isUserRegistered()) {
                    startActivity(new Intent(HomeActivity.this,SettingsActivity.class));
                } else {
                    showUserNotRegisteredDialog();
                }
                break;

            case R.id.logout:
                if (isUserRegistered()) {
                    showLogOutDialog();
                } else {
                    showUserNotRegisteredDialog();
                }
                break;


        }


        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void showLogOutDialog() {
        DialogUtils.showTwoActionButtonsDialog(this, R.string.sure_log_out,
                R.string.dialog_ok, new AppDialog.Action1ButtonListener() {
                    @Override
                    public void onAction1ButtonClick(Dialog dialog) {
                        CacheUtils.clearCache(HomeActivity.this);
                        dialog.dismiss();
                        startActivity(new Intent(HomeActivity.this,SignInActivity.class));
                        finish();
                    }
                }, R.string.dialog_cancel, null, true
        );
    }

    private void showExitDialog() {
        DialogUtils.showTwoActionButtonsDialog(this, R.string.sure_exit,
                R.string.dialog_ok, new AppDialog.Action1ButtonListener() {
                    @Override
                    public void onAction1ButtonClick(Dialog dialog) {
                        finish();
                    }
                }, R.string.dialog_cancel, null, true
        );
    }

    public void showUserNotRegisteredDialog() {
        DialogUtils.showUserNotRegisterDialog(this);
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
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClickCategory(String id) {
        Intent intent=new Intent(HomeActivity.this, CategoryActivity.class);
        intent.putExtra("category",id);
        startActivity(intent);
    }

    @Override
    public void onClickItem(String id) {
        Intent intent=new Intent(HomeActivity.this, PhoneProfileActivity.class);
        intent.putExtra("id",id);
        startActivity(intent);
    }

}