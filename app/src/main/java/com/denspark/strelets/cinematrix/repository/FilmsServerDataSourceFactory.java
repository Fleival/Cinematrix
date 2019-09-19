package com.denspark.strelets.cinematrix.repository;

import androidx.paging.DataSource;

import com.denspark.strelets.cinematrix.api.MovieWebService;

public class FilmsServerDataSourceFactory extends DataSource.Factory {
    MovieWebService mWebService;
    int maxResult;
    private DataSourceType dataType;

    public FilmsServerDataSourceFactory(MovieWebService webService, int maxResult, DataSourceType dataType) {
        mWebService = webService;
        this.maxResult = maxResult;
        this.dataType = dataType;
    }

    @Override
    public DataSource create() {
        FilmsServerDataSource dataSource = null;
        switch (dataType) {
            case POPULAR_MOVIES: {
                dataSource = new PopularFilmDataSource(mWebService, maxResult);
            }
            break;
            case LAST_MOVIES: {
                dataSource = new LastMoviesFilmDataSource(mWebService, maxResult);
            }
            break;
            case LAST_TV_SERIES: {
                dataSource = new LastTvSeriesFilmDataSource(mWebService, maxResult);
            }
            break;
        }
        return dataSource;
    }
}
