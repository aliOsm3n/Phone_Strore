package com.phonedeals.ascom.phonestrore.ui.activity;

import android.app.Dialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.phonedeals.ascom.phonestrore.R;
import com.phonedeals.ascom.phonestrore.data.model.ReceiveMessage;
import com.phonedeals.ascom.phonestrore.adapters.ChatRoomAdapter;
import com.phonedeals.ascom.phonestrore.data.model.ChatRoom;
import com.phonedeals.ascom.phonestrore.data.prefs.CacheUtils;
import com.phonedeals.ascom.phonestrore.ui.dialog.AppDialog;
import com.phonedeals.ascom.phonestrore.util.AppUtils;
import com.phonedeals.ascom.phonestrore.util.DialogUtils;
import com.phonedeals.ascom.phonestrore.util.ValidateUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ChatRoomActivity extends AppCompatActivity {

    private EditText chat_box;
    private ImageView send;
    private RecyclerView recyclerView;
    private ChatRoomAdapter mAdapter;
    private List<ChatRoom> chats = new ArrayList<>();
    private Toolbar toolbar;
    private String chat_id,itemId,user_photo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);

        chat_id=getIntent().getStringExtra("chat_id");
        itemId=getIntent().getStringExtra("itemId");
        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(R.drawable.ic_back);

        chat_box=findViewById(R.id.message_box);
        send=findViewById(R.id.send_btn);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chat_id==null){
                    sendFirstMessage();
                }else {
                    sendNewMessage();
                }
            }
        });

        recyclerView=findViewById(R.id.recycler_view);
        mAdapter = new ChatRoomAdapter(this,chats);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());

        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        Log.e("llllllllll",chat_id+"");

        if (chat_id!=null){
            loadAllMessages(chat_id);
        }
    }

    private void sendFirstMessage() {
        final String msg=AppUtils.getTextContent(chat_box);

        if (ValidateUtils.missingInputs(msg)) {
            AppUtils.showInfoDialog(this,R.string.empty_message);
            return;
        }
        AppUtils.showProgressDialog(this);
        AndroidNetworking.post("http://athelapps.com/phone/api/chat/send")
                .addBodyParameter("api_key", "BUNgDZlDqosZXWJtjYUgdx5aWbqKC2gr7wRxzVFh8Pg")
                .addBodyParameter("token", CacheUtils.getUserToken(this,"user"))
                .addBodyParameter("message",msg)
                .addBodyParameter("item_id",itemId)
                .setPriority(Priority.HIGH)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        AppUtils.dismissProgressDialog();
                        Log.e("cccccccccccccccc",response);

                        try {
                            if (new JSONObject(response).getString("code").toString().equals("201")){
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

                                ChatRoom room=new ChatRoom("","",CacheUtils.getUserId(ChatRoomActivity.this,"user"),simpleDateFormat.format(new Date()),msg,"");
                                chats.add(room);
                                mAdapter.notifyDataSetChanged();
                                recyclerView.scrollToPosition(mAdapter.getLastitemPosition());
                                chat_box.setText("");

                            } else {
                                AppUtils.showInfoToast(ChatRoomActivity.this,getString(R.string.massage_not_send));
                            }
                        } catch (JSONException e) {
                        }
                    }
                    @Override
                    public void onError(ANError anError) {
                        AppUtils.dismissProgressDialog();
                        AppUtils.showInfoToast(ChatRoomActivity.this,getString(R.string.massage_not_send));
                    }
                });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageReceived(ReceiveMessage message){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

        Log.e("zzzzzzzz",message.getJson());
        ChatRoom room=new ChatRoom("","","",simpleDateFormat.format(new Date()),message.getJson(),user_photo);
        chats.add(room);
        mAdapter.notifyDataSetChanged();
        recyclerView.scrollToPosition(mAdapter.getLastitemPosition());
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    private void loadAllMessages(String id) {
        if (!AppUtils.isInternetAvailable(this)) {
            AppUtils.showNoInternetDialog(this);
            return;
        }
        AppUtils.showProgressDialog(this);
        AndroidNetworking.post("http://athelapps.com/phone/api/chat/load")
                .addBodyParameter("api_key", "BUNgDZlDqosZXWJtjYUgdx5aWbqKC2gr7wRxzVFh8Pg")
                .addBodyParameter("token", CacheUtils.getUserToken(this,"user"))
                .addBodyParameter("chat_id",id)
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
                                    AppUtils.showInfoToast(ChatRoomActivity.this,getString(R.string.no_chats));
                                }
                                 if (jsonArray.length()!=0) {
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject object = jsonArray.getJSONObject(i);
                                        user_photo=new JSONObject(response).getJSONObject("users_photos").getString(object.getString("from"));
                                        ChatRoom chat=new ChatRoom(object.getString("id"),object.getString("chat_id"),object.getString("from"),object.getString("created_at"),object.getString("message"),user_photo);
                                        chats.add(chat);
                                    }
                                    mAdapter.notifyDataSetChanged();
                                    recyclerView.scrollToPosition(mAdapter.getLastitemPosition());

                                } else {
                                     AppUtils.showInfoToast(ChatRoomActivity.this,getString(R.string.no_chats));
                                }
                            } else {
                                AppUtils.showInfoToast(ChatRoomActivity.this,getString(R.string.no_chats));
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

    private void sendNewMessage() {
        final String msg=AppUtils.getTextContent(chat_box);
        if (ValidateUtils.missingInputs(msg)) {
            AppUtils.showInfoDialog(this,R.string.empty_message);
            return;
        }
        AppUtils.showProgressDialog(this);

        AndroidNetworking.post("http://athelapps.com/phone/api/chat/send")
                .addBodyParameter("api_key", "BUNgDZlDqosZXWJtjYUgdx5aWbqKC2gr7wRxzVFh8Pg")
                .addBodyParameter("token", CacheUtils.getUserToken(this,"user"))
                .addBodyParameter("message",msg)
                .addBodyParameter("chat_id",chat_id)
                .setPriority(Priority.HIGH)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        AppUtils.dismissProgressDialog();
                        Log.e("rrrrrrrrr",response);
                        try {
                            if (new JSONObject(response).getString("code").toString().equals("201")){
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

                                ChatRoom room=new ChatRoom("","",CacheUtils.getUserId(ChatRoomActivity.this,"user"),simpleDateFormat.format(new Date()),msg,"");
                                chats.add(room);
                                mAdapter.notifyDataSetChanged();
                                recyclerView.scrollToPosition(mAdapter.getLastitemPosition());
                                chat_box.setText("");

                            } else {
                                AppUtils.showInfoToast(ChatRoomActivity.this,getString(R.string.message_not_send));
                            }
                        } catch (JSONException e) {
                        }
                    }
                    @Override
                    public void onError(ANError anError) {
                        AppUtils.dismissProgressDialog();
                        AppUtils.showInfoToast(ChatRoomActivity.this,getString(R.string.message_not_send));
                    }
                });
    }

    private void showCanNotLoadDataDialog() {
        DialogUtils.showTwoActionButtonsDialog(this, R.string.dialog_error_no_internet,
                R.string.dialog_ok, new AppDialog.Action1ButtonListener() {
                    @Override
                    public void onAction1ButtonClick(Dialog dialog) {
                        dialog.dismiss();
                            loadAllMessages(chat_id);
                    }
                }, R.string.dialog_cancel, new AppDialog.Action2ButtonListener() {
                    @Override
                    public void onAction2ButtonClick(Dialog dialog) {
                        finish();
                    }
                }, false);
    }

}
