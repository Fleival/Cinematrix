package com.denspark.strelets.cinematrix.view.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.airbnb.lottie.LottieAnimationView;
import com.denspark.strelets.cinematrix.R;

public class CategoryFragment extends Fragment {
    LottieAnimationView thumb_up;
    LottieAnimationView thumb_down;
    LottieAnimationView toggle;
    int flag = 0;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.category_fragment,
                container, false);
        thumb_up = view.findViewById(R.id.lav_thumbUp);
        thumb_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                thumb_down.setProgress(0);
                thumb_down.pauseAnimation();
                thumb_up.playAnimation();
                Toast.makeText(getActivity(), "Cheers!!", Toast.LENGTH_SHORT).show();
                //---- Your code here------
            }
        });

        thumb_down = view.findViewById(R.id.lav_thumbDown);
        thumb_down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                thumb_up.setProgress(0);
                thumb_up.pauseAnimation();
                thumb_down.playAnimation();
                Toast.makeText(getActivity(), "Boo!!", Toast.LENGTH_SHORT).show();
                //---- Your code here------
            }
        });

        toggle = view.findViewById(R.id.lav_search_to_cross);
        toggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeState();
            }
        });
        return view;
    }
    private void changeState() {
        if (flag == 0) {
            toggle.setSpeed(1.75f);
            toggle.playAnimation();
            flag = 1;
        }else {
            toggle.setSpeed(-1.75f);
            toggle.playAnimation();
            flag = 0;
        }

    }
}
