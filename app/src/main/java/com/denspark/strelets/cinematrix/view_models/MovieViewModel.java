package com.denspark.strelets.cinematrix.view_models;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.paging.PagedList;
import com.denspark.strelets.cinematrix.app.Constants;
import com.denspark.strelets.cinematrix.database.entity.FilmixMovie;
import com.denspark.strelets.cinematrix.repository.MovieRepository;

import javax.inject.Inject;
import java.util.List;

public class MovieViewModel extends ViewModel {

    private MovieRepository repository;
    private LiveData<PagedList<FilmixMovie>> allMovies;
    private LiveData<FilmixMovie> currentMovie;

    private final static PagedList.Config config
            = new PagedList.Config.Builder()
            .setPageSize(Constants.PAGE_SIZE)
            .setInitialLoadSizeHint(Constants.PAGE_INITIAL_LOAD_SIZE_HINT)
            .setPrefetchDistance(Constants.PAGE_PREFETCH_DISTANCE)
            .setEnablePlaceholders(false)
            .build();


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
    public LiveData<PagedList<FilmixMovie>> getMovies(){
        if (allMovies == null) {
            allMovies = repository.getAllMovies(config);
        }
        return allMovies;
    }


    //just getter
    public LiveData<PagedList<FilmixMovie>> getAllMovies() {
        return allMovies;
    }

    public LiveData<FilmixMovie> getCurrentMovie(int id){
        return repository.getCurrMovie(id);
    }

    public void save(FilmixMovie movie){
        repository.save(movie);
    }

    public void addSomeTestDataFromServer(){
        repository.addSomeDataFromServer();
    }

    public void clearMovieData(){
        repository.clearMovieData();
    }
    public void reInitTestData(){
        repository.addSomeDataFromServer();
        repository.getAllMovies(config);
    }

    public void insertData(){
        repository.addSomeDataFromServer();
//        repository.getAllMovies(config);
    }

    public void testWebService(){
        repository.testWebServicePerson();
    }
    public void testDB(){
        repository.testGetPerson();
    }

}
