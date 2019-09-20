package com.denspark.strelets.cinematrix.view.activities;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.paging.PagedList;

import com.airbnb.lottie.LottieAnimationView;
import com.denspark.strelets.cinematrix.R;
import com.denspark.strelets.cinematrix.adapters.CheckableSpinnerAdapter;
import com.denspark.strelets.cinematrix.database.entity.Country;
import com.denspark.strelets.cinematrix.database.entity.FilmixMovie;
import com.denspark.strelets.cinematrix.database.entity.Genre;
import com.denspark.strelets.cinematrix.view.fragments.FilmsFragment;
import com.denspark.strelets.cinematrix.view.fragments.ProfileFragment;
import com.denspark.strelets.cinematrix.view.fragments.RecentUpdFragment;
import com.denspark.strelets.cinematrix.view.fragments.TvSeriesFragment;
import com.denspark.strelets.cinematrix.view_models.FactoryViewModel;
import com.denspark.strelets.cinematrix.view_models.MovieViewModel;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import dagger.android.AndroidInjection;
import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasAndroidInjector;

public class MainActivity extends AppCompatActivity implements HasAndroidInjector {

    private static final String TAG = "MainActivity";

    @Inject
    DispatchingAndroidInjector<Object> dispatchingAndroidInjector;

    private static final int RECENT_BTN = 0;
    private static final int FILMS_BTN = 1;
    private static final int TV_SERIES_BTN = 2;
    private static final int PROF_BTN = 3;

    private static final int SEARCH_ICON_SHOW_MAGNIFIER = 0;
    private static final int SEARCH_ICON_SHOW_CROSS = 1;

    boolean isUp;
    int flag = 0;


    private List<Drawable> notchIconsList = new ArrayList<>();

    @Inject
    FactoryViewModel viewModelFactory;

    private MovieViewModel movieViewModel;

    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;


    @BindView(R.id.filter_container)
    LinearLayout filterLinearLayout;

    private ConstraintLayout.LayoutParams navNotchParams;
    @BindView(R.id.nav_notch)
    ImageView navNotch;

    @BindView(R.id.lav_search_to_cross_white)
    LottieAnimationView searchAnimIcon;

    @BindView(R.id.filter_btn)
    ImageView filterBtn;

    @BindView(R.id.content_fragment_placeholder)
    FrameLayout fragmentContainer;

    @BindViews({R.id.updates_btn, R.id.films_btn, R.id.tv_series_btn, R.id.prof_btn})
    List<Button> navButtons;

    //    @BindView(R.id.filter_spinner_year)
//    Spinner spinnerYear;
    @BindView(R.id.filter_spinner_year)
    Spinner spinnerYear;
    @BindView(R.id.filter_spinner_genre)
    Spinner spinnerGenre;
    @BindView(R.id.filter_spinner_country)
    Spinner spinnerCountry;
    @BindView(R.id.apply_filter_btn)
    Button applyFilterBtn;
    @BindView(R.id.clear_filter_btn)
    Button clearFilterBtn;

    @BindView(R.id.search_et)
    EditText searchEtField;

    private Animation bottomNavAnimation;

    List<CheckableSpinnerAdapter.SpinnerItem<Genre>> spinner_genre_items = new ArrayList<>();
    CheckableSpinnerAdapter<Genre> genreSpinnerAdapter;
    ArrayAdapter<String> countryArrayAdapter;
    String[] countryNames;
    List<Genre> selected_genre_items = new ArrayList<>();
    String selected_year_item;
    String selected_country_item;

    private Fragment updatesFragment;
    private Fragment filmsFragment;
    private Fragment tvSeriesFragment;
    private Fragment profileFragment;
    LiveData<PagedList<FilmixMovie>> filterLiveData;

    List<Fragment> fragments = new ArrayList<>();


    @Override
    public AndroidInjector<Object> androidInjector() {
        return dispatchingAndroidInjector;
    }

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

        navButtons.get(RECENT_BTN).setSelected(true);
        checkPressedButtonStyle();

        bottomNavAnimation = AnimationUtils.loadAnimation(this, R.anim.bounce);


        searchAnimIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (flag == SEARCH_ICON_SHOW_CROSS) {
                    searchEtField.setText("");
                    searchEtField.clearFocus();
                    closeKeyboard();
                    changeState();
                }
            }
        });

        filterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterBtn.setSelected((!filterBtn.isSelected()));
                onSlideViewButtonClick(filterLinearLayout);
            }
        });

        navButtons.get(RECENT_BTN).setOnClickListener(clickNavButton(0));
        navButtons.get(FILMS_BTN).setOnClickListener(clickNavButton(1));
        navButtons.get(TV_SERIES_BTN).setOnClickListener(clickNavButton(2));
        navButtons.get(PROF_BTN).setOnClickListener(clickNavButton(3));


        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this,
                drawer,
                toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        Objects.requireNonNull(getSupportActionBar()).setHomeAsUpIndicator(R.drawable.menu);

        configureViewModel();


        isUp = true;
        TvSeriesFragment tvFr = (TvSeriesFragment) tvSeriesFragment;

        try {
            Field popup = Spinner.class.getDeclaredField("mPopup");
            popup.setAccessible(true);

            // Get private mPopup member variable and try cast to ListPopupWindow
            android.widget.ListPopupWindow popupWindow = (android.widget.ListPopupWindow) popup.get(spinnerGenre);

            // Set popupWindow height to 500px
            popupWindow.setHeight(500);
        } catch (NoClassDefFoundError | ClassCastException | NoSuchFieldException | IllegalAccessException e) {
            // silently fail...
        }

        applyFilterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: applyFilterBtn selected_genre_items = " + selected_genre_items);
