package com.denspark.strelets.cinematrix.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.denspark.strelets.cinematrix.database.entity.FilmixMovie;
import com.denspark.strelets.cinematrix.database.entity.Genre;
import com.denspark.strelets.cinematrix.database.entity.StateOfLocalDB;

@Dao
public interface StateOfLocalDBdao {

    @Insert
    void save(StateOfLocalDB stateOfLocalDB);

    @Update
    void update(StateOfLocalDB stateOfLocalDB);

    @Query("SELECT * FROM local_db_state WHERE id = 1")
    LiveData<StateOfLocalDB> load();

}
