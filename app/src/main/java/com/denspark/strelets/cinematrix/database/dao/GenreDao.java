package com.denspark.strelets.cinematrix.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import com.denspark.strelets.cinematrix.database.entity.Genre;

import java.util.List;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface GenreDao {
    @Insert(onConflict = REPLACE)
    void save(Genre genre);

    @Insert(onConflict = REPLACE)
    void save(List<Genre> genres);

    @Query("SELECT * FROM genre")
    LiveData<List<Genre>> getAllGenres();

    @Query("SELECT * FROM genre WHERE id = :id")
    LiveData<Genre> getGenreLive(int id);

    @Query("SELECT * FROM genre WHERE id = :id")
    Genre getGenre(int id);

}
