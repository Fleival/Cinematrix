package com.denspark.strelets.cinematrix.repository;

import androidx.annotation.NonNull;
import androidx.paging.PageKeyedDataSource;

import com.denspark.strelets.cinematrix.api.MovieWebService;
import com.denspark.strelets.cinematrix.database.entity.FilmixMovie;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public abstract class FilmsServerDataSource extends PageKeyedDataSource<Integer, FilmixMovie> {
    MovieWebService mWebService;
    int page;
    int maxResult;

    public FilmsServerDataSource(MovieWebService webService, int maxResult) {
        mWebService = webService;
        this.page = 1;
        this.maxResult = maxResult;
    }

    @Override
    public void loadInitial(@NonNull LoadInitialParams<Integer> params, @NonNull LoadInitialCallback<Integer, FilmixMovie> callback) {
        Call<List<FilmixMovie>> data = getListCallInitial();
        data.enqueue(new Callback<List<FilmixMovie>>() {
            @Override
            public void onResponse(Call<List<FilmixMovie>> call, Response<List<FilmixMovie>> response) {
                List<FilmixMovie> movieList = response.body();
                callback.onResult(movieList, null, page + 1);
            }

            @Override
            public void onFailure(Call<List<FilmixMovie>> call, Throwable t) {

            }
        });

    }


    @Override
    public void loadBefore(@NonNull LoadParams<Integer> params, @NonNull LoadCallback<Integer, FilmixMovie> callback) {

    }

    @Override
    public void loadAfter(@NonNull LoadParams<Integer> params, @NonNull LoadCallback<Integer, FilmixMovie> callback) {
        Call<List<FilmixMovie>> data = getListCallAfter(params);
        data.enqueue(new Callback<List<FilmixMovie>>() {
            @Override
            public void onResponse(Call<List<FilmixMovie>> call, Response<List<FilmixMovie>> response) {
                List<FilmixMovie> movieList = response.body();
                callback.onResult(movieList, (params.key) + 1);
            }

            @Override
            public void onFailure(Call<List<FilmixMovie>> call, Throwable t) {

            }
        });
    }

    protected abstract Call<List<FilmixMovie>> getListCallInitial();
    protected abstract Call<List<FilmixMovie>> getListCallAfter(@NonNull LoadParams<Integer> params);
}
