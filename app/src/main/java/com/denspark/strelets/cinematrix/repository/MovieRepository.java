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
import androidx.sqlite.db.SimpleSQLiteQuery;
import androidx.sqlite.db.SupportSQLiteQuery;
import com.denspark.strelets.cinematrix.App;
import com.denspark.strelets.cinematrix.api.MovieWebService;
import com.denspark.strelets.cinematrix.database.dao.*;
import com.denspark.strelets.cinematrix.database.entity.*;
import com.denspark.strelets.cinematrix.repository.paging.MoviesBoundaryCallback;
import com.denspark.strelets.cinematrix.utils.PagingRequestHelper;
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
    private static final String TAG = "MovieRepository";
    private boolean isUpdating = false;

    private final MovieDao movieDao;
    private final PersonDao personDao;
    private final GenreDao genreDao;
    private final PersonMovieDao personMovieDao;
    private final MovieGenreDao movieGenreDao;
    private final StateOfRemoteDBdao stateOfRemoteDBdao;
    private final StateOfLocalDBdao stateOfLocalDBdao;
    private final Executor executor;
    private final MovieWebService webservice;


    @Inject
    public MovieRepository(
            MovieDao movieDao,
            PersonDao personDao,
            GenreDao genreDao,
            PersonMovieDao personMovieDao,
            MovieGenreDao movieGenreDao,
            StateOfRemoteDBdao stateOfRemoteDBdao,
            StateOfLocalDBdao stateOfLocalDBdao,
            MovieWebService webservice,
            Executor executor) {

        this.movieDao = movieDao;
        this.personDao = personDao;
        this.genreDao = genreDao;
        this.personMovieDao = personMovieDao;
        this.movieGenreDao = movieGenreDao;
        this.stateOfRemoteDBdao = stateOfRemoteDBdao;
        this.stateOfLocalDBdao = stateOfLocalDBdao;
        this.webservice = webservice;
        this.executor = executor;

    }


    private MoviesBoundaryCallback boundaryCallback = new MoviesBoundaryCallback(this);

    public LiveData<PagedList<FilmixMovie>> getAllMovies(PagedList.Config config) {
        DataSource.Factory<Integer, FilmixMovie> factory = movieDao.getAllMoviesPaged();
        return new LivePagedListBuilder<>(factory, config).setBoundaryCallback(boundaryCallback).build();
    }

    public LiveData<PagedList<FilmixMovie>> getMoviesForGenre(PagedList.Config config, int genreId) {
        DataSource.Factory<Integer, FilmixMovie> factory = movieGenreDao.getMoviesForGenrePagged(genreId);
        return new LivePagedListBuilder<>(factory, config).build();
    }

    public LiveData<PagedList<FilmixMovie>> getMoviesForGenre(PagedList.Config config, String rawQuery) {
        SupportSQLiteQuery rawQ = new SimpleSQLiteQuery(rawQuery);

        DataSource.Factory<Integer, FilmixMovie> factory = movieGenreDao.getMoviesForGenrePagged(rawQ);
        return new LivePagedListBuilder<>(factory, config).build();
    }