//                movieViewModel.setGenreFilter(selected_genre_items);
//                filterLiveData = movieViewModel.getMoviesForGenrePagged();
//                filterLiveData.observe(MainActivity.this, new Observer<PagedList<FilmixMovie>>() {
//                    @Override
//                    public void onChanged(PagedList<FilmixMovie> movies) {
//                        Log.d(TAG, "onChanged: filtered films " + movies);
//                        filmsFragment.setPagingAdapterData(movies);
//                    }
//                });

                if (filterLiveData != null) {
                    filterLiveData.removeObservers(MainActivity.this);
                    tvFr.removeObservers();
                }
                filterLiveData = movieViewModel.searchFilteredMovies(null, selected_year_item, selected_country_item, selected_genre_items);
                tvFr.setOnlineMode(true);
                filterLiveData.observe(MainActivity.this, new Observer<PagedList<FilmixMovie>>() {
                    @Override
                    public void onChanged(PagedList<FilmixMovie> movies) {
                        Log.d(TAG, "onChanged: filtered films " + movies);
                        tvFr.setPagingAdapterData(movies);
                    }
                });
                filterBtn.setSelected((!filterBtn.isSelected()));
                onSlideViewButtonClick(filterLinearLayout);
            }
        });
        clearFilterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (filterLiveData != null) {
//                    filterLiveData.removeObservers(MainActivity.this);
//                    filmsFragment.setDefaultObserver(0);
//                }
                if (filterLiveData != null) {
                    filterLiveData.removeObservers(MainActivity.this);
                }
                tvFr.setOnlineMode(false);
                tvFr.clearAdapterData();
                tvFr.setDefaultObserver(0);
                filterBtn.setSelected((!filterBtn.isSelected()));
                onSlideViewButtonClick(filterLinearLayout);
            }
        });

        searchEtField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (count == 0) {
                    flag = SEARCH_ICON_SHOW_MAGNIFIER;
                    changeState();
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count > 2) {
                    if (filterLiveData != null) {
                        filterLiveData.removeObservers(MainActivity.this);
                        tvFr.removeObservers();
                    }

                    filterLiveData = movieViewModel.searchFilteredMovies(s.toString(),
                            selected_year_item,
                            selected_country_item,
                            selected_genre_items);

                    tvFr.setOnlineMode(true);
                    filterLiveData.observe(MainActivity.this, new Observer<PagedList<FilmixMovie>>() {
                        @Override
                        public void onChanged(PagedList<FilmixMovie> movies) {
                            Log.d(TAG, "onChanged: filtered films " + movies);
                            tvFr.setPagingAdapterData(movies);
                        }
                    });
                } else {
                    if (filterLiveData != null) {
                        filterLiveData.removeObservers(MainActivity.this);
                    }
                    tvFr.setOnlineMode(false);
                    tvFr.clearAdapterData();
                    tvFr.setDefaultObserver(0);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void closeKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
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
            playBottomNavAnimation(navNotch);

            switch (pos) {
                case 0:
                    displayFragment(RecentUpdFragment.class);
                    break;
                case 1:
                    displayFragment(FilmsFragment.class);
                    break;
                case 2:
                    displayFragment(TvSeriesFragment.class);
                    break;
                case 3:
                    displayFragment(ProfileFragment.class);
                    break;
            }
        };
    }

    private void playBottomNavAnimation(View v) {
        v.clearAnimation();
        v.startAnimation(bottomNavAnimation);
    }

    private void slideUp(View view) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(
                view,
                "translationY",
                -view.getHeight());

        animator.setDuration(500);
        animator.start();
        isUp = true;
    }

    private void slideDown(View view) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(
                view,
                "translationY",
                (view.getHeight() - 2));
        animator.setDuration(500);
        animator.start();
        isUp = false;
    }

    private void onSlideViewButtonClick(View view) {
        if (isUp) {
            slideDown(view);
        } else {
            slideUp(view);
        }
    }

    private void showFragment(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            fragments.clear();
            updatesFragment = new RecentUpdFragment();
            fragments.add(updatesFragment);
            filmsFragment = new FilmsFragment();
            fragments.add(filmsFragment);
            tvSeriesFragment = new TvSeriesFragment();
            fragments.add(tvSeriesFragment);
            profileFragment = new ProfileFragment();
            fragments.add(profileFragment);
        }

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        //Show main fragment
        ft.add(R.id.content_fragment_placeholder, updatesFragment);
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

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void configureViewModel() {
        movieViewModel = new ViewModelProvider(this, viewModelFactory)
                .get(MovieViewModel.class);

        movieViewModel.loadAllGenres();
        movieViewModel.loadCounties();

        initSpinnerGenre();
        movieViewModel.gelAllGenres().observe(this, new Observer<List<Genre>>() {
            @Override
            public void onChanged(List<Genre> genres) {
                if (genres != null) {
                    updateSpinnerGenre(genres);
                }
            }
        });
        movieViewModel.getCountryLiveData().observe(this, new Observer<List<Country>>() {
            @Override
            public void onChanged(List<Country> countries) {
                if (countries != null) {
                    updateSpinnerCountry(countries);
                }
            }
        });
        initSpinnerYear();

        movieViewModel.updateStateOfRemoteDB();
    }

    private void initSpinnerYear() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        List<String> yearsList = new ArrayList<>();
        yearsList.add("Все года");
        for (int i = year; i >= 1920; i--) {
            yearsList.add(String.valueOf(i));
        }
        String[] years = yearsList.toArray(new String[yearsList.size()]);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.simple_spinner_item, years);
        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        spinnerYear.setAdapter(adapter);

        spinnerYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {


            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position < 1) {
                    selected_year_item = null;
                } else {
                    selected_year_item = years[position];
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selected_year_item = null;
            }
        });
    }

    private void initSpinnerGenre() {
        String headerText = "Все жанры";
        genreSpinnerAdapter = new CheckableSpinnerAdapter<>(this, headerText, "Все жанры", spinner_genre_items, selected_genre_items);
        spinnerGenre.setAdapter(genreSpinnerAdapter);
    }

    private void updateSpinnerGenre(List<Genre> genres) {
        for (Genre o : genres) {
            CheckableSpinnerAdapter.SpinnerItem<Genre> spinnerItem = new CheckableSpinnerAdapter.SpinnerItem<>(o, o.getName());
            if (!spinner_genre_items.contains(spinnerItem)) {
                spinner_genre_items.add(spinnerItem);
            }
            genreSpinnerAdapter.notifyDataSetChanged();
        }
    }

    private void updateSpinnerCountry(List<Country> countries) {
        countryNames = new String[countries.size() + 1];
        countryNames[0] = "Все страны";
        for (Country c : countries) {
            int index = countries.indexOf(c);
            countryNames[index + 1] = c.getName();
        }
        countryArrayAdapter = new ArrayAdapter<>(this, R.layout.simple_spinner_item, countryNames);
        countryArrayAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        spinnerCountry.setAdapter(countryArrayAdapter);

        spinnerCountry.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {


            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position < 1) {
                    selected_country_item = null;
                }
                else {
                    selected_country_item = countryNames[position];
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selected_country_item = null;
            }
        });
        countryArrayAdapter.notifyDataSetChanged();
    }

    private void changeState() {
        if (flag == SEARCH_ICON_SHOW_MAGNIFIER) {
            searchAnimIcon.setSpeed(1.75f);
            searchAnimIcon.playAnimation();
            flag = SEARCH_ICON_SHOW_CROSS;
        } else {
            searchAnimIcon.setSpeed(-1.75f);
            searchAnimIcon.playAnimation();
            flag = SEARCH_ICON_SHOW_MAGNIFIER;
        }

    }


}
