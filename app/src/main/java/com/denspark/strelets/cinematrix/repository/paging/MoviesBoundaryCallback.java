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

    private int start;
    private int maxRows;
    private int check = 60;

    private String query = "SELECT f FROM Film f ORDER BY f.id ASC";

    private MovieRepository repository;
    private PagingRequestHelper helper;

    private MutableLiveData<String> networkErrors = new MutableLiveData<>();


    public MoviesBoundaryCallback(MovieRepository repository) {

        this.repository = repository;

        Executor executor = Executors.newSingleThreadExecutor();
        helper = new PagingRequestHelper(executor);
    }

    @Override
    public void onZeroItemsLoaded() {
        start = 0;
        maxRows = 60;
        repository.addSomeDataFromServer(query, start, maxRows, PagingRequestHelper.RequestType.INITIAL, helper);
//        requestAndSave(PagingRequestHelper.RequestType.INITIAL);
    }

    // TODO: 14.06.2019 This is piece of shit
    @Override public void onItemAtEndLoaded(@NonNull FilmixMovie itemAtEnd) {
        int itemAtEndId = itemAtEnd.getId();
        if (itemAtEndId == check) {
            check = check + 30;
            Log.d(TAG, "onItemAtEndLoaded: " + itemAtEnd.getId());
            repository.addSomeDataFromServer(query, itemAtEndId, 30, PagingRequestHelper.RequestType.AFTER, helper);
        }
    }


    LiveData<String> networkErrors() {
        return networkErrors;
    }

}
