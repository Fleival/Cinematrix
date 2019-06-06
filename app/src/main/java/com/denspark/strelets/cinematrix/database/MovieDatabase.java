package com.denspark.strelets.cinematrix.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import com.denspark.strelets.cinematrix.database.conventers.DateConverter;
import com.denspark.strelets.cinematrix.database.dao.*;
import com.denspark.strelets.cinematrix.database.entity.*;


@Database(entities = {
        FilmixMovie.class,
        Person.class,
        Genre.class,
        PersonGenreJoin.class,
        MovieGenreJoin.class
        }, version = 1, exportSchema = false)

@TypeConverters(DateConverter.class)

public abstract class MovieDatabase extends RoomDatabase {

    // --- SINGLETON ---
    private static volatile MovieDatabase INSTANCE;

    // --- DAOs ---
    public abstract MovieDao movieDao();

    public abstract PersonDao personDao();

    public abstract GenreDao genreDao();

    public abstract PersonGenreDao personGenreDao();

    public abstract MovieGenreDao movieGenreDao();
}
