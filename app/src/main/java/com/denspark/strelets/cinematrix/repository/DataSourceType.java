package com.denspark.strelets.cinematrix.repository;

public enum DataSourceType {
    POPULAR_MOVIES,
    LAST_MOVIES,
    LAST_TV_SERIES;

    public static DataSourceType fromString(final String s){
        return DataSourceType.valueOf(s);
    }
}
