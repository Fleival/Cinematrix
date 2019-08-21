package com.denspark.strelets.cinematrix.repository;

import androidx.annotation.NonNull;
import androidx.paging.PageKeyedDataSource;

import com.denspark.strelets.cinematrix.api.MovieWebService;
import com.denspark.strelets.cinematrix.database.entity.FilmixMovie;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FilmsSearchDataSource extends PageKeyedDataSource<Integer, FilmixMovie> {
    MovieWebService mWebService;
    String mSearchQuery;
    String mYear;
    String mCountry;
    String mGenres;
    int page;
    int maxResult;

    public FilmsSearchDataSource(MovieWebService webService, String searchQuery, String year, String country, String stringGenresQuery, int maxResult) {
        this.maxResult = maxResult;
        mWebService = webService;
        mSearchQuery = searchQuery;
        mYear = year;
        mCountry = country;
        mGenres = stringGenresQuery;
        page = 1;

    }

    @Override
    public void loadInitial(@NonNull LoadInitialParams<Integer> params, @NonNull LoadInitialCallback<Integer, FilmixMovie> callback) {
        Call<List<FilmixMovie>> data = mWebService.filteredFilmSearch(mSearchQuery, mYear, mCountry, mGenres, page, maxResult);
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

        Call<List<FilmixMovie>> data = mWebService.filteredFilmSearch(mSearchQuery, mYear, mCountry, mGenres, params.key, maxResult);
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
}
