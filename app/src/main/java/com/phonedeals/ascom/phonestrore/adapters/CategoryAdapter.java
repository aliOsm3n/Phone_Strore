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

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.MyViewHolder> {

    private List<Category> prices;
    private Context context;
    private IMyOrderClickHandler handler;
    public CategoryAdapter(Context context, List<Category> prices) {
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
        GregorianCalendar calendar = new GregorianCalendar();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

        String dateString = price.getAddTime();

        Date date = new Date();
        SimpleDateFormat myformat = new SimpleDateFormat("a");

        Date date1=null,date2=null;
        try {
             date1 = simpleDateFormat.parse(dateString);

             if (myformat.format(date).equalsIgnoreCase("AM")){
                 date2 = simpleDateFormat.parse(calendar.get(Calendar.YEAR)+"-"+(calendar.get(Calendar.MONTH)+1)+
                         "-"+calendar.get(Calendar.DAY_OF_MONTH)+" "+(calendar.get(Calendar.HOUR))+":"+
                         calendar.get(Calendar.MINUTE)+":"+calendar.get(Calendar.SECOND));
             }else {
                 date2 = simpleDateFormat.parse(calendar.get(Calendar.YEAR)+"-"+(calendar.get(Calendar.MONTH)+1)+
                         "-"+calendar.get(Calendar.DAY_OF_MONTH)+" "+(calendar.get(Calendar.HOUR)+12)+":"+
                         calendar.get(Calendar.MINUTE)+":"+calendar.get(Calendar.SECOND));
             }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        holder.time.setText(addedTime(date1,date2));
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

    public String addedTime(Date startDate, Date endDate) {
        //milliseconds
        long different = endDate.getTime() - startDate.getTime();

        System.out.println("startDate : " + startDate);
        System.out.println("endDate : "+ endDate);
        System.out.println("different : " + different);

        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;
        long monthsInMilli =(long) 2629746000.0;
        long yearsInMilli = (long) 31556952000.0;

        String f="";
        long elapsedYears = different / yearsInMilli;
        different = different % yearsInMilli;

        long elapsedMonths = different / monthsInMilli;
        different = different % monthsInMilli;

        long elapsedDays = different / daysInMilli;
        different = different % daysInMilli;

        long elapsedHours = different / hoursInMilli;
        different = different % hoursInMilli;

        long elapsedMinutes = different / minutesInMilli;
        different = different % minutesInMilli;

        long elapsedSeconds = different / secondsInMilli;

        System.out.printf(
                "%d days, %d hours, %d minutes, %d seconds%n",
                elapsedDays, elapsedHours, elapsedMinutes, elapsedSeconds);

        if (elapsedYears>1){
            f=" منذ " +elapsedYears+ " سنة ";
            return f;
        }else if (elapsedMonths>1){
            f=" منذ " +elapsedMonths+" شهر ";
            return f;

        }else if (elapsedDays>1){
            f=" منذ " +elapsedDays+" يوم ";
            return f;

        }else if (elapsedHours>1){
            f="  منذ " +elapsedHours+" ساعة ";
            return f;

        }else if (elapsedMinutes>1){
            f=" منذ " +elapsedMinutes+" دقيقة ";
            return f;

        }else {
            f=" منذ " +elapsedSeconds+" ثانية ";
            return f;

        }

    }

}