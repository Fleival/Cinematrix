package com.denspark.strelets.cinematrix;

import android.content.Context;
import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import com.denspark.strelets.cinematrix.database.MovieDatabase;
import com.denspark.strelets.cinematrix.database.dao.MovieDao;
import com.denspark.strelets.cinematrix.database.entity.FilmixMovie;
import com.denspark.strelets.cinematrix.di.component.DaggerJTestComponent;
import com.denspark.strelets.cinematrix.repository.MovieRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

@RunWith(AndroidJUnit4.class)
public class RepositoryTest {
    @Inject
    public Context context;
    @Inject
    public MovieRepository repository;
    @Inject
    public MovieDao movieDao;
    @Inject
    public MovieDatabase db;
    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Before
    public void initDagger() {
        DaggerJTestComponent.create().inject(this);
    }

    @After
    public void closeDb() throws IOException {
        db.close();
    }

    @Test
    public void testMovieDao() {
        LiveData<FilmixMovie> currentMovie = repository.getCurrMovie(491);
        FilmixMovie movie = movieDao.getById(491);
        assertEquals(movie.getName(), "Супергерои");
        assertNotEquals(movie.getName(), "FooBar");
        currentMovie.observeForever(new Observer<FilmixMovie>() {
            @Override public void onChanged(FilmixMovie filmixMovie) {
                FilmixMovie movie1 = filmixMovie;
                assertEquals(movie1.getName(), "Супергерои");
            }
        });
    }
}
