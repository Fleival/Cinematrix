package com.denspark.strelets.cinematrix.repository;

import androidx.annotation.NonNull;

import com.denspark.strelets.cinematrix.api.MovieWebService;
import com.denspark.strelets.cinematrix.database.entity.FilmixMovie;

import java.util.List;

import retrofit2.Call;

public class LastMoviesFilmDataSource extends FilmsServerDataSource {
    public LastMoviesFilmDataSource(MovieWebService webService, int maxResult) {
        super(webService, maxResult);
    }

    @Override
    protected Call<List<FilmixMovie>> getListCallInitial() {
        return mWebService.getLastMovies(page, maxResult);
    }

    @Override
    protected Call<List<FilmixMovie>> getListCallAfter(@NonNull LoadParams<Integer> params) {
        return mWebService.getLastMovies(params.key, maxResult);
    }
}
