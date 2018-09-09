package com.phonedeals.ascom.phonestrore.ui.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.phonedeals.ascom.phonestrore.R;
import com.phonedeals.ascom.phonestrore.interfaces.OnImageClickListener;
import com.squareup.picasso.Picasso;


public class PreviewFragment extends Fragment {
    private String page;

    private OnImageClickListener onImageClickListener;

    public static PreviewFragment newInstance(String page) {
        PreviewFragment fragmentFirst = new PreviewFragment();
        Bundle args = new Bundle();
        args.putString("someInt", page);
        fragmentFirst.setArguments(args);

        return fragmentFirst;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        page = getArguments().getString("someInt", "");
        onImageClickListener=(OnImageClickListener)getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_preview, container, false);
        ImageView tvLabel = view.findViewById(R.id.images);
         try {
             Picasso.with(getContext()).load(page).into(tvLabel);
         }catch (Exception e){

         }

         tvLabel.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 onImageClickListener.onClick();
             }
         });


        return view;
    }
}