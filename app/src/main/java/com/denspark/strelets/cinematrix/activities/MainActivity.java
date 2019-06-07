package com.denspark.strelets.cinematrix.activities;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import com.denspark.strelets.cinematrix.R;
import com.denspark.strelets.cinematrix.fragments.CategoryFragment;
import com.denspark.strelets.cinematrix.fragments.ExploreFragment;
import com.denspark.strelets.cinematrix.fragments.FavoriteFragment;
import dagger.android.AndroidInjection;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.HasSupportFragmentInjector;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements HasSupportFragmentInjector {

    private static final String TAG = "MainActivity";

    @Inject
    DispatchingAndroidInjector<Fragment> dispatchingAndroidInjector;

    private static final int EXPLORE_BTN = 0;
    private static final int CAT_BTN = 1;
    private static final int FAV_BTN = 2;
    private static final int PROF_BTN = 3;


    private List<Drawable> notchIconsList = new ArrayList<>();

    private ConstraintLayout.LayoutParams navNotchParams;
    @BindView(R.id.nav_notch)
    ImageView navNotch;
    @BindView(R.id.search_btn)
    ImageView searchBtn;
    @BindView(R.id.filter_btn)
    ImageView filterBtn;

    @BindViews({R.id.explore_btn, R.id.cat_btn, R.id.fav_btn, R.id.prof_btn})
    List<Button> navButtons;

    private Animation animation;

    private Fragment exploreFragment;
    private Fragment favoriteFragment;

    List<Fragment> fragments = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ButterKnife.bind(this);

        this.configureDagger();
        this.showFragment(savedInstanceState);


        navNotchParams = (ConstraintLayout.LayoutParams) navNotch.getLayoutParams();
        LayerDrawable navNotchBgWithIco = (LayerDrawable) getResources().getDrawable(R.drawable.nav_notch_with_ico);
        Drawable notchIconExplore = navNotchBgWithIco.findDrawableByLayerId(R.id.notch_ico_sel_1);
        notchIconsList.add(notchIconExplore);
        Drawable notchIconCategory = navNotchBgWithIco.findDrawableByLayerId(R.id.notch_ico_sel_2);
        notchIconsList.add(notchIconCategory);
        Drawable notchIconFavorite = navNotchBgWithIco.findDrawableByLayerId(R.id.notch_ico_sel_3);
        notchIconsList.add(notchIconFavorite);
        Drawable notchIconProfile = navNotchBgWithIco.findDrawableByLayerId(R.id.notch_ico_sel_4);
        notchIconsList.add(notchIconProfile);

        navButtons.get(EXPLORE_BTN).setSelected(true);
        checkPressedButtonStyle();

        animation = AnimationUtils.loadAnimation(this, R.anim.bounce);


        searchBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View arg0, MotionEvent arg1) {
                searchBtn.setSelected(arg1.getAction() == MotionEvent.ACTION_DOWN);
                return true;
            }
        });

        filterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterBtn.setSelected((!filterBtn.isSelected()));
            }
        });

        navButtons.get(EXPLORE_BTN).setOnClickListener(clickNavButton(0));
        navButtons.get(CAT_BTN).setOnClickListener(clickNavButton(1));
        navButtons.get(FAV_BTN).setOnClickListener(clickNavButton(2));
        navButtons.get(PROF_BTN).setOnClickListener(clickNavButton(3));


    }

    @Override
    public DispatchingAndroidInjector<Fragment> supportFragmentInjector() {
        return dispatchingAndroidInjector;
    }


    private View.OnClickListener clickNavButton(int pos) {
        return v -> {
            for (int i = 0; i < navButtons.size(); i++) {
                if (i == pos) {
                    moveNotchTo(navButtons.get(i));
                    navButtons.get(i).setSelected(true);
                } else {
                    navButtons.get(i).setSelected(false);
                }
            }
            checkPressedButtonStyle();
            playAnimEffect(navNotch);

            switch (pos) {
                case 0:
                    displayFragment(ExploreFragment.class);
                    break;
                case 1:
                    displayFragment(CategoryFragment.class);
                    break;
                case 2:
                    displayFragment(FavoriteFragment.class);
                    break;
                case 3:
                    break;
            }
        };
    }

    private void playAnimEffect(View v) {
        v.clearAnimation();
        v.startAnimation(animation);
    }

    private void showFragment(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            fragments.clear();
            exploreFragment = new ExploreFragment();
            fragments.add(exploreFragment);
            Fragment categoryFragment = new CategoryFragment();
            fragments.add(categoryFragment);
            favoriteFragment = new FavoriteFragment();
            fragments.add(favoriteFragment);
        }

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        //Show main fragment
        ft.add(R.id.content_fragment_placeholder, exploreFragment);
        // Complete the changes added above
        ft.commit();
    }

    private void moveNotchTo(View v) {
        navNotchParams.startToStart = v.getId();
        navNotchParams.endToEnd = v.getId();
        navNotch.setLayoutParams(navNotchParams);
        navNotch.requestLayout();
    }

    private void checkPressedButtonStyle() {
        for (int i = 0; i < navButtons.size(); i++) {
            Button b = navButtons.get(i);
            if (b.isSelected()) {
                b.setTextColor(ContextCompat.getColor(this, R.color.colorRedAccent));
                notchIconsList.get(i).setAlpha(255);
            } else if (!b.isSelected()) {
                b.setTextColor(ContextCompat.getColor(this, R.color.white));
                notchIconsList.get(i).setAlpha(0);
            }
        }
    }

    private <T extends Fragment> void displayFragment(Class<T> clazz) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

        for (Fragment f : fragments) {

            if (f.getClass().isAssignableFrom(clazz)) {
                if (f.isAdded()) { // if the fragment is already in container
                    ft.show(f);
                    Log.d(TAG, "displayFragment: " + f.getClass().getName());
                } else { // fragment needs to be added to frame container
                    ft.add(R.id.content_fragment_placeholder, f);
                }
            } else {
                // Hide other fragments
                if (f.isAdded()) {
                    ft.hide(f);
                }
            }
        }
        ft.commit();
    }

    private void configureDagger() {
        AndroidInjection.inject(this);
    }
}