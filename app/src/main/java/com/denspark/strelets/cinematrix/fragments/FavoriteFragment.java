package com.denspark.strelets.cinematrix.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.request.RequestOptions;
import com.denspark.strelets.cinematrix.R;
import com.denspark.strelets.cinematrix.glide.GlideApp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import jp.wasabeef.glide.transformations.SupportRSBlurTransformation;

public class FavoriteFragment extends Fragment {

    Button button;
    ImageView iv;
    String imageURL = "https://icebreakerideas.com/wp-content/uploads/2014/11/Depositphotos_12926816_s-663x375.jpg";
    public static final String TAG = " FavoriteFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.favorite_fragment,
                container, false);
        button = view.findViewById(R.id.show_btn);
        iv = view.findViewById(R.id.iv_tv);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(),"Hello",Toast.LENGTH_SHORT).show();
                Log.d(TAG, "button onClick()" );

                GlideApp.with(FavoriteFragment.this)
                        .load(imageURL)
                        .apply(RequestOptions.bitmapTransform(new SupportRSBlurTransformation(10,10)))
                        .into(iv);
            }
        });


        return view;
}
}
