package com.denspark.strelets.cinematrix.repository;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.paging.DataSource;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PageKeyedDataSource;
import androidx.paging.PagedList;
import androidx.sqlite.db.SimpleSQLiteQuery;
import androidx.sqlite.db.SupportSQLiteQuery;

import com.denspark.strelets.cinematrix.api.MovieWebService;
import com.denspark.strelets.cinematrix.database.dao.CountryDao;
import com.denspark.strelets.cinematrix.database.dao.GenreDao;
import com.denspark.strelets.cinematrix.database.dao.MovieDao;
import com.denspark.strelets.cinematrix.database.dao.MovieGenreDao;
import com.denspark.strelets.cinematrix.database.dao.PersonDao;
import com.denspark.strelets.cinematrix.database.dao.PersonMovieDao;
import com.denspark.strelets.cinematrix.database.dao.RatingDao;
import com.denspark.strelets.cinematrix.database.dao.StateOfLocalDBdao;
import com.denspark.strelets.cinematrix.database.dao.StateOfRemoteDBdao;
import com.denspark.strelets.cinematrix.database.entity.ContainerEntity;
import com.denspark.strelets.cinematrix.database.entity.Country;
import com.denspark.strelets.cinematrix.database.entity.FilmixMovie;
import com.denspark.strelets.cinematrix.database.entity.Genre;
import com.denspark.strelets.cinematrix.database.entity.JoinEntity;
import com.denspark.strelets.cinematrix.database.entity.MovieGenreJoin;
import com.denspark.strelets.cinematrix.database.entity.Person;
import com.denspark.strelets.cinematrix.database.entity.PersonMoviesJoin;
import com.denspark.strelets.cinematrix.database.entity.StateOfLocalDB;
import com.denspark.strelets.cinematrix.database.entity.StateOfRemoteDB;
import com.denspark.strelets.cinematrix.repository.paging.MoviesBoundaryCallback;
import com.denspark.strelets.cinematrix.utils.PagingRequestHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.denspark.strelets.cinematrix.repository.DataSourceType.fromString;

//Repository Version = 0.1beta
@Singleton
public class MovieRepository {
    private static final String TAG = "MovieRepository";
    public final AtomicInteger atomicLastId = new AtomicInteger(0);
    public final AtomicInteger atomicMovieCount = new AtomicInteger(0);
    private boolean isUpdating = false;

    private final MovieDao movieDao;
    private final PersonDao personDao;
    private final GenreDao genreDao;
    private final CountryDao countryDao;
    private final PersonMovieDao personMovieDao;
    private final MovieGenreDao movieGenreDao;
    private final StateOfRemoteDBdao stateOfRemoteDBdao;
    private final StateOfLocalDBdao stateOfLocalDBdao;
    private final RatingDao ratingDao;
    private final Executor executor;
    private final MovieWebService webservice;
    private StateOfLocalDB currentStateOfLocalDB;

    private MutableLiveData<PageKeyedDataSource<Integer, FilmixMovie>> mutableLiveDataDataSource;


    @Inject
    public MovieRepository(
            MovieDao movieDao,
            PersonDao personDao,
            GenreDao genreDao,
            CountryDao countryDao,
            PersonMovieDao personMovieDao,
            MovieGenreDao movieGenreDao,
            StateOfRemoteDBdao stateOfRemoteDBdao,
            StateOfLocalDBdao stateOfLocalDBdao,
            RatingDao ratingDao,
            MovieWebService webservice,
            Executor executor) {

        this.movieDao = movieDao;
        this.personDao = personDao;
        this.genreDao = genreDao;
        this.countryDao = countryDao;
        this.personMovieDao = personMovieDao;
        this.movieGenreDao = movieGenreDao;
        this.stateOfRemoteDBdao = stateOfRemoteDBdao;
        this.stateOfLocalDBdao = stateOfLocalDBdao;
        this.ratingDao = ratingDao;
        this.webservice = webservice;
        this.executor = executor;

        movieDao.getRowCount().observeForever(integer -> {
                    if (currentStateOfLocalDB != null) {
                        currentStateOfLocalDB.setMoviesCount(integer);
//                                atomicMovieCount.set(integer);
                        Log.d(TAG, "MovieRepository: " + currentStateOfLocalDB.getMoviesCount());
                        executor.execute(() ->
                                stateOfLocalDBdao.save(currentStateOfLocalDB));
                    }
                }
        );

        stateOfLocalDBdao.load().observeForever(new Observer<StateOfLocalDB>() {
            @Override
            public void onChanged(StateOfLocalDB stateOfLocalDB) {
                if (stateOfLocalDB != null) {
                    currentStateOfLocalDB = stateOfLocalDB;
                    atomicLastId.set(stateOfLocalDB.getLastId());

                    Log.d(TAG, "MovieRepository: " +
                            "currentStateOfLocalDB.ID = " + currentStateOfLocalDB.getLastId() +
                            " currentStateOfLocalDB.MoviesCount = " + currentStateOfLocalDB.getMoviesCount()
                    );
                    atomicMovieCount.set(stateOfLocalDB.getMoviesCount());
                } else {
                    currentStateOfLocalDB = new StateOfLocalDB();
                    currentStateOfLocalDB.setId(1);
                    Log.d(TAG, "MovieRepository: atomicLastId = " + atomicLastId.get());
                }
            }
        });
    }

