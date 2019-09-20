package com.denspark.strelets.cinematrix.view.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.denspark.strelets.cinematrix.App;
import com.denspark.strelets.cinematrix.R;
import com.denspark.strelets.cinematrix.adapters.MoviePagingAdapter;
import com.denspark.strelets.cinematrix.database.entity.FilmixMovie;
import com.denspark.strelets.cinematrix.view.activities.MovieActivity;
import com.denspark.strelets.cinematrix.view_models.FactoryViewModel;
import com.denspark.strelets.cinematrix.view_models.MovieViewModel;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import dagger.android.support.AndroidSupportInjection;

public class FilmsFragment extends Fragment implements
        MoviePagingAdapter.MoviePagingAdapterListener {
    public static final int VIEW_MOVIE_REQUEST = 2;
    private Unbinder unbinder;


    private static final String TAG = "FilmsFragment";

    @Inject
    FactoryViewModel viewModelFactory;

    private MovieViewModel movieViewModel;
    private MoviePagingAdapter pagingAdapter;

    @BindView(R.id.recycler_view_films)
    RecyclerView recyclerViewFilms;

    @BindView(R.id.retry_refresh)
    Button retryBtn;

    @BindView(R.id.swipe_to_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;

    LinearLayout llBottomSheet;
    BottomSheetBehavior bottomSheetBehavior;



    private LiveData<PagedList<FilmixMovie>> recyclerLiveData;

    public FilmsFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.films_fragment,
                container, false);
        unbinder = ButterKnife.bind(this, view);

        pagingAdapter = new MoviePagingAdapter(this);// TODO: 23.05.2019 make OnClickListener in adapter
        ((SimpleItemAnimator) recyclerViewFilms.getItemAnimator()).setSupportsChangeAnimations(false);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getActivity(), 2);

        recyclerViewFilms.setHasFixedSize(false);//// TRUE will cause item jumping!!!
        recyclerViewFilms.setLayoutManager(layoutManager);
        recyclerViewFilms.setAdapter(pagingAdapter);

        llBottomSheet = view.findViewById(R.id.bottom_sheet);
        bottomSheetBehavior = BottomSheetBehavior.from(llBottomSheet);

        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        bottomSheetBehavior.setSkipCollapsed(true);

        retryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(App.context, "attempt to receive data", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "retryDataTransmission: " + movieViewModel.retryDataTransmission());
            }
        });
        recyclerLiveData = null;
        initSwipeToRefreshLayout();
        return view;
    }


    private void initSwipeToRefreshLayout() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                reInitViewModelData();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.configureDagger();
        this.configureViewModel();
    }

    private void configureDagger() {
        AndroidSupportInjection.inject(this);
    }


    private void configureViewModel() {
        movieViewModel = new ViewModelProvider(getActivity(), viewModelFactory)
                .get(MovieViewModel.class);


        recyclerLiveData = movieViewModel.getAllMoviesRemote();
        recyclerLiveData.observe(this, new Observer<PagedList<FilmixMovie>>() {
            @Override
            public void onChanged(PagedList<FilmixMovie> filmixMovies) {
                pagingAdapter.submitList(filmixMovies);
            }
        });


    }

    private void reInitViewModelData() {
        if (recyclerLiveData != null) {
            recyclerLiveData.removeObservers(this);
        }
        recyclerLiveData = movieViewModel.getAllMoviesRemote();
    }

    @Override
    public void onMovieClick(FilmixMovie movie) {
        Log.d("TAG", "Movie clicked id: " + movie.getId());
        Intent intent = new Intent(App.context, MovieActivity.class);

        intent.putExtra(MovieActivity.EXTRA_ONLINE_MODE, movieViewModel.isOnlineMode());
        intent.putExtra(MovieActivity.EXTRA_ID, movie.getId());
        intent.putExtra(MovieActivity.EXTRA_POSTER_URL, movie.getFilmPosterUrl());
        intent.putExtra(MovieActivity.EXTRA_TITLE, movie.getName());

        startActivityForResult(intent, VIEW_MOVIE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
