package com.denspark.strelets.cinematrix.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.denspark.strelets.cinematrix.R;

public class CategoryFragment extends Fragment {
    Button favBtn;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.category_fragment,
                container, false);
        favBtn = view.findViewById(R.id.fav_add_btn);
        favBtn.setOnClickListener(favClick);
        return view;
    }

    View.OnClickListener favClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            favBtn.setSelected(!favBtn.isSelected());
            if (favBtn.isSelected()){
                Toast.makeText(getActivity(), "Movie added to favorite", Toast.LENGTH_SHORT).show();
            }else if (!favBtn.isSelected()){
                Toast.makeText(getActivity(), "Movie removed from favorite", Toast.LENGTH_SHORT).show();
            }
        }
    };
}
