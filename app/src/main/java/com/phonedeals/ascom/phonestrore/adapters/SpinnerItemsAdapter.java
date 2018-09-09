package com.phonedeals.ascom.phonestrore.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.phonedeals.ascom.phonestrore.R;
import com.phonedeals.ascom.phonestrore.util.AppUtils;

import java.util.List;

public class SpinnerItemsAdapter extends BaseAdapter {

    private List<String> items ;

    public SpinnerItemsAdapter(List<String> items){
        this.items=items;
    }
    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int i) {
        return items.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }


    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        TextView textView;
        if (view == null) {
            view = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.spinner_item_layout, viewGroup, false);
            textView = (TextView) view;
            AppUtils.applyMediumFont(textView);
        } else {
            textView = (TextView) view;
        }
        textView.setText(getItem(i).toString());
        return textView;
    }
}
