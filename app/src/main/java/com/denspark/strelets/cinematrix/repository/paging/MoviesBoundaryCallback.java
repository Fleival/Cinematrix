package com.denspark.strelets.cinematrix.repository.paging;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.paging.PagedList;

import com.denspark.strelets.cinematrix.database.entity.FilmixMovie;
import com.denspark.strelets.cinematrix.repository.MovieRepository;
import com.denspark.strelets.cinematrix.utils.PagingRequestHelper;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MoviesBoundaryCallback extends PagedList.BoundaryCallback<FilmixMovie> {

    private static final String TAG = "MoviesBoundaryCallback";

    private int page = 1;
    private int maxResult = 10;
    private int lastId;

//    private String query = "SELECT f from Film f ORDER BY f.uploadDate desc";

    private MovieRepository repository;
    private PagingRequestHelper helper;

    private MutableLiveData<String> networkState = new MutableLiveData<>();


    public MoviesBoundaryCallback(MovieRepository repository) {

        this.repository = repository;

        Executor executor = Executors.newSingleThreadExecutor();
        helper = new PagingRequestHelper(executor);
    }

    @Override
    public void onZeroItemsLoaded() {
        repository.atomicLastId.set(0);
        lastId = repository.atomicLastId.get();
        page=1;
        updateDataFromServer();
    }

    // TODO: 14.06.2019 Need to refactor this is piece of shit but its working
    @Override
    public void onItemAtEndLoaded(@NonNull FilmixMovie itemAtEnd) {
        lastId = repository.atomicLastId.get();

        Log.d(TAG, "onItemAtEndLoaded:  page= " + page + " itemAtEnd_Id= " + itemAtEnd.getId());
        Log.d(TAG, "lastId: " + lastId);
        if (itemAtEnd.getId() == lastId) {
            page = repository.atomicMovieCount.get()/maxResult;
            page++;
            updateDataFromServer();
        }

    }

    private void updateDataFromServer() {
        repository.addSomeDataFromServer(page, maxResult, PagingRequestHelper.RequestType.INITIAL, helper);
    }


    public LiveData<String> networkState() {
        return networkState;
    }

    public void postNetworkState(String msg) {
        networkState.postValue(msg);
    }

    public boolean retryFailed(String stateMsg) {
        postNetworkState(stateMsg);
        return helper.retryAllFailed();
    }

}
