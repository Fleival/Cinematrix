package com.denspark.strelets.cinematrix.view_models;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;
import androidx.paging.PagedList;
import com.denspark.strelets.cinematrix.app.Constants;
import com.denspark.strelets.cinematrix.database.entity.FilmixMovie;
import com.denspark.strelets.cinematrix.database.entity.Genre;
import com.denspark.strelets.cinematrix.repository.MovieRepository;

import javax.inject.Inject;
import java.util.List;

public class MovieViewModel extends ViewModel {

    private MovieRepository repository;
    private LiveData<PagedList<FilmixMovie>> allMovies;
    private LiveData<FilmixMovie> currentMovie;
    private LiveData<List<Genre>> allGenres;

    private final static PagedList.Config config
            = new PagedList.Config.Builder()
            .setPageSize(Constants.PAGE_SIZE)
            .setInitialLoadSizeHint(Constants.PAGE_INITIAL_LOAD_SIZE_HINT)
            .setPrefetchDistance(Constants.PAGE_PREFETCH_DISTANCE)
            .setEnablePlaceholders(true)
            .build();


//    private MutableLiveData<Genre> currentGenre = new MutableLiveData<>();
//    private MutableLiveData<Integer> currentAction = new MutableLiveData<>();
//    private LiveData<PagedList<FilmixMovie>> moviesForGenrePagged = Transformations.switchMap(currentGenre,
//            (genre -> {
//                return repository.getMoviesForGenre(config, genre.getId());
//            }));

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
                if (genreList.size()>0){
                    query +="GROUP BY movie.id HAVING COUNT(DISTINCT movie_genre_join.genreId) = " +genreList.size();
                }
                return repository.getMoviesForGenre(config,query);
            }));


    @Inject
    public MovieViewModel(MovieRepository repository) {
        this.repository = repository;
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


    public LiveData<PagedList<FilmixMovie>> getMoviesForGenrePagged() {
        return moviesForGenrePagged;
    }

//    public void setGenreFilter(String genreFilter) {
//        repository.getExecutor().execute(
//                () -> {
//                    Genre filterGenre = repository.getGenre(genreFilter);
//                    currentGenre.postValue(filterGenre);
//                }
//        );
//    }



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

    public void save(FilmixMovie movie) {
        repository.save(movie);
    }

    public void addSomeTestDataFromServer() {
        repository.addSomeDataFromServer("SELECT f FROM Film f WHERE " + "f.id<=60");
    }

    public void clearMovieData() {
        repository.clearMovieData();
    }

    public void reInitTestData() {
        repository.addSomeDataFromServer("SELECT f FROM Film f WHERE " + "f.id<=60");
//        repository.getAllMovies(config);
    }

    public void insertData() {
        repository.addSomeDataFromServer("SELECT f FROM Film f WHERE " + "f.id<=60");
//        repository.getAllMovies(config);
    }

    public void testWebService() {
        repository.testWebServicePerson();
    }

    public void testDB() {
        repository.testGetPerson();
    }

    public void updateStateOfRemoteDB(){
        repository.updateStateOfRemoteDB();
    }

}
