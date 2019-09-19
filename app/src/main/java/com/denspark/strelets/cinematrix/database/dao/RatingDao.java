package com.denspark.strelets.cinematrix.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.denspark.strelets.cinematrix.database.entity.Genre;
import com.denspark.strelets.cinematrix.database.entity.Rating;

import java.util.List;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface RatingDao {
    @Insert(onConflict = REPLACE)
    void save(Rating rating);

    @Insert(onConflict = REPLACE)
    void save(List<Rating> ratingList);

    @Query("SELECT * FROM rating")
    LiveData<List<Rating>> getAllRatings();

    @Query("SELECT * FROM rating WHERE id = :id")
    LiveData<Rating> getRatingLive(int id);

    @Query("SELECT * FROM genre WHERE id = :id")
    Genre getGenre(int id);

    @Query("SELECT * FROM genre WHERE name = :name")
    Genre getGenre(String name);

}
