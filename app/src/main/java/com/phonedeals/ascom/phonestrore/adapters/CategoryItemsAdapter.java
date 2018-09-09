package com.phonedeals.ascom.phonestrore.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.phonedeals.ascom.phonestrore.interfaces.ICategoryHandler;
import com.phonedeals.ascom.phonestrore.R;
import com.phonedeals.ascom.phonestrore.data.model.Items;
import com.phonedeals.ascom.phonestrore.util.AppUtils;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CategoryItemsAdapter extends RecyclerView.Adapter<CategoryItemsAdapter.MyViewHolder> {

    private List<Items> prices;
    private Context context;
    private ICategoryHandler handler;
    public CategoryItemsAdapter(Context context, List<Items> prices) {
        this.prices = prices;
        this.context=context;
        handler= (ICategoryHandler) context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
            final Items items = prices.get(position);
            holder.categoryName.setText(items.getCategoryName());
            holder.item1Views.setText(items.getItem1Views());
            holder.item2Views.setText(items.getItem2Views());
            holder.item3Views.setText(items.getItem3Views());
            holder.item4Views.setText(items.getItem4Views());
            holder.item5Views.setText(items.getItem5Views());
            holder.item6Views.setText(items.getItem6Views());
            holder.item7Views.setText(items.getItem7Views());
            holder.item8Views.setText(items.getItem8Views());
            holder.item9Views.setText(items.getItem9Views());

        try {
            Picasso.with(context).load(items.getItem1Photo()).into(holder.item1Photo);
            Picasso.with(context).load(items.getItem2Photo()).into(holder.item2Photo);
            Picasso.with(context).load(items.getItem3Photo()).into(holder.item3Photo);
            Picasso.with(context).load(items.getItem4Photo()).into(holder.item4Photo);
            Picasso.with(context).load(items.getItem5Photo()).into(holder.item5Photo);
            Picasso.with(context).load(items.getItem6Photo()).into(holder.item6Photo);
            Picasso.with(context).load(items.getItem7Photo()).into(holder.item7Photo);
            Picasso.with(context).load(items.getItem8Photo()).into(holder.item8Photo);
            Picasso.with(context).load(items.getItem9Photo()).into(holder.item9Photo);
        }catch (Exception e){
            Log.e("gggggg",e.getMessage());
        }
            holder.categoryName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    handler.onClickCategory(items.getCategoryId());
                }
            });
            holder.item1Photo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    handler.onClickItem(items.getItem1Id());
                }
            });

            holder.item2Photo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    handler.onClickItem(items.getItem2Id());
                }
            });

            holder.item3Photo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    handler.onClickItem(items.getItem3Id());
                }
            });

            holder.item4Photo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    handler.onClickItem(items.getItem4Id());
                }
            });

            holder.item5Photo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    handler.onClickItem(items.getItem5Id());
                }
            });

            holder.item6Photo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    handler.onClickItem(items.getItem6Id());
                }
            });

            holder.item7Photo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    handler.onClickItem(items.getItem7Id());
                }
            });

            holder.item8Photo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    handler.onClickItem(items.getItem8Id());
                }
            });

            holder.item9Photo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    handler.onClickItem(items.getItem9Id());
                }
            });

    }

    @Override
    public int getItemCount() {
        if (prices==null){
            return 0;
        }
        return prices.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView categoryName, item1Views,item2Views,item3Views,item4Views,
        item5Views,item6Views,item7Views,item8Views ,item9Views;
        public ImageView item1Photo,item2Photo,item3Photo,item4Photo,item5Photo,item6Photo,item7Photo,item8Photo,item9Photo;
        public View mView;

        public MyViewHolder(View view) {
            super(view);
            categoryName = view.findViewById(R.id.category_name);
            AppUtils.applyMediumFont(categoryName);
            item1Views = view.findViewById(R.id.num_view_1);
            item1Photo= view.findViewById(R.id.item_1_bg);

            item2Views =  view.findViewById(R.id.num_view_2);
            item2Photo= view.findViewById(R.id.item_2_bg);
            item3Views =  view.findViewById(R.id.num_view_3);
            item3Photo= view.findViewById(R.id.item_3_bg);

            item4Views = view.findViewById(R.id.num_view_4);
            item4Photo= view.findViewById(R.id.item_4_bg);
            item5Views = view.findViewById(R.id.num_view_5);
            item5Photo= view.findViewById(R.id.item_5_bg);
            item6Views = view.findViewById(R.id.num_view_6);
            item6Photo= view.findViewById(R.id.item_6_bg);
            item7Views = view.findViewById(R.id.num_view_7);
            item7Photo= view.findViewById(R.id.item_7_bg);

            item8Views = view.findViewById(R.id.num_view_8);
            item8Photo=view.findViewById(R.id.item_8_bg);
            item9Views = view.findViewById(R.id.num_view_9);
            item9Photo=view.findViewById(R.id.item_9_bg);
            }
    }

}