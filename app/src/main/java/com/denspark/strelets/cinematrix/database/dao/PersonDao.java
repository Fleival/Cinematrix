package com.denspark.strelets.cinematrix.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import com.denspark.strelets.cinematrix.database.entity.Genre;
import com.denspark.strelets.cinematrix.database.entity.Person;

import java.util.List;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface PersonDao {
    @Insert(onConflict = REPLACE)
    void save(Person person);

    @Insert(onConflict = REPLACE)
    void save(List<Person> people);

    @Query("SELECT * FROM people")
    LiveData<List<Person>> getAllPeople();

    @Query("SELECT * FROM people WHERE id = :id")
    Person getPerson(int id);


}
