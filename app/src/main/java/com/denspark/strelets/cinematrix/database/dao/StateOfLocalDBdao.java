package com.denspark.strelets.cinematrix.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.denspark.strelets.cinematrix.database.entity.FilmixMovie;
import com.denspark.strelets.cinematrix.database.entity.Genre;
import com.denspark.strelets.cinematrix.database.entity.StateOfLocalDB;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface StateOfLocalDBdao {

    @Insert(onConflict = REPLACE)
    void save(StateOfLocalDB stateOfLocalDB);

    @Update
    void update(StateOfLocalDB stateOfLocalDB);

    @Query("SELECT * FROM local_db_state WHERE id = 1")
    LiveData<StateOfLocalDB> load();

}
