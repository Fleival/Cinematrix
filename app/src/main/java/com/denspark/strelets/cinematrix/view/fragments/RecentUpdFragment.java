package com.denspark.strelets.cinematrix.view.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.denspark.strelets.cinematrix.App;
import com.denspark.strelets.cinematrix.R;
import com.denspark.strelets.cinematrix.adapters.HorizontalAdapterListener;
import com.denspark.strelets.cinematrix.adapters.HorizontalBigMoviesAdapter;
import com.denspark.strelets.cinematrix.adapters.HorizontalMedMoviesAdapter;
import com.denspark.strelets.cinematrix.database.entity.FilmixMovie;
import com.denspark.strelets.cinematrix.view.activities.MovieActivity;
import com.denspark.strelets.cinematrix.view.custom_views.MySwipeToRefresh;
import com.denspark.strelets.cinematrix.view_models.FactoryViewModel;
import com.denspark.strelets.cinematrix.view_models.MovieViewModel;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import dagger.android.support.AndroidSupportInjection;

public class RecentUpdFragment extends Fragment implements HorizontalAdapterListener {
    private Unbinder unbinder;
    private static final String TAG = "RecentUpdFragment";
    public static final int VIEW_MOVIE_REQUEST = 2;
    @Inject
    FactoryViewModel viewModelFactory;
    private MovieViewModel movieViewModel;

    private HorizontalBigMoviesAdapter popularFilmsPagingAdapter;
    private HorizontalMedMoviesAdapter recentFilmsPagingAdapter;
    private HorizontalMedMoviesAdapter recentTvSeriesPagingAdapter;

    @BindView(R.id.popular_films_recycler_view)
    RecyclerView popularFilmsRecyclerView;

    @BindView(R.id.recent_films_recycler_view)
    RecyclerView recentFilmsRecyclerView;

    @BindView(R.id.recent_tv_series_recycler_view)
    RecyclerView recentTvSeriesRecyclerView;

    @BindView(R.id.swipe_to_refresh_recent_layout)
    MySwipeToRefresh swipeRefreshLayout;

    private LiveData<PagedList<FilmixMovie>> popularMoviesLiveData;
    private LiveData<PagedList<FilmixMovie>> recentMoviesLiveData;
    private LiveData<PagedList<FilmixMovie>> recentTvSeriesLiveData;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.recent_updated_fragment,
                container, false);
        unbinder = ButterKnife.bind(this, view);

        popularFilmsPagingAdapter = new HorizontalBigMoviesAdapter(this);
        ((SimpleItemAnimator) popularFilmsRecyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        popularFilmsRecyclerView.setHasFixedSize(false);//// TRUE will cause item jumping!!!
        popularFilmsRecyclerView.setAdapter(popularFilmsPagingAdapter);

        recentFilmsPagingAdapter = new HorizontalMedMoviesAdapter(this);
        ((SimpleItemAnimator) recentFilmsRecyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        recentFilmsRecyclerView.setHasFixedSize(false);
        recentFilmsRecyclerView.setAdapter(recentFilmsPagingAdapter);

        recentTvSeriesPagingAdapter = new HorizontalMedMoviesAdapter(this);
        ((SimpleItemAnimator) recentTvSeriesRecyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        recentTvSeriesRecyclerView.setHasFixedSize(false);
        recentTvSeriesRecyclerView.setAdapter(recentTvSeriesPagingAdapter);
        initSwipeToRefreshLayout();
        return view;
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
        reInitViewModelData(movieViewModel);
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
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private void initSwipeToRefreshLayout() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                reInitViewModelData(movieViewModel);
//                mainAnimationHelper.playInfiniteAnimation();
//                mMainVectorAnimationHelper.showWorkingInProgressInstead();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void reInitViewModelData(MovieViewModel movieViewModel) {
        if (popularMoviesLiveData != null) {
            popularMoviesLiveData.removeObservers(this);
        }
        if (recentMoviesLiveData != null) {
            recentMoviesLiveData.removeObservers(this);
        }
        if (recentTvSeriesLiveData != null) {
            recentTvSeriesLiveData.removeObservers(this);
        }

        popularMoviesLiveData = movieViewModel.getPopularMovies();
        popularMoviesLiveData.observe(this, new Observer<PagedList<FilmixMovie>>() {
            @Override
            public void onChanged(PagedList<FilmixMovie> filmixMovies) {
                popularFilmsPagingAdapter.submitList(filmixMovies);
            }
        });

        recentMoviesLiveData = movieViewModel.getLastMovies();
        recentMoviesLiveData.observe(this, filmixMovies -> recentFilmsPagingAdapter.submitList(filmixMovies));

        recentTvSeriesLiveData = movieViewModel.getLastTvSeries();
        recentTvSeriesLiveData.observe(this, filmixMovies -> recentTvSeriesPagingAdapter.submitList(filmixMovies));
    }

}