//    public LiveData<List<FilmixMovie>> getMoviesForGenre(String rawQuery) {
//        SupportSQLiteQuery rawQ = new SimpleSQLiteQuery(rawQuery);
//
//        return movieGenreDao.getMoviesForGenrePagged(rawQ);
//
//    }

    public LiveData<FilmixMovie> getCurrMovie(int id) {
        MediatorLiveData<FilmixMovie> filmixMovieMediatorLiveData = new MediatorLiveData<>();
        filmixMovieMediatorLiveData.addSource(movieDao.load(id), new Observer<FilmixMovie>() {
            @Override public void onChanged(FilmixMovie filmixMovie) {
                movieGenreDao.getGenresForMovieLive(id).observeForever(new Observer<List<Genre>>() {
                    @Override public void onChanged(List<Genre> genres) {
                        filmixMovie.setGenres(genres);
                        filmixMovieMediatorLiveData.setValue(filmixMovie);
                    }
                });

            }
        });
        return filmixMovieMediatorLiveData;
    }

    public LiveData<FilmixMovie> currentMovie(int id) {
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

    public void addSomeDataFromServer(String query) {
        executor.execute(() -> {

            webservice.getMovies(query).enqueue(new Callback<List<FilmixMovie>>() {
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
                        isUpdating = false;
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
                                saveGenresForEntity(person, genreIds, genresOfFilms, new PersonMoviesJoin(0, 0));
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
                if (joinEntity.getClass().isAssignableFrom(PersonMoviesJoin.class)) {
                    personMovieDao.insert(new PersonMoviesJoin(personOrMovie.getId(), genreId));
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
                                                    if (joinEntity.getClass().isAssignableFrom(PersonMoviesJoin.class)) {
                                                        personMovieDao.insert(new PersonMoviesJoin(personOrMovie.getId(), genreId));
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

    private void saveActorsForMovie(
            ContainerEntity personOrMovie,
            List<Integer> personIds,
            List<Person> savedPersonList,
            JoinEntity joinEntity) {

        for (Integer personId : personIds) {
            Person person = personDao.getPerson(personId);
            if (person != null) {
                savedPersonList.add(person);
                personMovieDao.insert(new PersonMoviesJoin(personId, personOrMovie.getId()));
            } else {
                executor.execute(
                        () -> {
                            webservice.getPersonByIdRemote(personId).enqueue(new Callback<Person>() {
                                @Override public void onResponse(Call<Person> call, Response<Person> response) {
                                    if (response.body() != null) {
                                        executor.execute(
                                                () -> {
                                                    personDao.save(response.body());
                                                    Person actor = personDao.getPerson(personId);
                                                    savedPersonList.add(actor);
                                                    personMovieDao.insert(
                                                            new PersonMoviesJoin(personId, personOrMovie.getId())
                                                    );
                                                }
                                        );
                                    }
                                }

                                @Override public void onFailure(Call<Person> call, Throwable t) {
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

    public Executor getExecutor() {
        return executor;
    }


    // TODO: 17.06.2019 refactor to genre for person
//    public void testGetPerson() {
//        executor.execute(
//                () -> {
//                    int id = 110;
//                    Person p = personDao.getPerson(id);
//                    p.setFilmsGenres(personMovieDao.getGenresForPerson(id));
//                    Log.d("TAG", "Db test success" + p);
//                }
//        );
//    }

    public Genre getGenre(String genreName) {
        return genreDao.getGenre(genreName);
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

    public LiveData<List<Genre>> getAllGenres() {
        return genreDao.getAllGenres();
    }

    public void updateStateOfRemoteDB() {
        executor.execute(
                () -> {
                    webservice.getStateOfRemoteDB().enqueue(new Callback<StateOfRemoteDB>() {
                        @Override public void onResponse(Call<StateOfRemoteDB> call, Response<StateOfRemoteDB> response) {
                            if (response.body() != null) {
                                executor.execute(
                                        () -> {
                                            stateOfRemoteDBdao.save(response.body());
                                        }
                                );
                            }
                        }

                        @Override public void onFailure(Call<StateOfRemoteDB> call, Throwable t) {

                        }
                    });
                }
        );
    }

    public void addSomeDataFromServer(String query, int start, int maxRows, PagingRequestHelper.RequestType type, PagingRequestHelper helper) {

        helper.runIfNotRunning(type, callback ->
                webservice.getNewMovies(query, start, maxRows).enqueue(new Callback<List<FilmixMovie>>() {
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
                                            List<Integer> actorIds = movie.getActorsId();
                                            List<Genre> genresOfFilms = new ArrayList<>();
                                            List<Person> actorsInFilm = new ArrayList<>();
                                            // TODO: 25.05.2019 Live Data observer for Genres
                                            saveGenresForEntity(movie, genreIds, genresOfFilms, new MovieGenreJoin(0, 0));
                                            saveActorsForMovie(movie,actorIds,actorsInFilm,new PersonMoviesJoin(0, 0));
                                        }
                                );
                            }
                            callback.recordSuccess();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<FilmixMovie>> call, Throwable t) {
                        Log.d("MOVIES_ERROR", call.toString());
                        callback.recordFailure(t);
                    }
                })
        );
    }


    public LiveData<List<Person>> getActorsForMovie(int movieId){
     return personMovieDao.getPersonsForMovies(movieId);
    }

}
