package com.phonedeals.ascom.phonestrore.ui.activity;

import android.app.Dialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.phonedeals.ascom.phonestrore.data.model.Chat;
import com.phonedeals.ascom.phonestrore.data.model.MyAds;
import com.phonedeals.ascom.phonestrore.data.prefs.CacheUtils;
import com.phonedeals.ascom.phonestrore.interfaces.IChatItemClickHandler;
import com.phonedeals.ascom.phonestrore.interfaces.IMyOrderClickHandler;
import com.phonedeals.ascom.phonestrore.R;
import com.phonedeals.ascom.phonestrore.adapters.ChatUsersAdapter;
import com.phonedeals.ascom.phonestrore.ui.dialog.AppDialog;
import com.phonedeals.ascom.phonestrore.util.AppUtils;
import com.phonedeals.ascom.phonestrore.util.DialogUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity implements IChatItemClickHandler {
    private RecyclerView recyclerView;
    private ArrayList<String> chatsId;
    private ChatUsersAdapter mAdapter;
    private List<Chat> chats = new ArrayList<>();
    private Toolbar toolbar;
    private TextView message;
    private String itemId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        itemId=getIntent().getStringExtra("id");

        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(R.drawable.ic_back);
        message=findViewById(R.id.title);
        AppUtils.applyMediumFont(message);

        recyclerView=findViewById(R.id.recycler_view);
        mAdapter = new ChatUsersAdapter(this,chats);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());

        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        if (itemId==null){
            loadAllChats();
        }else {
            loadItemChats(itemId);
        }

    }

    private void loadAllChats() {
        AppUtils.showProgressDialog(this);
        AndroidNetworking.post("http://athelapps.com/phone/api/user-chats")
                .addBodyParameter("api_key", "BUNgDZlDqosZXWJtjYUgdx5aWbqKC2gr7wRxzVFh8Pg")
                .addBodyParameter("token", CacheUtils.getUserToken(this,"user"))
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
                                    AppUtils.showInfoToast(ChatActivity.this,getString(R.string.no_chats));
                                }
                                if (jsonArray.length()!=0) {
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject object = jsonArray.getJSONObject(i);
                                        Chat chat=new Chat(object.getString("id"),object.getString("title"),object.getString("from"),object.getString("item_id"));
                                        chats.add(chat);
                                    }
                                    mAdapter.notifyDataSetChanged();
                                } else {
                                    AppUtils.showInfoToast(ChatActivity.this,getString(R.string.no_chats));
                                }
                            } else {
                                AppUtils.showInfoToast(ChatActivity.this,getString(R.string.no_chats));
                            }
                        } catch (JSONException e) {
                            AppUtils.showErrorToast(ChatActivity.this,getString(R.string.error));
                        }
                    }
                    @Override
                    public void onError(ANError anError) {
                        AppUtils.dismissProgressDialog();
                        showCanNotLoadDataDialog();
                    }
                });
    }

    private void loadItemChats(String itemId) {
        AppUtils.showProgressDialog(this);
        AndroidNetworking.post("http://athelapps.com/phone/api/item-chats")
                .addBodyParameter("api_key", "BUNgDZlDqosZXWJtjYUgdx5aWbqKC2gr7wRxzVFh8Pg")
                .addBodyParameter("token", CacheUtils.getUserToken(this,"user"))
                .addBodyParameter("item_id",itemId)
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
                                    AppUtils.showInfoToast(ChatActivity.this,getString(R.string.no_chats));
                                }
                                if (jsonArray.length()!=0) {
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject object = jsonArray.getJSONObject(i);
                                        Chat chat=new Chat(object.getString("id"),object.getString("title"),object.getString("from"),object.getString("item_id"));
                                        chats.add(chat);
                                    }
                                    mAdapter.notifyDataSetChanged();
                                } else {
                                    AppUtils.showInfoToast(ChatActivity.this,getString(R.string.no_chats));
                                }
                            } else {
                                AppUtils.showInfoToast(ChatActivity.this,getString(R.string.no_chats));
                            }
                        } catch (JSONException e) {
                            AppUtils.showErrorToast(ChatActivity.this,getString(R.string.error));
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
    public void onClick(String id,String userId,String itemId) {
        Intent intent=new Intent(this,ChatRoomActivity.class);
        intent.putExtra("chat_id",id);
        intent.putExtra("itemId",itemId);
        startActivity(intent);
    }

    private void showCanNotLoadDataDialog() {
        DialogUtils.showTwoActionButtonsDialog(this, R.string.dialog_error_no_internet,
                R.string.dialog_ok, new AppDialog.Action1ButtonListener() {
                    @Override
                    public void onAction1ButtonClick(Dialog dialog) {
                        dialog.dismiss();
                        if (itemId==null){
                            loadAllChats();
                        }else {
                            loadItemChats(itemId);
                        }
                    }
                }, R.string.dialog_cancel, new AppDialog.Action2ButtonListener() {
                    @Override
                    public void onAction2ButtonClick(Dialog dialog) {
                        finish();
                    }
                }, false);
    }

}
