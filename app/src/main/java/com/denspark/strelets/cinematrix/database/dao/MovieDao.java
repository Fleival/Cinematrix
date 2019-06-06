package com.denspark.strelets.cinematrix.database.dao;

import androidx.lifecycle.LiveData;
import androidx.paging.DataSource;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import com.denspark.strelets.cinematrix.database.entity.FilmixMovie;

import java.util.List;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface MovieDao {

    @Insert(onConflict = REPLACE)
    void save(FilmixMovie filmixMovie);

    @Insert(onConflict = REPLACE)
    void save(List<FilmixMovie> movies);

    @Query("DELETE FROM movie")
    void deleteAllMovies();

    @Query("SELECT * FROM movie WHERE id = :movieId")
    LiveData<FilmixMovie> load(int movieId);

    @Query("SELECT * FROM movie WHERE name = :movieTitle")
    LiveData<FilmixMovie> load(String movieTitle);

    @Query("SELECT * FROM movie")
    LiveData<List<FilmixMovie>> getAllMovies();

    @Query("SELECT * FROM movie ORDER BY uploadDate DESC")
    DataSource.Factory<Integer, FilmixMovie> getAllMoviesPaged();

}
