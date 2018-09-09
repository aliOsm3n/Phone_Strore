package com.phonedeals.ascom.phonestrore.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.phonedeals.ascom.phonestrore.interfaces.IMyOrderClickHandler;
import com.phonedeals.ascom.phonestrore.R;
import com.phonedeals.ascom.phonestrore.data.model.OrderPrice;
import com.phonedeals.ascom.phonestrore.util.AppUtils;
import com.phonedeals.ascom.phonestrore.util.CustomTypefaceSpan;

import java.util.List;
import java.util.Random;

public class OrderPriceAdapter extends RecyclerView.Adapter<OrderPriceAdapter.MyViewHolder> {

    private List<OrderPrice> prices;
    private Context context;
    IMyOrderClickHandler handler;
    public OrderPriceAdapter(Context context,List<OrderPrice> prices) {
        this.prices = prices;
        this.context=context;
        handler= (IMyOrderClickHandler) context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.orders, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final OrderPrice price = prices.get(position);
        holder.name.setText(price.getPhoneName());
        holder.city.setText(price.getPhoneCity());
        holder.status.setText(price.getPhoneStatus());

        Random rnd = new Random();
        int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
        holder.mView.setBackgroundColor(color);
        holder.overflow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupMenu(holder.overflow,price.getId(),price.getUserId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return prices.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name, city, status,phone;
        public ImageView overflow;
        public View mView;

        public MyViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.phone_name);
            phone = view.findViewById(R.id.phone);
            city = (TextView) view.findViewById(R.id.phone_city);
            status = (TextView) view.findViewById(R.id.phone_status);
            overflow=view.findViewById(R.id.overflow);
            mView=view.findViewById(R.id.view);
            AppUtils.applyMediumFont(name,city,status,phone);
        }
    }

    private void showPopupMenu(View view, final String id,String userId) {
        // inflate menu
        Context wrapper = new ContextThemeWrapper(context, R.style.PopupMenu);

        PopupMenu popup = new PopupMenu(wrapper, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_order_price, popup.getMenu());
        Menu menu = popup.getMenu();
        for (int i = 0; i < menu.size(); i++) {
            MenuItem mi = menu.getItem(i);
            applyFontToMenuItem(mi);
        }
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId()==R.id.order_price){
                    handler.onClick(id,userId);
                }
                return true;
            }
        });
        popup.show();
    }

    private void applyFontToMenuItem(MenuItem mi) {
        Typeface font = Typeface.createFromAsset(context.getAssets(), "GE_SS_Two_Medium.otf");
        SpannableString mNewTitle = new SpannableString(mi.getTitle());
        mNewTitle.setSpan(new CustomTypefaceSpan("" , font), 0 , mNewTitle.length(),  Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        mi.setTitle(mNewTitle);
    }
}