package com.denspark.strelets.cinematrix.view_models;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;
import androidx.paging.PagedList;

import com.denspark.strelets.cinematrix.app.Constants;
import com.denspark.strelets.cinematrix.database.entity.Country;
import com.denspark.strelets.cinematrix.database.entity.FilmixMovie;
import com.denspark.strelets.cinematrix.database.entity.Genre;
import com.denspark.strelets.cinematrix.database.entity.Person;
import com.denspark.strelets.cinematrix.repository.FilmsSearchDataSource;
import com.denspark.strelets.cinematrix.repository.MovieRepository;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.inject.Inject;

public class MovieViewModel extends ViewModel {

    private boolean onlineMode = false;

    private MovieRepository repository;
    private LiveData<PagedList<FilmixMovie>> allMovies;
    private LiveData<PagedList<FilmixMovie>> filteredMovies;
    private FilmixMovie currentMovie;
    private LiveData<FilmixMovie> currentLiveDataMovie;

    private LiveData<List<Genre>> allGenres;

    private final static PagedList.Config config
            = new PagedList.Config.Builder()
            .setPageSize(Constants.PAGE_SIZE)
            .setInitialLoadSizeHint(Constants.PAGE_INITIAL_LOAD_SIZE_HINT)
            .setPrefetchDistance(Constants.PAGE_PREFETCH_DISTANCE)
            .setEnablePlaceholders(true)
            .build();

    private MutableLiveData<List<Genre>> currentGenreList = new MutableLiveData<>();
    //SELECT * FROM movie INNER JOIN movie_genre_join ON movie.id=movie_genre_join.movieId WHERE movie_genre_join.genreId IN (3,33) GROUP BY movie.id HAVING COUNT(DISTINCT movie_genre_join.genreId) = 2
    private LiveData<PagedList<FilmixMovie>> moviesForGenrePagged = Transformations.switchMap(currentGenreList,
            (genreList -> {
                String query = "SELECT * FROM movie INNER JOIN movie_genre_join ON movie.id=movie_genre_join.movieId WHERE movie_genre_join.genreId IN (";
                for (int i = 0; i < genreList.size(); i++) {
                    if (i == 0) {
                        query += genreList.get(i).getId();
                    }
                    if (i > 0) {
                        query += "," + genreList.get(i).getId();
                    }
                }
                query += ")";
                if (genreList.size() > 0) {
                    query += "GROUP BY movie.id HAVING COUNT(DISTINCT movie_genre_join.genreId) = " + genreList.size();
                }
                return repository.getMoviesForGenre(config, query);
            }));


    @Inject
    public MovieViewModel(MovieRepository repository) {
        this.repository = repository;
        currentLiveDataMovie = new MutableLiveData<>();
    }

    public void init() {
        if (this.allMovies != null) {
            return;
        }
        allMovies = repository.getAllMovies(config);
    }

    public LiveData<PagedList<FilmixMovie>> getMovies() {
        if (allMovies == null) {
            allMovies = repository.getAllMovies(config);
        }
        return allMovies;
    }

    public LiveData<List<Genre>> gelAllGenres() {
        if (allGenres == null) {
            allGenres = repository.getAllGenres();
        }
        return allGenres;
    }

    public void loadAllGenres() {
        repository.loadAllGenres();
    }


    public LiveData<PagedList<FilmixMovie>> getMoviesForGenrePagged() {
        return moviesForGenrePagged;
    }

    public void setGenreFilter(List<Genre> genreFilter) {
        repository.getExecutor().execute(
                () -> {
                    currentGenreList.postValue(genreFilter);
                }
        );
    }

    //just getter
    public LiveData<PagedList<FilmixMovie>> getAllMovies() {
        return allMovies;
    }

    public LiveData<FilmixMovie> getCurrentMovie(int id) {
        return repository.getCurrMovie(id);
    }

    public LiveData<FilmixMovie> getCurrentMovieRemote(int id) {
        return repository.getCurrMovieRemote(id);
    }

    public void save(FilmixMovie movie) {
        repository.save(movie);
    }

    public void reInitTestData() {
//        repository.addSomeDataFromServer("SELECT f FROM Film f WHERE " + "f.id<=60");
//        repository.getAllMovies(config);
    }

    public void updateStateOfRemoteDB() {
        repository.updateStateOfRemoteDB();
    }

    public LiveData<List<Person>> getActorsForMovie(int movieId) {
        return repository.getActorsForMovie(movieId);
    }

    public LiveData<PagedList<FilmixMovie>> searchMovies(String name) {
        filteredMovies = repository.searchMoviesByName(config, name);
        return filteredMovies;
    }

    public LiveData<PagedList<FilmixMovie>> searchFilteredMovies(String name, String year, String country, List<Genre> genres) {
        filteredMovies = repository.searchFilteredMovies(config, name, year, country, genres);
        return filteredMovies;
    }

    public LiveData<String> networkStateMessage() {
        return repository.networkStateMessage();
    }

    public boolean retryDataTransmission() {
        return repository.retryDataTransmission();
    }

    public FilmixMovie getCurrentMovie() {
        return currentMovie;
    }

    public void clearMovieData(){
        repository.clearMovieData();
    }

    public void setCurrentMovie(FilmixMovie currentMovie) {
        this.currentMovie = currentMovie;
    }

    public String getPath() {
        String Url = currentMovie.getFilmPageUrl();
        String[] splitUrlArray = null;
        try {
            Pattern regex = Pattern.compile("^.+?[^\\/:](?=[?\\/]|$)");
            splitUrlArray = regex.split(Url);
        } catch (PatternSyntaxException ex) {
            // Syntax error in the regular expression
        }

        String path = splitUrlArray[1];
        return path;
    }

    public String getBaseUrl() {
        String baseUrl = null;
        String Url = currentMovie.getFilmPageUrl();
        try {
            Pattern regex = Pattern.compile("^.+?[^\\/:](?=[?\\/]|$)");
            Matcher regexMatcher = regex.matcher(Url);
            if (regexMatcher.find()) {
                baseUrl = regexMatcher.group();
            }
        } catch (PatternSyntaxException ex) {
            // Syntax error in the regular expression
        }
        return baseUrl;
    }

    public void updateMovieData(FilmixMovie movie){
        repository.updateMovieData(movie);
    }

    public void updateMovieDataRemote(FilmixMovie movie){
        repository.updateMovieDataRemote(movie);
    }

    public boolean isOnlineMode() {
        return onlineMode;
    }

    public void setOnlineMode(boolean onlineMode) {
        this.onlineMode = onlineMode;
    }

    public void loadCounties(){
        repository.loadCountyList();
    }

    public LiveData<List<Country>> getCountryLiveData(){
        return repository.getCountryLiveData();
    }
}
