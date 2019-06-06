package com.denspark.strelets.cinematrix.api;

import com.denspark.strelets.cinematrix.database.entity.FilmixMovie;
import com.denspark.strelets.cinematrix.database.entity.Genre;
import com.denspark.strelets.cinematrix.database.entity.Person;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

import java.util.List;

public interface MovieWebService {
    @GET("/test_spring/get_films?query=SELECT f FROM Film f WHERE f.id<=1000")
    Call<List<FilmixMovie>> getNewMovies();

    @GET("/test_spring/get_persons?query=SELECT p FROM Person p WHERE p.id<=110")
    Call<List<Person>> getTestPerson();

    @GET("/test_spring/get_films?query=SELECT f FROM Film f WHERE f.id=30500")
    Call<List<FilmixMovie>> getSomeMovies();

    @GET("/test_spring/genre/{id}")
    Call<Genre> getGenreByIdRemote(@Path("id") int genreId);


}
