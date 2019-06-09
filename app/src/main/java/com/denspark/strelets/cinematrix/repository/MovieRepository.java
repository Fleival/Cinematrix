package com.denspark.strelets.cinematrix.repository;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;
import androidx.paging.DataSource;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;
import com.denspark.strelets.cinematrix.App;
import com.denspark.strelets.cinematrix.api.MovieWebService;
import com.denspark.strelets.cinematrix.database.dao.*;
import com.denspark.strelets.cinematrix.database.entity.*;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

//Repository Version = 0.1beta
@Singleton
public class MovieRepository {

    private final MovieDao movieDao;
    private final PersonDao personDao;
    private final GenreDao genreDao;
    private final PersonGenreDao personGenreDao;
    private final MovieGenreDao movieGenreDao;
    private final Executor executor;
    private final MovieWebService webservice;


    @Inject
    public MovieRepository(
            MovieDao movieDao,
            PersonDao personDao,
            GenreDao genreDao,
            PersonGenreDao personGenreDao,
            MovieGenreDao movieGenreDao,
            MovieWebService webservice,
            Executor executor) {

        this.movieDao = movieDao;
        this.personDao = personDao;
        this.genreDao = genreDao;
        this.personGenreDao = personGenreDao;
        this.movieGenreDao = movieGenreDao;
        this.webservice = webservice;
        this.executor = executor;

    }



    public LiveData<PagedList<FilmixMovie>> getAllMovies(PagedList.Config config) {
        DataSource.Factory<Integer, FilmixMovie> factory = movieDao.getAllMoviesPaged();
        return new LivePagedListBuilder<>(factory, config).build();
    }

    public LiveData<FilmixMovie> getCurrMovie(int id){
        MediatorLiveData<FilmixMovie> filmixMovieMediatorLiveData = new MediatorLiveData<>();
        filmixMovieMediatorLiveData.addSource(movieDao.load(id), new Observer<FilmixMovie>() {
            @Override public void onChanged(FilmixMovie filmixMovie) {
                filmixMovie.setGenres((movieGenreDao.getGenresForMovie(id)));
                filmixMovieMediatorLiveData.setValue(filmixMovie);
            }
        });
        return filmixMovieMediatorLiveData;
    }

    public LiveData<FilmixMovie> currentMovie(int id){
        return movieDao.load(id);
    }


    public void save(FilmixMovie movie) {
        new InsertMovieAsyncTask(movieDao).execute(movie);
    }

    public void clearMovieData() {
        executor.execute(
                movieDao::deleteAllMovies
        );
    }

    public void addSomeDataFromServer() {
        executor.execute(() -> {

            webservice.getNewMovies().enqueue(new Callback<List<FilmixMovie>>() {
                @Override
                public void onResponse(Call<List<FilmixMovie>> call, Response<List<FilmixMovie>> response) {
                    Log.d("TAG", "DATA REFRESHED FROM NETWORK");
                    Toast.makeText(App.context, "Data refreshed from network !", Toast.LENGTH_LONG).show();

                    List<FilmixMovie> listResponse = response.body();
                    if (listResponse != null) {
                        Log.d("MOVIES", listResponse.toString());
                        for (FilmixMovie movie : listResponse) {
                            executor.execute(() -> movieDao.save(movie));
                            executor.execute(
                                    () -> {
                                        List<Integer> genreIds = movie.getGenresId();
                                        List<Genre> genresOfFilms = new ArrayList<>();
                                        // TODO: 25.05.2019 Live Data observer for Genres
                                        saveGenresForEntity(movie, genreIds, genresOfFilms, new MovieGenreJoin(0, 0));
                                    }
                            );
                        }
                    }
                }

                @Override
                public void onFailure(Call<List<FilmixMovie>> call, Throwable t) {
                    Log.d("MOVIES_ERROR", call.toString());
                }
            });
        });
    }

