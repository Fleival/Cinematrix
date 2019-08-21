package com.denspark.strelets.cinematrix.database.dao;

import androidx.lifecycle.LiveData;
import androidx.paging.DataSource;
import androidx.room.*;
import androidx.sqlite.db.SupportSQLiteQuery;
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

    @Query("SELECT * FROM movie INNER JOIN movie_genre_join ON movie.id=movie_genre_join.movieId WHERE movie_genre_join.genreId=:genreId")
    DataSource.Factory<Integer, FilmixMovie> getMoviesForGenrePagged(int genreId);

    @Query("SELECT * FROM genre INNER JOIN movie_genre_join ON genre.id=movie_genre_join.genreId WHERE movie_genre_join.movieId=:movieId")
    List<Genre> getGenresForMovie(int movieId);

    @Query("SELECT * FROM genre INNER JOIN movie_genre_join ON genre.id=movie_genre_join.genreId WHERE movie_genre_join.movieId=:movieId")
    LiveData<List<Genre>> getGenresForMovieLive(int movieId);

//    @RawQuery(observedEntities = FilmixMovie.class)
//    LiveData<List<FilmixMovie>> getMoviesForGenrePagged(SupportSQLiteQuery query);

    @RawQuery(observedEntities = FilmixMovie.class)
    DataSource.Factory<Integer, FilmixMovie> getMoviesForGenrePagged(SupportSQLiteQuery query);

    @Query("DELETE FROM movie_genre_join")
    void deleteAllMovieGenres();
}