    private MoviesBoundaryCallback boundaryCallback = new MoviesBoundaryCallback(this);

    @SuppressLint("NewApi")
    public LiveData<PagedList<FilmixMovie>> getAllMovies(PagedList.Config config) {
        DataSource.Factory<Integer, FilmixMovie> factory = movieDao.getAllMoviesPaged();
        return new LivePagedListBuilder<>(factory, config).setBoundaryCallback(boundaryCallback).build();
    }

    public LiveData<PagedList<FilmixMovie>> searchMoviesByName(PagedList.Config config, String name) {
        DataSource.Factory<Integer, FilmixMovie> factory = movieDao.searchMoviesByName(name);
        return new LivePagedListBuilder<>(factory, config).build();
    }

    public LiveData<PagedList<FilmixMovie>> searchFilteredMovies(PagedList.Config config, String name, String year, String country, List<Genre> genres) {
        FilmSearchDataSourceFactory filmSearchDataSourceFactory = new FilmSearchDataSourceFactory(webservice, name, year, country, genres, 10);
        mutableLiveDataDataSource = filmSearchDataSourceFactory.getMutableLiveData();
        return new LivePagedListBuilder<Integer, FilmixMovie>(filmSearchDataSourceFactory, config).setFetchExecutor(executor).build();
    }

    public LiveData<PagedList<FilmixMovie>> getMoviesForGenre(PagedList.Config config, String rawQuery) {
        SupportSQLiteQuery rawQ = new SimpleSQLiteQuery(rawQuery);

        DataSource.Factory<Integer, FilmixMovie> factory = movieGenreDao.getMoviesForGenrePagged(rawQ);
        return new LivePagedListBuilder<>(factory, config).build();
    }


    public LiveData<FilmixMovie> getCurrMovie(int id) {
        MediatorLiveData<FilmixMovie> filmixMovieMediatorLiveData = new MediatorLiveData<>();
        filmixMovieMediatorLiveData.addSource(movieDao.load(id), new Observer<FilmixMovie>() {
            @Override
            public void onChanged(FilmixMovie filmixMovie) {
                if (filmixMovie != null) {
                    movieGenreDao.getGenresForMovieLive(id).observeForever(new Observer<List<Genre>>() {
                        @Override
                        public void onChanged(List<Genre> genres) {
                            filmixMovie.setGenres(genres);
                            filmixMovieMediatorLiveData.setValue(filmixMovie);
                        }
                    });
                }
            }
        });
        return filmixMovieMediatorLiveData;
    }

    public LiveData<FilmixMovie> getCurrMovieRemote(int id) {
        MutableLiveData<FilmixMovie> filmixMovieMediatorLiveData = new MutableLiveData<>();
        webservice.getMovieByIdRemote(id).enqueue(new Callback<FilmixMovie>() {
            @Override
            public void onResponse(Call<FilmixMovie> call, Response<FilmixMovie> response) {
                FilmixMovie remoteMovie = response.body();
                if (remoteMovie != null) {
                    filmixMovieMediatorLiveData.postValue(remoteMovie);
                }
            }

            @Override
            public void onFailure(Call<FilmixMovie> call, Throwable t) {

            }
        });


        return filmixMovieMediatorLiveData;
    }


    public void save(FilmixMovie movie) {
        new InsertMovieAsyncTask(movieDao).execute(movie);
    }

    public void clearMovieData() {
        executor.execute(
                () -> {
                    personMovieDao.deleteAllMoviePersons();
                    movieGenreDao.deleteAllMovieGenres();
                    movieDao.deleteAllMovies();
                }
        );
    }

    public Executor getExecutor() {
        return executor;
    }

