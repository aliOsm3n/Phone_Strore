package com.phonedeals.ascom.phonestrore.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.phonedeals.ascom.phonestrore.interfaces.ICategoryHandler;
import com.phonedeals.ascom.phonestrore.R;
import com.phonedeals.ascom.phonestrore.data.model.Recent;
import com.squareup.picasso.Picasso;

import java.util.List;

public class RecentAddedAdapter extends RecyclerView.Adapter<RecentAddedAdapter.MyViewHolder> {

    private List<Recent> recentList;
    private Context context;
    private ICategoryHandler handler;
    public RecentAddedAdapter(Context context, List<Recent> recentList) {
        this.recentList = recentList;
        this.context=context;
        handler=(ICategoryHandler)context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recent_added, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final Recent price = recentList.get(position);
        holder.views.setText(price.getViews());
        Picasso.with(context).load(price.getPhoto()).into(holder.photo);

        holder.photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handler.onClickItem(price.getId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return recentList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView views;
        public ImageView photo;

        public MyViewHolder(View view) {
            super(view);
            views = view.findViewById(R.id.num_view);
            photo=view.findViewById(R.id.item_bg);
        }
    }

}