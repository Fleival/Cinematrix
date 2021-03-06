package com.denspark.strelets.cinematrix;

import android.content.Context;
import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import com.denspark.strelets.cinematrix.database.MovieDatabase;
import com.denspark.strelets.cinematrix.database.dao.MovieDao;
import com.denspark.strelets.cinematrix.database.dao.MovieGenreDao;
import com.denspark.strelets.cinematrix.database.entity.FilmixMovie;
import com.denspark.strelets.cinematrix.database.entity.Genre;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;


import java.io.IOException;
import java.util.List;

@RunWith(AndroidJUnit4.class)
public class RoomQueryTests {
    private static final String TAG = "RoomQueryTests";
    private MovieDao movieDao;
    private MovieGenreDao movieGenreDao;
    private MovieDatabase db;
    private Context context;


    @Before
    public void initDb() {
        // Context of the app under test.
//        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        context = ApplicationProvider.getApplicationContext();
        db = MovieDatabase.getINSTANCE(context, true);
        movieDao = db.movieDao();
        movieGenreDao = db.movieGenreDao();
    }

    @After
    public void closeDb() throws IOException {
        db.close();
    }

    @Test
    public void testMovieDao() {
        FilmixMovie movie = movieDao.getById(491);
        List<Genre> genreList = movieGenreDao.getGenresForMovie(491);

//        Log.d(TAG, "Genres: " + genreList);
        assertThat(movie.getName(), equalTo("Супергерои"));
    }
}

