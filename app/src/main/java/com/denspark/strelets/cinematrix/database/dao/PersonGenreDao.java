package com.denspark.strelets.cinematrix.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import com.denspark.strelets.cinematrix.database.entity.Genre;
import com.denspark.strelets.cinematrix.database.entity.Person;
import com.denspark.strelets.cinematrix.database.entity.PersonGenreJoin;

import java.util.List;

@Dao
public interface PersonGenreDao extends JoinDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(PersonGenreJoin personGenreJoin);

    @Query("SELECT * FROM people INNER JOIN person_genre_join ON people.id=person_genre_join.personId WHERE person_genre_join.genreId=:genreId")
    List<Person> getPersonForGenre(int genreId);

    @Query("SELECT * FROM genre INNER JOIN person_genre_join ON genre.id=person_genre_join.genreId WHERE person_genre_join.personId=:personId")
    List<Genre> getGenresForPerson(int personId);
}
