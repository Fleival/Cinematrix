package com.denspark.strelets.cinematrix.database;

import android.app.Application;
import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.denspark.strelets.cinematrix.database.conventers.DateConverter;
import com.denspark.strelets.cinematrix.database.dao.CountryDao;
import com.denspark.strelets.cinematrix.database.dao.GenreDao;
import com.denspark.strelets.cinematrix.database.dao.MovieDao;
import com.denspark.strelets.cinematrix.database.dao.MovieGenreDao;
import com.denspark.strelets.cinematrix.database.dao.PersonDao;
import com.denspark.strelets.cinematrix.database.dao.PersonMovieDao;
import com.denspark.strelets.cinematrix.database.dao.RatingDao;
import com.denspark.strelets.cinematrix.database.dao.StateOfLocalDBdao;
import com.denspark.strelets.cinematrix.database.dao.StateOfRemoteDBdao;
import com.denspark.strelets.cinematrix.database.entity.Country;
import com.denspark.strelets.cinematrix.database.entity.FilmixMovie;
import com.denspark.strelets.cinematrix.database.entity.Genre;
import com.denspark.strelets.cinematrix.database.entity.MovieGenreJoin;
import com.denspark.strelets.cinematrix.database.entity.Person;
import com.denspark.strelets.cinematrix.database.entity.PersonMoviesJoin;
import com.denspark.strelets.cinematrix.database.entity.Rating;
import com.denspark.strelets.cinematrix.database.entity.StateOfLocalDB;
import com.denspark.strelets.cinematrix.database.entity.StateOfRemoteDB;


@Database(entities = {
        FilmixMovie.class,
        Person.class,
        Genre.class,
        PersonMoviesJoin.class,
        MovieGenreJoin.class,
        StateOfRemoteDB.class,
        StateOfLocalDB.class,
        Country.class,
        Rating.class
        }, version = 1, exportSchema = false)

@TypeConverters(DateConverter.class)

public abstract class MovieDatabase extends RoomDatabase {

    // --- SINGLETON ---
    private static volatile MovieDatabase INSTANCE;

    // --- DAOs ---
    public abstract MovieDao movieDao();

    public abstract PersonDao personDao();

    public abstract GenreDao genreDao();

    public abstract PersonMovieDao personGenreDao();

    public abstract MovieGenreDao movieGenreDao();

    public abstract StateOfRemoteDBdao stateOfRemoteDBdao();

    public abstract StateOfLocalDBdao stateOfLocalDBdao();

    public abstract CountryDao countryDao();

    public abstract RatingDao ratingDao();

    public static MovieDatabase getINSTANCE(Context context, boolean TEST_MODE) {
        if (INSTANCE == null) {
            if (TEST_MODE) {
                INSTANCE = Room.databaseBuilder(context,
                        MovieDatabase.class, "MovieDatabase.db")
                        .allowMainThreadQueries()
                        .build();
            } else {
                INSTANCE = Room.databaseBuilder(context,
                        MovieDatabase.class, "MovieDatabase.db")
                        .build();
            }
        }
        return INSTANCE;
    }
    public static MovieDatabase getINSTANCE(Application application, boolean TEST_MODE) {
        return getINSTANCE(application.getApplicationContext(), TEST_MODE);
    }
}
