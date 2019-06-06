package com.denspark.strelets.cinematrix.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import com.denspark.strelets.cinematrix.database.entity.FilmixMovie;
import com.denspark.strelets.cinematrix.database.entity.Genre;
import com.denspark.strelets.cinematrix.database.entity.MovieGenreJoin;

import java.util.List;

@Dao
public interface MovieGenreDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(MovieGenreJoin movieGenreJoin);

    @Query("SELECT * FROM movie INNER JOIN movie_genre_join ON movie.id=movie_genre_join.movieId WHERE movie_genre_join.genreId=:genreId")
    List<FilmixMovie> getMovieForGenre(int genreId);

    @Query("SELECT * FROM genre INNER JOIN movie_genre_join ON genre.id=movie_genre_join.genreId WHERE movie_genre_join.movieId=:movieId")
    List<Genre> getGenresForPerson(int movieId);
}
