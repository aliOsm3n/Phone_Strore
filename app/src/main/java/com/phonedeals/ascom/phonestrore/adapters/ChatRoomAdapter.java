package com.phonedeals.ascom.phonestrore.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.phonedeals.ascom.phonestrore.R;
import com.phonedeals.ascom.phonestrore.data.model.ChatRoom;
import com.phonedeals.ascom.phonestrore.data.prefs.CacheUtils;
import com.phonedeals.ascom.phonestrore.util.AppUtils;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatRoomAdapter extends RecyclerView.Adapter<ChatRoomAdapter.MyViewHolder> {

    private List<ChatRoom> prices;
    private Context context;
    private int indecator;
    private String photo="";
    public ChatRoomAdapter(Context context, List<ChatRoom> prices) {
        this.prices = prices;
        this.context=context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView=null;
        if (viewType==1){
            itemView= LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.user_1, parent, false);
        }else {
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.user_2, parent, false);
        }

        return new MyViewHolder(itemView);
    }

    @Override
    public int getItemViewType(int position) {
        Log.e("fffff",prices.get(position).getFrom()+"   keys   "+CacheUtils.getUserId(context,"user"));
        if (prices.get(position).getFrom().equalsIgnoreCase(CacheUtils.getUserId(context,"user"))){
            indecator=1;
            return 1;
        }
        indecator=0;
        return 0;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final ChatRoom price = prices.get(position);
        holder.content.setText(price.getContent());
        holder.msg_time.setText(price.getTime());

        try {
            if (indecator==1){
                Picasso.with(context).load(CacheUtils.getUserPhoto(context,"user")).placeholder(R.drawable.man).into(holder.imageView);
            } else {
                Picasso.with(context).load(price.getPhoto()).placeholder(R.drawable.man).into(holder.imageView);
            }
        }catch (Exception e){

        }

    }

    @Override
    public int getItemCount() {
        return prices.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView content,msg_time;
        public LinearLayout layout;
        public CircleImageView imageView;

        public MyViewHolder(View view) {
            super(view);
            content = view.findViewById(R.id.message_content);
            msg_time=view.findViewById(R.id.msg_time);
            imageView=view.findViewById(R.id.user_1_id);
            AppUtils.applyMediumFont(content);
        }
    }

    public int getLastitemPosition() {
        return prices.size() - 1;
    }

}