    public void testWebServicePerson() {
        executor.execute(() -> webservice.getTestPerson().enqueue(new Callback<List<Person>>() {
            @Override
            public void onResponse(Call<List<Person>> call, Response<List<Person>> response) {
                Log.d("TAG", "DATA REFRESHED FROM NETWORK");
                Toast.makeText(App.context, "Data refreshed from network !", Toast.LENGTH_LONG).show();

                List<Person> personListResponse = response.body();
                Log.d("PERSONS", personListResponse.toString());

                for (Person person : personListResponse) {
                    executor.execute(() -> personDao.save(person));
                    executor.execute(
                            () -> {
                                List<Integer> genreIds = person.getFilmsGenresId();
                                List<Genre> genresOfFilms = new ArrayList<>();
                                // TODO: 25.05.2019 Live Data observer for Genres
                                saveGenresForEntity(person, genreIds, genresOfFilms, new PersonGenreJoin(0, 0));
                            }
                    );
                }
            }

            @Override
            public void onFailure(Call<List<Person>> call, Throwable t) {

            }
        }));

    }

    private void saveGenresForEntity(
            ContainerEntity personOrMovie,
            List<Integer> genreIds,
            List<Genre> savedGenreList,
            JoinEntity joinEntity) {

        for (Integer genreId : genreIds) {
            Genre genre = genreDao.getGenre(genreId);
            if (genre != null) {
                savedGenreList.add(genre);
                if (joinEntity.getClass().isAssignableFrom(PersonGenreJoin.class)) {
                    personGenreDao.insert(new PersonGenreJoin(personOrMovie.getId(), genreId));
                } else if (joinEntity.getClass().isAssignableFrom(MovieGenreJoin.class)) {
                    movieGenreDao.insert(new MovieGenreJoin(personOrMovie.getId(), genreId));

                }
            } else {
                executor.execute(
                        () -> {
                            webservice.getGenreByIdRemote(genreId).enqueue(new Callback<Genre>() {
                                @Override public void onResponse(Call<Genre> call, Response<Genre> response) {
                                    if (response.body() != null) {
                                        executor.execute(
                                                () -> {
                                                    genreDao.save(response.body());
                                                    Genre genre_1 = genreDao.getGenre(genreId);
                                                    savedGenreList.add(genre_1);
                                                    if (joinEntity.getClass().isAssignableFrom(PersonGenreJoin.class)) {
                                                        personGenreDao.insert(new PersonGenreJoin(personOrMovie.getId(), genreId));
                                                    } else if (joinEntity.getClass().isAssignableFrom(MovieGenreJoin.class)) {
                                                        movieGenreDao.insert(new MovieGenreJoin(personOrMovie.getId(), genreId));

                                                    }
                                                }
                                        );
                                    }
                                }

                                @Override public void onFailure(Call<Genre> call, Throwable t) {
                                }
                            });
                        }
                );
            }
        }
    }

    public void testWebServiceFilm() {
        executor.execute(() -> {

            webservice.getSomeMovies().enqueue(new Callback<List<FilmixMovie>>() {
                @Override
                public void onResponse(Call<List<FilmixMovie>> call, Response<List<FilmixMovie>> response) {
                    Log.d("TAG", "DATA REFRESHED FROM NETWORK");
                    Toast.makeText(App.context, "Data refreshed from network !", Toast.LENGTH_LONG).show();


                    List<FilmixMovie> listResponse = response.body();
                    if (listResponse != null) {
                        Log.d("MOVIES", listResponse.toString());
                    }
//                    executor.execute(()-> movieDao.save(response.body()));
                }

                @Override
                public void onFailure(Call<List<FilmixMovie>> call, Throwable t) {
                    Log.d("MOVIES_ERROR", call.toString());
                }
            });
        });

    }

    public void testGetPerson() {
        executor.execute(
                () -> {
                    int id = 110;
                    Person p = personDao.getPerson(id);
                    p.setFilmsGenres(personGenreDao.getGenresForPerson(id));
                    Log.d("TAG", "Db test success" + p);
                }
        );

    }

    private static class InsertMovieAsyncTask extends AsyncTask<FilmixMovie, Void, Void> {
        private MovieDao movieDao;

        private InsertMovieAsyncTask(MovieDao movieDao) {
            this.movieDao = movieDao;
        }

        @Override
        protected Void doInBackground(FilmixMovie... movies) {
            movieDao.save(movies[0]);
            return null;
        }
    }

}
