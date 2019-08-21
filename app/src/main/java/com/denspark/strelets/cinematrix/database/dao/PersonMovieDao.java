package com.denspark.strelets.cinematrix.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import com.denspark.strelets.cinematrix.database.entity.Genre;
import com.denspark.strelets.cinematrix.database.entity.Person;
import com.denspark.strelets.cinematrix.database.entity.PersonMoviesJoin;

import java.util.List;

@Dao
public interface PersonMovieDao extends JoinDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(PersonMoviesJoin personMoviesJoin);

    @Query("SELECT * FROM people INNER JOIN person_movie_join ON people.id=person_movie_join.personId WHERE person_movie_join.movieId=:movieId")
    LiveData<List<Person>> getPersonsForMovies(int movieId);

    @Query("SELECT * FROM movie INNER JOIN person_movie_join ON movie.id=person_movie_join.movieId WHERE person_movie_join.personId=:personId")
    List<Genre> getMoviesForPerson(int personId);

    @Query("DELETE FROM person_movie_join")
    void deleteAllMoviePersons();
}
