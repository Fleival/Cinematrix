package com.denspark.strelets.cinematrix.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.denspark.strelets.cinematrix.database.entity.StateOfLocalDB;
import com.denspark.strelets.cinematrix.database.entity.StateOfRemoteDB;

@Dao
public interface StateOfRemoteDBdao {

    @Insert
    void save(StateOfRemoteDB stateOfRemoteDB);

    @Update
    void update(StateOfRemoteDB stateOfRemoteDB);

    @Query("SELECT * FROM local_db_state WHERE id = 1")
    LiveData<StateOfRemoteDB> load();

}

