package com.phonedeals.ascom.phonestrore.ui.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.phonedeals.ascom.phonestrore.R;
import com.phonedeals.ascom.phonestrore.data.prefs.CacheUtils;
import com.phonedeals.ascom.phonestrore.ui.dialog.AppDialog;
import com.phonedeals.ascom.phonestrore.util.AppUtils;
import com.phonedeals.ascom.phonestrore.util.DialogUtils;
import com.phonedeals.ascom.phonestrore.util.ValidateUtils;
import com.google.android.gms.maps.OnMapReadyCallback;

import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONObject;

public class IntroduceOfferActivity extends AppCompatActivity implements OnMapReadyCallback {

    private MapView mapView;
    private GoogleMap gmap;
    private float latitude;
    private float longitude;
    private float currentLatitude;
    private float currentLongitude;
    private static final String MAP_VIEW_BUNDLE_KEY = "MapViewBundleKey";
    private Button makeOffer;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_introduce_offer);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(R.drawable.ic_back);
        latitude=getIntent().getFloatExtra("latitude", (float) 24.4320);
        longitude=getIntent().getFloatExtra("longitude", (float) 46.45670);

        makeOffer=  findViewById(R.id.introduce_order);


        AppUtils.applyBoldFont(makeOffer);
        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAP_VIEW_BUNDLE_KEY);
        }

        mapView = findViewById(R.id.mapView);
        mapView.onCreate(mapViewBundle);
        mapView.getMapAsync(this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle(MAP_VIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAP_VIEW_BUNDLE_KEY, mapViewBundle);
        }

        mapView.onSaveInstanceState(mapViewBundle);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    protected void onPause() {
        mapView.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gmap = googleMap;

        gmap.getUiSettings().setZoomControlsEnabled(true);
        LatLng ny = new LatLng(latitude, longitude);
        CameraPosition cameraPosition = new CameraPosition.Builder().target(ny).zoom(15).build();
        gmap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        gmap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {

                currentLatitude=(float) cameraPosition.target.latitude;
                currentLongitude=(float) cameraPosition.target.longitude;
                Log.e("wwwwwwwwww",currentLatitude+"   "+currentLongitude);

            }
        });
    }

    public void selectLocation(View view) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("latitude",currentLatitude+"");
        returnIntent.putExtra("longitude",currentLongitude+"");
        setResult(Activity.RESULT_OK,returnIntent);
        finish();
    }

}