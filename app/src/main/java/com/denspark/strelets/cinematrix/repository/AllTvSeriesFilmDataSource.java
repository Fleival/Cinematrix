package com.denspark.strelets.cinematrix.repository;

import androidx.annotation.NonNull;

import com.denspark.strelets.cinematrix.api.MovieWebService;
import com.denspark.strelets.cinematrix.database.entity.FilmixMovie;

import java.util.List;

import retrofit2.Call;

public class AllTvSeriesFilmDataSource extends FilmsServerDataSource {
    public AllTvSeriesFilmDataSource(MovieWebService webService, int maxResult) {
        super(webService, maxResult);
    }

    @Override
    protected Call<List<FilmixMovie>> getListCallInitial() {
        return mWebService.getAllTvSeries(page, maxResult);
    }

    @Override
    protected Call<List<FilmixMovie>> getListCallAfter(@NonNull LoadParams<Integer> params) {
        return mWebService.getAllTvSeries(params.key, maxResult);
    }
}
