package com.phonedeals.ascom.phonestrore.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.phonedeals.ascom.phonestrore.interfaces.IMyOrderClickHandler;
import com.phonedeals.ascom.phonestrore.R;
import com.phonedeals.ascom.phonestrore.data.model.Category;
import com.phonedeals.ascom.phonestrore.util.AppUtils;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Random;

public class FavouriteAdapter extends RecyclerView.Adapter<FavouriteAdapter.MyViewHolder> {

    private List<Category> prices;
    private Context context;
    private IMyOrderClickHandler handler;
    public FavouriteAdapter(Context context, List<Category> prices) {
        this.prices = prices;
        this.context=context;
        handler= (IMyOrderClickHandler) context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.category_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final Category price = prices.get(position);
        holder.name.setText(price.getPhoneName());
        holder.city.setText(price.getPhoneCity());
        holder.status.setText(price.getPhoneStatus());
        holder.time.setText(price.getAddTime());
        holder.price.setText(price.getPhonePrice());

        try {
            Picasso.with(context).load(price.getPhoneImage()).into(holder.phone_image);
        }catch (Exception e){

        }
        Random rnd = new Random();
        int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
        holder.mView.setBackgroundColor(color);
        holder.phone_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handler.onClick(price.getId(),"");
            }
        });

    }

    @Override
    public int getItemCount() {
        if (prices.size()==0){
            return 0;
        }
        return prices.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name, city, status,time,price,concurency,phone;
        public ImageView phone_image;
        public View mView;

        public MyViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.phone_name);
            phone = view.findViewById(R.id.phone);
            city = view.findViewById(R.id.phone_city);
            status = view.findViewById(R.id.phone_status);
            time = view.findViewById(R.id.add_time);
            price = view.findViewById(R.id.phone_price);
            phone_image= view.findViewById(R.id.phone_bg);
            concurency = view.findViewById(R.id.price_text);
            mView= view.findViewById(R.id.view);
            AppUtils.applyMediumFont(name,city,status,time,concurency,phone);
        }
    }

}