    private void saveActorsForMovie(
            ContainerEntity personOrMovie,
            List<Integer> personIds,
            List<Person> savedPersonList) {
        for (Integer personId : personIds) {
            Person person = personDao.getPerson(personId);
            if (person != null) {
                savedPersonList.add(person);
                personMovieDao.insert(new PersonMoviesJoin(personId, personOrMovie.getId()));
            } else {
                try {
                    Response<Person> response = webservice.getPersonByIdRemote(personId).execute();
                    if (response.body() != null) {
                        personDao.save(response.body());
                        Person actor = personDao.getPerson(personId);
                        savedPersonList.add(actor);
                        personMovieDao.insert(
                                new PersonMoviesJoin(personId, personOrMovie.getId())
                        );
                    }

                } catch (IOException e) {

                }
            }
        }
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
                try {
                    Response<Genre> response = webservice.getGenreByIdRemote(genreId).execute();
                    if (response.body() != null) {
                        genreDao.save(response.body());
                        Genre genre_1 = genreDao.getGenre(genreId);
                        savedGenreList.add(genre_1);
                        if (joinEntity.getClass().isAssignableFrom(PersonMoviesJoin.class)) {
                            personMovieDao.insert(new PersonMoviesJoin(personOrMovie.getId(), genreId));
                        } else if (joinEntity.getClass().isAssignableFrom(MovieGenreJoin.class)) {
                            movieGenreDao.insert(new MovieGenreJoin(personOrMovie.getId(), genreId));
                        }
                    }

                } catch (IOException e) {
                }
            }
        }
    }

    public void updateMovieData(FilmixMovie movie) {
        webservice.getMovieByIdRemote(movie.getId()).enqueue(new Callback<FilmixMovie>() {
            @Override
            public void onResponse(Call<FilmixMovie> call, Response<FilmixMovie> response) {
                FilmixMovie remoteMovie = response.body();
                if (remoteMovie != null) {
                    executor.execute(() -> saveActorsForMovie(movie, remoteMovie.getActorsId(), new ArrayList<>()));
                }
            }

            @Override
            public void onFailure(Call<FilmixMovie> call, Throwable t) {

            }
        });

    }

    public void updateMovieDataRemote(FilmixMovie movie) {
        MutableLiveData<List<Person>> actorsLiveData = new MutableLiveData<>();
        MutableLiveData<List<Genre>> genresLiveData = new MutableLiveData<>();
        if (movie.getActorsId() != null) {
            List<Person> actors = new ArrayList<>();
            for (Integer actorId : movie.getActorsId()) {
                webservice.getPersonByIdRemote(actorId).enqueue(new Callback<Person>() {
                    @Override
                    public void onResponse(Call<Person> call, Response<Person> response) {
                        if (response.body() != null) {
                            actors.add(response.body());
                            actorsLiveData.postValue(actors);
                        }
                    }

                    @Override
                    public void onFailure(Call<Person> call, Throwable t) {

                    }
                });
            }
            movie.setActors(actors);
            movie.setActorsLiveData(actorsLiveData);
        }

        if (movie.getGenresId() != null) {
            List<Genre> genres = new ArrayList<>();
            for (Integer genreId : movie.getGenresId()) {
                webservice.getGenreByIdRemote(genreId).enqueue(new Callback<Genre>() {
                    @Override
                    public void onResponse(Call<Genre> call, Response<Genre> response) {
                        if (response.body() != null) {
                            genres.add(response.body());
                            genresLiveData.postValue(genres);
                        }
                    }

                    @Override
                    public void onFailure(Call<Genre> call, Throwable t) {

                    }
                });
            }
            movie.setGenres(genres);
            movie.setGenresLiveData(genresLiveData);
        }


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

    private class SaveDataForMovieAsyncTask extends AsyncTask<List<FilmixMovie>, Void, Void> {
        private MovieDao movieDao;
        private GenreDao genreDao;
        private PersonDao personDao;
        private PersonMovieDao personMovieDao;
        private MovieGenreDao movieGenreDao;
        private RatingDao ratingDao;
        private MovieWebService webservice;
        private MoviesBoundaryCallback boundaryCallback;

        private SaveDataForMovieAsyncTask(MovieDao movieDao, PersonDao personDao, GenreDao genreDao, PersonMovieDao personMovieDao, MovieGenreDao movieGenreDao, RatingDao ratingDao, MovieWebService webservice, MoviesBoundaryCallback boundaryCallback) {
            this.movieDao = movieDao;
            this.movieGenreDao = movieGenreDao;
            this.genreDao = genreDao;
            this.boundaryCallback = boundaryCallback;
            this.webservice = webservice;
            this.personDao = personDao;
            this.personMovieDao = personMovieDao;
            this.ratingDao = ratingDao;
        }


        @Override
        protected Void doInBackground(List<FilmixMovie>... movies) {
            for (FilmixMovie movie : movies[0]) {
                movie.setNameLowerCase(movie.getName().toLowerCase());// костыль но сойдет
                movieDao.save(movie);

                List<Integer> genreIds = movie.getGenresId();
                List<Integer> actorIds = movie.getActorsId();
                List<Genre> genresOfFilms = new ArrayList<>();
                List<Person> actorsInFilm = new ArrayList<>();
                // TODO: 25.05.2019 Live Data observer for Genres
                MovieRepository.this.saveGenresForEntity(movie, genreIds, genresOfFilms, new MovieGenreJoin(0, 0));
//                MovieRepository.this.saveActorsForMovie(movie, actorIds, actorsInFilm);
//                saveActorsForMovie(movie, actorIds, actorsInFilm);


            }
            return null;
        }


        @Override
        protected void onPostExecute(Void aVoid) {
            boundaryCallback.postNetworkState("MOVIES_RECEIVED");
        }
    }


    public LiveData<List<Genre>> getAllGenres() {
        return genreDao.getAllGenres();
    }

    public void loadAllGenres() {
        webservice.loadGenres().enqueue(new Callback<List<Genre>>() {
            @Override
            public void onResponse(Call<List<Genre>> call, Response<List<Genre>> response) {
                List<Genre> genres = response.body();
                if (genres != null) {
                    executor.execute(
                            () -> {
                                genreDao.save(genres);
                            }
                    );
                }
            }

            @Override
            public void onFailure(Call<List<Genre>> call, Throwable t) {

            }
        });
    }

    public void updateStateOfRemoteDB() {
        webservice.getStateOfRemoteDB().enqueue(new Callback<StateOfRemoteDB>() {
            @Override
            public void onResponse(Call<StateOfRemoteDB> call, Response<StateOfRemoteDB> response) {
                if (response.body() != null) {
                    executor.execute(
                            () -> {
                                stateOfRemoteDBdao.save(response.body());
                            }
                    );
                }
            }

            @Override
            public void onFailure(Call<StateOfRemoteDB> call, Throwable t) {

            }
        });
    }

    public void addSomeDataFromServer(String query, int page, int maxResult, PagingRequestHelper.RequestType type, PagingRequestHelper helper) {

        helper.runIfNotRunning(type, callback ->
                {
                    boundaryCallback.postNetworkState("MOVIES_UPDATING");
                    webservice.getNewMovies(query, page, maxResult).enqueue(new Callback<List<FilmixMovie>>() {
                        @Override
                        public void onResponse(Call<List<FilmixMovie>> call, Response<List<FilmixMovie>> response) {
                            Log.d("TAG", "DATA REFRESHED FROM NETWORK");


                            List<FilmixMovie> listResponse = response.body();
                            if (listResponse != null) {
                                Log.d("MOVIES", listResponse.toString());
                                atomicLastId.set(listResponse.get(listResponse.size() - 1).getId());
                                currentStateOfLocalDB.setLastId(atomicLastId.get());
                                executor.execute(() -> stateOfLocalDBdao.save(currentStateOfLocalDB));

                                SaveDataForMovieAsyncTask asyncTask = new
                                        SaveDataForMovieAsyncTask(movieDao, personDao, genreDao, personMovieDao, movieGenreDao, ratingDao, webservice, boundaryCallback);
                                asyncTask.execute(listResponse);

                                callback.recordSuccess();
                            }
                        }

                        @Override
                        public void onFailure(Call<List<FilmixMovie>> call, Throwable t) {
                            Log.d("MOVIES_ERROR", call.toString());
                            boundaryCallback.postNetworkState("MOVIES_ERROR");
                            callback.recordFailure(t);
                        }
                    });
                }
        );
    }

    public void addSomeDataFromServer(int page, int maxResult, PagingRequestHelper.RequestType type, PagingRequestHelper helper) {

        helper.runIfNotRunning(type, callback ->
                {
                    boundaryCallback.postNetworkState("MOVIES_UPDATING");
                    webservice.getNewMovies(page, maxResult).enqueue(new Callback<List<FilmixMovie>>() {
                        @RequiresApi(api = Build.VERSION_CODES.N)
                        @Override
                        public void onResponse(Call<List<FilmixMovie>> call, Response<List<FilmixMovie>> response) {
                            Log.d("TAG", "DATA REFRESHED FROM NETWORK");


                            List<FilmixMovie> listResponse = response.body();
                            if (listResponse != null) {
                                Log.d("MOVIES", listResponse.toString());
                                listResponse.sort((o1, o2) -> {
                                    int result = o2.getUploadDate().compareTo(o1.getUploadDate());
                                    if (result == 0)
                                        result = ((Integer) o1.getId()).compareTo((Integer) o2.getId());
                                    return result;
                                });
                                atomicLastId.set(listResponse.get(listResponse.size() - 1).getId());


                                currentStateOfLocalDB.setLastId(atomicLastId.get());
                                executor.execute(() -> stateOfLocalDBdao.save(currentStateOfLocalDB));

                                SaveDataForMovieAsyncTask asyncTask = new
                                        SaveDataForMovieAsyncTask(movieDao, personDao, genreDao, personMovieDao, movieGenreDao, ratingDao, webservice, boundaryCallback);
                                asyncTask.execute(listResponse);

                                callback.recordSuccess();
                            }
                        }

                        @Override
                        public void onFailure(Call<List<FilmixMovie>> call, Throwable t) {
                            Log.d("MOVIES_ERROR", call.toString());
                            boundaryCallback.postNetworkState("MOVIES_ERROR");
                            callback.recordFailure(t);
                        }
                    });
                }
        );
    }

    public LiveData<List<Person>> getActorsForMovie(int movieId) {
        return personMovieDao.getPersonsForMovies(movieId);
    }

    public LiveData<String> networkStateMessage() {
        return boundaryCallback.networkState();
    }

    public boolean retryDataTransmission() {
        return boundaryCallback.retryFailed("ATTEMPT_TO_RECEIVE_DATA");
    }

    public void loadCountyList() {
        webservice.loadCountryList().enqueue(new Callback<List<String>>() {
            @Override
            public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                List<String> countries = response.body();
                if (countries != null) {
                    for (String country : countries) {
                        Country c = new Country(countries.indexOf(country), country);
                        executor.execute(
                                () -> countryDao.save(c)
                        );
                    }
                }
            }

            @Override
            public void onFailure(Call<List<String>> call, Throwable t) {

            }
        });
    }

    public LiveData<List<Country>> getCountryLiveData() {
        return countryDao.getAllCountries();
    }

    public LiveData<PagedList<FilmixMovie>> getPopularMovies(PagedList.Config config) {
        FilmsServerDataSourceFactory filmsServerDataSourceFactory = new FilmsServerDataSourceFactory(webservice,30, fromString("POPULAR_MOVIES"));
        return new LivePagedListBuilder<Integer, FilmixMovie>(filmsServerDataSourceFactory, config).setFetchExecutor(executor).build();
    }

    public LiveData<PagedList<FilmixMovie>> getLastMovies(PagedList.Config config) {
        FilmsServerDataSourceFactory filmsServerDataSourceFactory = new FilmsServerDataSourceFactory(webservice,30, fromString("LAST_MOVIES"));
        return new LivePagedListBuilder<Integer, FilmixMovie>(filmsServerDataSourceFactory, config).setFetchExecutor(executor).build();
    }

    public LiveData<PagedList<FilmixMovie>> getLastTvSeries(PagedList.Config config) {
        FilmsServerDataSourceFactory filmsServerDataSourceFactory = new FilmsServerDataSourceFactory(webservice,30, fromString("LAST_TV_SERIES"));
        return new LivePagedListBuilder<Integer, FilmixMovie>(filmsServerDataSourceFactory, config).setFetchExecutor(executor).build();
    }

    public LiveData<PagedList<FilmixMovie>> getAllMoviesRemote(PagedList.Config config) {
        FilmsServerDataSourceFactory filmsServerDataSourceFactory = new FilmsServerDataSourceFactory(webservice,30, fromString("ALL_MOVIES"));
        return new LivePagedListBuilder<Integer, FilmixMovie>(filmsServerDataSourceFactory, config).setFetchExecutor(executor).build();
    }

    public LiveData<PagedList<FilmixMovie>> getAllTvSeriesRemote(PagedList.Config config) {
        FilmsServerDataSourceFactory filmsServerDataSourceFactory = new FilmsServerDataSourceFactory(webservice,30, fromString("ALL_TV_SERIES"));
        return new LivePagedListBuilder<Integer, FilmixMovie>(filmsServerDataSourceFactory, config).setFetchExecutor(executor).build();
    }
}
