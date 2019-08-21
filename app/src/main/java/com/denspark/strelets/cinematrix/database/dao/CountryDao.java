package com.denspark.strelets.cinematrix.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.denspark.strelets.cinematrix.database.entity.Country;

import java.util.List;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface CountryDao {
    @Insert(onConflict = REPLACE)
    void save(Country country);

    @Insert(onConflict = REPLACE)
    void save(List<Country> countries);

    @Query("SELECT * FROM country")
    LiveData<List<Country>> getAllCountries();
}
