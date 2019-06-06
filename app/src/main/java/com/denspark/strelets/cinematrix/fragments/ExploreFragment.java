package com.denspark.strelets.cinematrix.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import com.denspark.strelets.cinematrix.App;
import com.denspark.strelets.cinematrix.R;
import com.denspark.strelets.cinematrix.activities.MainActivity;
import com.denspark.strelets.cinematrix.activities.MovieActivity;
import com.denspark.strelets.cinematrix.adapters.MovieAdapter;
import com.denspark.strelets.cinematrix.adapters.MoviePagingAdapter;
import com.denspark.strelets.cinematrix.database.entity.FilmixMovie;
import com.denspark.strelets.cinematrix.view_models.FactoryViewModel;
import com.denspark.strelets.cinematrix.view_models.MovieViewModel;
import dagger.android.support.AndroidSupportInjection;

import javax.inject.Inject;

public class ExploreFragment extends Fragment implements MoviePagingAdapter.MoviePagingAdapterListener {
    public static final int VIEW_MOVIE_REQUEST = 1;
    private Unbinder unbinder;


    private static final String TAG = "ExploreFragment";

    @Inject
    FactoryViewModel viewModelFactory;

    private MovieViewModel movieViewModel;
    private MovieAdapter adapter;
    private MoviePagingAdapter pagingAdapter;

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    @BindView(R.id.clear_data)
    Button addBtn;

    @BindView(R.id.test_data)
    Button testBtn;

    @BindView(R.id.test_db)
    Button testDbBtn;

    @BindView(R.id.swipe_to_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;

    public ExploreFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.explore_fragment,
                container, false);
        unbinder = ButterKnife.bind(this, view);

        adapter = new MovieAdapter();
        pagingAdapter = new MoviePagingAdapter(this);// TODO: 23.05.2019 make OnClickListener in adapter

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getActivity(), 2);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(pagingAdapter);
//        initScrollListener();
        initSwipeToRefreshLayout();

        return view;
    }


    @OnClick(R.id.clear_data)
    public void clearData() {
        movieViewModel.clearMovieData();
    }

    @OnClick(R.id.test_data)
    public void testApi() {
        movieViewModel.testWebService();
    }

    @OnClick(R.id.test_db)
    public void testDb() {
        movieViewModel.testDB();
    }


    public void loadMore() {

        movieViewModel.addSomeTestDataFromServer();
    }

    private void initSwipeToRefreshLayout(){
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
        movieViewModel = ViewModelProviders.of(this, viewModelFactory)
                .get(MovieViewModel.class);
//        movieViewModel.init();
//        movieViewModel.getAllMovies().observe(this, movies -> adapter.submitList(movies));

        movieViewModel.getMovies().observe(this, new Observer<PagedList<FilmixMovie>>() {
            @Override
            public void onChanged(@Nullable PagedList<FilmixMovie> movies) {
                if (movies == null || movies.size() == 0) {
                    // add data when data is empty
                    movieViewModel.insertData();
                }

                pagingAdapter.submitList(movies);
            }
        });
    }

    private void reInitViewModelData(){
        movieViewModel.reInitTestData();
    }

    @Override public void onMovieClick(FilmixMovie movie) {
        Log.d("TAG", "Movie clicked");
        Toast.makeText(App.context, "Movie clicked id: " + movie.getId(), Toast.LENGTH_LONG).show();
        Intent intent = new Intent(App.context, MovieActivity.class);
        intent.putExtra(MovieActivity.EXTRA_ID, movie.getId());
        intent.putExtra(MovieActivity.EXTRA_POSTER_URL, movie.getFilmPosterUrl());
        intent.putExtra(MovieActivity.EXTRA_TITLE, movie.getName());
        startActivityForResult(intent, VIEW_MOVIE_REQUEST);
    }

    @Override public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
