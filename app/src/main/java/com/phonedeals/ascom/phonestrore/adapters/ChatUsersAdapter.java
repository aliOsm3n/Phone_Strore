package com.phonedeals.ascom.phonestrore.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.phonedeals.ascom.phonestrore.data.model.Chat;
import com.phonedeals.ascom.phonestrore.interfaces.IChatItemClickHandler;
import com.phonedeals.ascom.phonestrore.interfaces.IMyOrderClickHandler;
import com.phonedeals.ascom.phonestrore.R;
import com.phonedeals.ascom.phonestrore.util.AppUtils;

import java.util.List;
import java.util.Random;

public class ChatUsersAdapter extends RecyclerView.Adapter<ChatUsersAdapter.MyViewHolder> {

    private List<Chat> prices;
    private Context context;
    IChatItemClickHandler handler;
    public ChatUsersAdapter(Context context, List<Chat> prices) {
        this.prices = prices;
        this.context=context;
        handler= (IChatItemClickHandler) context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.chat, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final Chat price = prices.get(position);
        holder.name.setText(price.getName());


        if (position%2==0){
            holder.layout.setBackgroundColor(Color.argb(255, 186, 243, 250));
        }else {
            holder.layout.setBackgroundColor(Color.argb(255, 250, 245, 209));
        }
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handler.onClick(price.getId(),price.getFrom(),price.getItemId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return prices.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public LinearLayout layout;

        public MyViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.chat_head);
            layout=view.findViewById(R.id.chat_bgcolor);
            AppUtils.applyLightFont(name);
        }
    }


}