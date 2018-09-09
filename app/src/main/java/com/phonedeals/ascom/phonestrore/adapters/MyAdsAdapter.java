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

import com.phonedeals.ascom.phonestrore.interfaces.IMyAdsClickHandler;
import com.phonedeals.ascom.phonestrore.R;
import com.phonedeals.ascom.phonestrore.data.model.MyAds;
import com.phonedeals.ascom.phonestrore.util.AppUtils;
import com.phonedeals.ascom.phonestrore.util.CustomTypefaceSpan;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Random;

public class MyAdsAdapter extends RecyclerView.Adapter<MyAdsAdapter.MyViewHolder> {

    private List<MyAds> prices;
    private Context context;
    private IMyAdsClickHandler handler;
    public MyAdsAdapter(Context context, List<MyAds> prices) {
        this.prices = prices;
        this.context=context;
        handler= (IMyAdsClickHandler) context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.phone, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final MyAds price = prices.get(position);
        holder.name.setText(price.getPhoneName());
        holder.city.setText(price.getPhoneCity());
        holder.status.setText(price.getPhoneStatus());
        holder.numberView.setText(price.getNumberView());
        try {
            Picasso.with(context).load(price.getPhoneImage()).into(holder.phone_image);
        }catch (Exception e){

        }
        Random rnd = new Random();
        int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
        holder.mView.setBackgroundColor(color);
        holder.overflow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupMenu(holder.overflow,price.getId(),price.getVisible());
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
        public TextView name, city, status,numberView,phone;
        public ImageView overflow,phone_image;
        public View mView;

        public MyViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.phone_name);
            phone = view.findViewById(R.id.phone);
            city = view.findViewById(R.id.phone_city);
            status = view.findViewById(R.id.phone_status);
            numberView = view.findViewById(R.id.num_view);
            overflow=view.findViewById(R.id.overflow);
            phone_image=view.findViewById(R.id.phone_bg);
            mView=view.findViewById(R.id.view);
            AppUtils.applyMediumFont(name,city,status,phone);
        }
    }

    private void showPopupMenu(View view, final String id, final String visible) {
        // inflate menu
        Context wrapper = new ContextThemeWrapper(context, R.style.PopupMenu);

        PopupMenu popup = new PopupMenu(wrapper, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_my_ads, popup.getMenu());
        MenuItem visibleMenuItem = popup.getMenu().findItem(R.id.stop);
        if (visible.equals("1")){
            visibleMenuItem.setTitle(context.getString(R.string.stop));
        }else {
            visibleMenuItem.setTitle(R.string.turn_on);
        }


        Menu menu = popup.getMenu();
        for (int i = 0; i < menu.size(); i++) {
            MenuItem mi = menu.getItem(i);
            applyFontToMenuItem(mi);
        }
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId()==R.id.edit){
                    handler.edit(id);
                }else if (item.getItemId()==R.id.messages){
                    handler.messages(id);
                }else if (item.getItemId()==R.id.stop){
                    handler.stop(id,visible);
                }else {
                    handler.delete(id);
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