package com.phonedeals.ascom.phonestrore.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


import com.phonedeals.ascom.phonestrore.interfaces.OnDeleteAdsImageItemClick;
import com.phonedeals.ascom.phonestrore.R;
import com.phonedeals.ascom.phonestrore.util.AppUtils;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class AdsImagesAdapter extends RecyclerView.Adapter<AdsImagesAdapter.ViewHolder> implements View.OnClickListener {

    private OnDeleteAdsImageItemClick onDeleteAdsImageItemClick;
    private List<String> images = new ArrayList<>();
    private int width, height;


    public AdsImagesAdapter(OnDeleteAdsImageItemClick onDeleteAdsImageItemClick) {
        this.onDeleteAdsImageItemClick = onDeleteAdsImageItemClick;
    }


    public void addImage(String imageUrl) {
        images.add(imageUrl);
        notifyDataSetChanged();
    }

    public void deleteImage(String imageUrl) {
        images.remove(imageUrl);
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.add_ads_image, parent, false);
        ViewHolder viewHolder = new ViewHolder(itemView);
        viewHolder.deleteImage.setOnClickListener(this);
        width = (int) AppUtils.pxFromDp(parent.getContext(), 100);
        height = (int) AppUtils.pxFromDp(parent.getContext(), 80);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String s = images.get(position);
        if (s.startsWith("http")) {
            // images from server.
            Picasso.with(holder.itemView.getContext()).load(s).resize(width, height)
                    .into(holder.adsImage);
        } else {
            // images from SDCARD,
            Picasso.with(holder.itemView.getContext()).load(new File(s)).resize(width, height)
                    .into(holder.adsImage);
        }

        holder.deleteImage.setTag(R.id.item, s);
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    @Override
    public void onClick(View view) {
        String imageUrl = (String) view.getTag(R.id.item);
        onDeleteAdsImageItemClick.deleteImageItemClick(imageUrl);
    }

    public void addImages(List<String> images) {
        this.images.addAll(images);
        notifyDataSetChanged();
    }

    public void removeImage(String imageUrl) {
        this.images.remove(imageUrl);
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView deleteImage, adsImage;

        ViewHolder(View itemView) {
            super(itemView);
            deleteImage = itemView.findViewById(R.id.delete_image);
            adsImage = itemView.findViewById(R.id.ads_image);
        }
    }
}
