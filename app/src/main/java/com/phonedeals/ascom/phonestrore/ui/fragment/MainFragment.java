package com.phonedeals.ascom.phonestrore.ui.fragment;


import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.phonedeals.ascom.phonestrore.R;
import com.phonedeals.ascom.phonestrore.adapters.CategoryItemsAdapter;
import com.phonedeals.ascom.phonestrore.adapters.MostViewAdapter;
import com.phonedeals.ascom.phonestrore.adapters.RecentAddedAdapter;
import com.phonedeals.ascom.phonestrore.data.model.Items;
import com.phonedeals.ascom.phonestrore.data.model.MostView;
import com.phonedeals.ascom.phonestrore.data.model.Recent;
import com.phonedeals.ascom.phonestrore.ui.activity.PhoneProfileActivity;
import com.phonedeals.ascom.phonestrore.ui.dialog.AppDialog;
import com.phonedeals.ascom.phonestrore.util.AppUtils;
import com.phonedeals.ascom.phonestrore.util.CustomRecyclerView;
import com.phonedeals.ascom.phonestrore.util.DialogUtils;
import com.phonedeals.ascom.phonestrore.util.SpeedyLinearLayoutManager;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class MainFragment extends Fragment  {
    ArrayList<MostView> list = new ArrayList<>();
    private RecentAddedAdapter mAdapter;
    private MostViewAdapter mostAdapter;
    private List<Recent> recentList = new ArrayList<>();
    private List<Items> itemsList = new ArrayList<>();
    private RecyclerView category_recy;
    private CustomRecyclerView recyclerView,mostRecycler;
    private CategoryItemsAdapter itemsAdapter;
    public int i=0;

    public MainFragment() {
    }

    public static MainFragment newInstance() {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_main, container, false);
        initView(view);
        return view;
    }

    private void loadRecentAdd() {
        AndroidNetworking.post("http://athelapps.com/phone/api/item/recent-add")
                .addBodyParameter("api_key", "BUNgDZlDqosZXWJtjYUgdx5aWbqKC2gr7wRxzVFh8Pg")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {

                            try {
                                if (new JSONObject(response).getString("code").toString().equals("200")){

                                    JSONObject jsonObject=new JSONObject(response);
                                    JSONArray jsonArray=jsonObject.getJSONArray("data");

                                    if (jsonArray.length()==0){

                                    } else {
                                        for (int i = 0; i < jsonArray.length(); i++) {
                                            JSONObject object = jsonArray.getJSONObject(i);
                                            Recent recent = new Recent(object.getString("id"), object.getString("photo"), object.getString("num_views"));
                                            recentList.add(recent);

                                            mAdapter.notifyDataSetChanged();
                                            if (i == 8)
                                                break;

                                        }
                                    }

                                    loadData();


                                }else {
                                    AppUtils.dismissProgressDialog();
                                    AppUtils.showErrorToast(getActivity(),getString(R.string.recent_add_not_load));
                                }
                            } catch (JSONException e) {
                                AppUtils.dismissProgressDialog();
                            }

                    }
                    @Override
                    public void onError(ANError anError) {
                        AppUtils.dismissProgressDialog();
                        showCanNotLoadDataDialog();
                    }
                });
    }

    private void initView(View view) {

        mostRecycler=view.findViewById(R.id.most_recycler);
        mostAdapter = new MostViewAdapter(getActivity(),list);
        mostRecycler.setLayoutManager(new SpeedyLinearLayoutManager(getActivity(), SpeedyLinearLayoutManager.HORIZONTAL, false));
        mostRecycler.setItemAnimator(new DefaultItemAnimator());
        mostRecycler.setAdapter(mostAdapter);

        recyclerView=view.findViewById(R.id.recent_recycler);
        mAdapter = new RecentAddedAdapter(getActivity(),recentList);
        recyclerView.setLayoutManager(new SpeedyLinearLayoutManager(getActivity(), SpeedyLinearLayoutManager.HORIZONTAL, false));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        category_recy=view.findViewById(R.id.category_recycler);
        itemsAdapter = new CategoryItemsAdapter(getActivity(),itemsList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        category_recy.setLayoutManager(mLayoutManager);
        category_recy.setItemAnimator(new DefaultItemAnimator());
        category_recy.setAdapter(itemsAdapter);


        TextView choose=view.findViewById(R.id.choose_for_you);
        TextView new_produact=view.findViewById(R.id.new_produact);



        loadMostView();
        AppUtils.applyMediumFont(choose,new_produact);
    }

    public void loadMostView(){
        AppUtils.showProgressDialog(getActivity());

        AndroidNetworking.post("http://athelapps.com/phone/api/item/most-views")
                .addBodyParameter("api_key", "BUNgDZlDqosZXWJtjYUgdx5aWbqKC2gr7wRxzVFh8Pg")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            if (new JSONObject(response).getString("code").toString().equalsIgnoreCase("200")) {

                                JSONObject obj = new JSONObject(response);

                                JSONArray jsonArray = obj.getJSONArray("data");

                                if (jsonArray.length()==0){

                                } else {
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject object = jsonArray.getJSONObject(i);

                                        MostView view = new MostView(object.getString("id"), object.getString("photo"), object.getString("num_views"));
                                        list.add(view);

                                        mostAdapter.notifyDataSetChanged();

                                        if(i==8)
                                            break;

                                    }
                                }
                                loadRecentAdd();

                            } else if ( new JSONObject(response).getString("code").toString().equalsIgnoreCase("401")) {
                                AppUtils.dismissProgressDialog();
                                AppUtils.showErrorToast(getActivity(),getString(R.string.your_request_not_done));
                            }
                        } catch (JSONException e) {
                            AppUtils.dismissProgressDialog();
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
        DialogUtils.showTwoActionButtonsDialog(getActivity(), R.string.dialog_error_no_internet,
                R.string.dialog_ok, new AppDialog.Action1ButtonListener() {
                    @Override
                    public void onAction1ButtonClick(Dialog dialog) {
                        dialog.dismiss();
                        loadMostView();
                    }
                }, R.string.dialog_cancel, new AppDialog.Action2ButtonListener() {
                    @Override
                    public void onAction2ButtonClick(Dialog dialog) {
                        getActivity().onBackPressed();
                    }
                }, false);
    }

    public void loadData(){

        AndroidNetworking.post("http://athelapps.com/phone/api/list/categories-items")
                .addBodyParameter("api_key", "BUNgDZlDqosZXWJtjYUgdx5aWbqKC2gr7wRxzVFh8Pg")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        AppUtils.dismissProgressDialog();

                            try {
                                if (new JSONObject(response).getString("code").toString().equals("200")){
                                    JSONObject jsonObject=new JSONObject(response);
                                    JSONArray jsonArray=jsonObject.getJSONArray("data");

                                    for (int i=0;i<jsonArray.length();i++){
                                        JSONObject object=jsonArray.getJSONObject(i);

                                        JSONArray j=object.getJSONArray("items");


                                        if (j.length()!= 0 && j.length()> 8){
                                            Items myAds=new Items(object.getString("id"),object.getString("name"),
                                                    j.getJSONObject(0).getString("id"), j.getJSONObject(0).getString("photo"),j.getJSONObject(0).getString("num_views")
                                                    ,j.getJSONObject(1).getString("id"),j.getJSONObject(1).getString("photo"),j.getJSONObject(1).getString("num_views")
                                                    ,j.getJSONObject(2).getString("id"),j.getJSONObject(2).getString("photo"),j.getJSONObject(2).getString("num_views")
                                                    ,j.getJSONObject(3).getString("id"),j.getJSONObject(3).getString("photo"),j.getJSONObject(3).getString("num_views")
                                                    ,j.getJSONObject(4).getString("id"),j.getJSONObject(4).getString("photo"),j.getJSONObject(4).getString("num_views")
                                                    ,j.getJSONObject(5).getString("id"),j.getJSONObject(5).getString("photo"),j.getJSONObject(5).getString("num_views")
                                                    ,j.getJSONObject(6).getString("id"),j.getJSONObject(6).getString("photo"),j.getJSONObject(6).getString("num_views")
                                                    ,j.getJSONObject(7).getString("id"),j.getJSONObject(7).getString("photo"),j.getJSONObject(7).getString("num_views")
                                                    ,j.getJSONObject(8).getString("id"),j.getJSONObject(8).getString("photo"),j.getJSONObject(8).getString("num_views"));
                                            itemsList.add(myAds);
                                        }else if (j.length()==1){
                                            Items myAds=new Items(object.getString("id"),object.getString("name"),
                                                    j.getJSONObject(0).getString("id"), j.getJSONObject(0).getString("photo"),j.getJSONObject(0).getString("num_views")
                                                    ,"","",""
                                                    ,"","",""
                                                    ,"","",""
                                                    ,"","",""
                                                    ,"","",""
                                                    ,"","",""
                                                    ,"","",""
                                                    ,"","",""
                                            );
                                            itemsList.add(myAds);
                                        } else if (j.length()==2){
                                            Items myAds=new Items(object.getString("id"),object.getString("name"),
                                                    j.getJSONObject(0).getString("id"), j.getJSONObject(0).getString("photo"),j.getJSONObject(0).getString("num_views")
                                                    ,j.getJSONObject(1).getString("id"),j.getJSONObject(1).getString("photo"),j.getJSONObject(1).getString("num_views")
                                                    ,"","",""
                                                    ,"","",""
                                                    ,"","",""
                                                    ,"","",""
                                                    ,"","",""
                                                    ,"","",""
                                                    ,"","",""
                                            );
                                            itemsList.add(myAds);
                                        } else if (j.length()==3){
                                            Items myAds=new Items(object.getString("id"),object.getString("name"),
                                                    j.getJSONObject(0).getString("id"), j.getJSONObject(0).getString("photo"),j.getJSONObject(0).getString("num_views")
                                                    ,j.getJSONObject(1).getString("id"),j.getJSONObject(1).getString("photo"),j.getJSONObject(1).getString("num_views")
                                                    ,j.getJSONObject(2).getString("id"),j.getJSONObject(2).getString("photo"),j.getJSONObject(2).getString("num_views")
                                                    ,"","",""
                                                    ,"","",""
                                                    ,"","",""
                                                    ,"","",""
                                                    ,"","",""
                                                    ,"","",""
                                            );
                                            itemsList.add(myAds);
                                        } else if (j.length()==4){
                                            Items myAds=new Items(object.getString("id"),object.getString("name"),
                                                    j.getJSONObject(0).getString("id"), j.getJSONObject(0).getString("photo"),j.getJSONObject(0).getString("num_views")
                                                    ,j.getJSONObject(1).getString("id"),j.getJSONObject(1).getString("photo"),j.getJSONObject(1).getString("num_views")
                                                    ,j.getJSONObject(2).getString("id"),j.getJSONObject(2).getString("photo"),j.getJSONObject(2).getString("num_views")
                                                    ,j.getJSONObject(3).getString("id"),j.getJSONObject(3).getString("photo"),j.getJSONObject(3).getString("num_views")
                                                    ,"","",""
                                                    ,"","",""
                                                    ,"","",""
                                                    ,"","",""
                                                    ,"","",""
                                            );
                                            itemsList.add(myAds);
                                        } else if (j.length()==5){
                                            Items myAds=new Items(object.getString("id"),object.getString("name"),
                                                    j.getJSONObject(0).getString("id"), j.getJSONObject(0).getString("photo"),j.getJSONObject(0).getString("num_views")
                                                    ,j.getJSONObject(1).getString("id"),j.getJSONObject(1).getString("photo"),j.getJSONObject(1).getString("num_views")
                                                    ,j.getJSONObject(2).getString("id"),j.getJSONObject(2).getString("photo"),j.getJSONObject(2).getString("num_views")
                                                    ,j.getJSONObject(3).getString("id"),j.getJSONObject(3).getString("photo"),j.getJSONObject(3).getString("num_views")
                                                    ,j.getJSONObject(4).getString("id"),j.getJSONObject(4).getString("photo"),j.getJSONObject(4).getString("num_views")
                                                    ,"","",""
                                                    ,"","",""
                                                    ,"","",""
                                                    ,"","",""
                                            );
                                            itemsList.add(myAds);
                                        } else if (j.length()==6){
                                            Items myAds=new Items(object.getString("id"),object.getString("name"),
                                                    j.getJSONObject(0).getString("id"), j.getJSONObject(0).getString("photo"),j.getJSONObject(0).getString("num_views")
                                                    ,j.getJSONObject(1).getString("id"),j.getJSONObject(1).getString("photo"),j.getJSONObject(1).getString("num_views")
                                                    ,j.getJSONObject(2).getString("id"),j.getJSONObject(2).getString("photo"),j.getJSONObject(2).getString("num_views")
                                                    ,j.getJSONObject(3).getString("id"),j.getJSONObject(3).getString("photo"),j.getJSONObject(3).getString("num_views")
                                                    ,j.getJSONObject(4).getString("id"),j.getJSONObject(4).getString("photo"),j.getJSONObject(4).getString("num_views")
                                                    ,j.getJSONObject(5).getString("id"),j.getJSONObject(5).getString("photo"),j.getJSONObject(5).getString("num_views")
                                                    ,"","",""
                                                    ,"","",""
                                                    ,"","",""
                                            );
                                            itemsList.add(myAds);
                                        } else if (j.length()==7){
                                            Items myAds=new Items(object.getString("id"),object.getString("name"),
                                                    j.getJSONObject(0).getString("id"), j.getJSONObject(0).getString("photo"),j.getJSONObject(0).getString("num_views")
                                                    ,j.getJSONObject(1).getString("id"),j.getJSONObject(1).getString("photo"),j.getJSONObject(1).getString("num_views")
                                                    ,j.getJSONObject(2).getString("id"),j.getJSONObject(2).getString("photo"),j.getJSONObject(2).getString("num_views")
                                                    ,j.getJSONObject(3).getString("id"),j.getJSONObject(3).getString("photo"),j.getJSONObject(3).getString("num_views")
                                                    ,j.getJSONObject(4).getString("id"),j.getJSONObject(4).getString("photo"),j.getJSONObject(4).getString("num_views")
                                                    ,j.getJSONObject(5).getString("id"),j.getJSONObject(5).getString("photo"),j.getJSONObject(5).getString("num_views")
                                                    ,j.getJSONObject(6).getString("id"),j.getJSONObject(6).getString("photo"),j.getJSONObject(6).getString("num_views")
                                                    ,"","",""
                                                    ,"","",""
                                            );
                                            itemsList.add(myAds);
                                        } else if (j.length()==8){
                                            Items myAds=new Items(object.getString("id"),object.getString("name"),
                                                    j.getJSONObject(0).getString("id"), j.getJSONObject(0).getString("photo"),j.getJSONObject(0).getString("num_views")
                                                    ,j.getJSONObject(1).getString("id"),j.getJSONObject(1).getString("photo"),j.getJSONObject(1).getString("num_views")
                                                    ,j.getJSONObject(2).getString("id"),j.getJSONObject(2).getString("photo"),j.getJSONObject(2).getString("num_views")
                                                    ,j.getJSONObject(3).getString("id"),j.getJSONObject(3).getString("photo"),j.getJSONObject(3).getString("num_views")
                                                    ,j.getJSONObject(4).getString("id"),j.getJSONObject(4).getString("photo"),j.getJSONObject(4).getString("num_views")
                                                    ,j.getJSONObject(5).getString("id"),j.getJSONObject(5).getString("photo"),j.getJSONObject(5).getString("num_views")
                                                    ,j.getJSONObject(6).getString("id"),j.getJSONObject(6).getString("photo"),j.getJSONObject(6).getString("num_views")
                                                    ,j.getJSONObject(7).getString("id"),j.getJSONObject(7).getString("photo"),j.getJSONObject(7).getString("num_views")
                                                    ,"","",""
                                            );
                                            itemsList.add(myAds);
                                        }
                                        itemsAdapter.notifyDataSetChanged();

                                    }

                                }else {
                                    AppUtils.showErrorToast(getActivity(),getString(R.string.your_request_not_done));
                                }
                            } catch (JSONException e) {
                                AppUtils.dismissProgressDialog();
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

}