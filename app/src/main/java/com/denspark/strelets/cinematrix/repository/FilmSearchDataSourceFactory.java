package com.denspark.strelets.cinematrix.repository;

import androidx.lifecycle.MutableLiveData;
import androidx.paging.DataSource;
import androidx.paging.PageKeyedDataSource;

import com.denspark.strelets.cinematrix.api.MovieWebService;
import com.denspark.strelets.cinematrix.database.entity.FilmixMovie;
import com.denspark.strelets.cinematrix.database.entity.Genre;

import java.util.List;

public class FilmSearchDataSourceFactory extends DataSource.Factory {
    MovieWebService mWebService;
    String mSearchQuery;
    String mYear;
    String mCountry;
    List<Genre> mGenres;
    int maxResult;


    FilmsSearchDataSource filmsSearchDataSource;
    MutableLiveData<PageKeyedDataSource<Integer, FilmixMovie>> mutableLiveData;

    public FilmSearchDataSourceFactory(MovieWebService webService, String searchQuery, String year, String country, List<Genre> genres, int maxResult) {
        mWebService = webService;
        mSearchQuery = searchQuery;
        mYear = year;
        mGenres = genres;
        mCountry = country;
        mutableLiveData = new MutableLiveData<>();
        this.maxResult = maxResult;
    }

    @Override
    public DataSource create() {
        String stringGenresQuery = buildGenresQuery(mGenres);
        filmsSearchDataSource = new FilmsSearchDataSource(mWebService, mSearchQuery, mYear, mCountry, stringGenresQuery, maxResult);
        mutableLiveData.postValue(filmsSearchDataSource);
        return filmsSearchDataSource;
    }

    public MutableLiveData<PageKeyedDataSource<Integer, FilmixMovie>> getMutableLiveData() {
        return mutableLiveData;
    }

    private String buildGenresQuery(List<Genre> genres) {
        String s = null;
        if (genres != null) {
            for (int i = 0; i < genres.size(); i++) {
                Genre g = genres.get(i);
                if (i == 0) {
                    s = g.getName();
                } else {
                    s += "*" + g.getName();
                }
            }
        }
        return s;
    }
}