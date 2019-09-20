package com.denspark.strelets.cinematrix.api;

import com.denspark.strelets.cinematrix.database.entity.FilmixMovie;
import com.denspark.strelets.cinematrix.database.entity.Genre;
import com.denspark.strelets.cinematrix.database.entity.Person;
import com.denspark.strelets.cinematrix.database.entity.Rating;
import com.denspark.strelets.cinematrix.database.entity.StateOfRemoteDB;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface MovieWebService {
    @GET("/filmix/get_films")
//    @GET("/filmix/get_films?query=SELECT f FROM Film f WHERE f.id<=60")
//    @GET("/filmix/get_films?query=SELECT f FROM Film f") // Dont DO this !!!!!
    Call<List<FilmixMovie>> getMovies(@Query("query") String query);

    @GET("/filmix/get_ex_films")
    Call<List<FilmixMovie>> getNewMovies(@Query("query") String query, @Query("page") int page, @Query("maxResult") int maxResult);

    @GET("/filmix/films")
    Call<List<FilmixMovie>> getNewMovies(@Query("page") int page, @Query("maxResult") int maxResult);

    @GET("/filmix/get_persons?query=SELECT p FROM Person p WHERE p.id<=110")
    Call<List<Person>> getTestPerson();

    @GET("/filmix/get_films?query=SELECT f FROM Film f WHERE f.id=30500")
    Call<List<FilmixMovie>> getSomeMovies();

    @GET("/filmix/genre/{id}")
    Call<Genre> getGenreByIdRemote(@Path("id") int genreId);

    @GET("/filmix/load_genres")
    Call<List<Genre>> loadGenres();

    @GET("/filmix/person/{id}")
    Call<Person> getPersonByIdRemote(@Path("id") int personId);

    @GET("/filmix/film/{id}")
    Call<FilmixMovie> getMovieByIdRemote(@Path("id") int movieId);

    @GET("/filmix/get_db_state")
    Call<StateOfRemoteDB> getStateOfRemoteDB();

    @GET("/filmix/search_films")
    Call<List<FilmixMovie>> searchMovies(@Query("search") String search, @Query("page") int page, @Query("maxResult")int maxResult );

    @GET("/filmix/filtered_film_search")
    Call<List<FilmixMovie>> filteredFilmSearch(
            @Query("search") String searchName,
            @Query("year") String year,
            @Query("country") String country,
            @Query("genres") String genres,
            @Query("page") int page,
            @Query("maxResult")int maxResult );

    @GET("/filmix/countries")
    Call<List<String>> loadCountryList();

    @GET("/filmix/rating_for_film")
    Call<Rating> getRatingForFilm(@Query("id") int filmId);

    @GET("/filmix/top_films")
    Call<List<FilmixMovie>> getPopularFilms(
            @Query("page") int page,
            @Query("limit")int maxResult );

    @GET("/filmix/last_movies")
    Call<List<FilmixMovie>> getLastMovies(
            @Query("page") int page,
            @Query("limit")int maxResult );

    @GET("/filmix/last_tv_series")
    Call<List<FilmixMovie>> getLastTvSeries(
            @Query("page") int page,
            @Query("limit")int maxResult );

    @GET("/filmix/all_movies")
    Call<List<FilmixMovie>> getAllMovies(
            @Query("page") int page,
            @Query("limit")int maxResult );

    @GET("/filmix/all_tv_series")
    Call<List<FilmixMovie>> getAllTvSeries(
            @Query("page") int page,
            @Query("limit")int maxResult );
}
