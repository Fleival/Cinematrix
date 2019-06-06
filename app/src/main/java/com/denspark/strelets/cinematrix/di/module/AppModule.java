package com.denspark.strelets.cinematrix.di.module;

import android.app.Application;
import androidx.room.Room;
import com.denspark.strelets.cinematrix.api.MovieWebService;
import com.denspark.strelets.cinematrix.database.MovieDatabase;
import com.denspark.strelets.cinematrix.database.dao.*;
import com.denspark.strelets.cinematrix.database.entity.Genre;
import com.denspark.strelets.cinematrix.repository.MovieRepository;
import com.google.gson.*;
import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import javax.inject.Singleton;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Module(includes = ViewModelModule.class)
public class AppModule {

    // --- DATABASE INJECTION ---

    @Provides
    @Singleton
    MovieDatabase provideDatabase(Application application) {
        return Room.databaseBuilder(application,
                MovieDatabase.class, "MovieDatabase.db")
                .build();
    }

    @Provides
    @Singleton
    MovieDao provideMovieDao(MovieDatabase database) {
        return database.movieDao();
    }

    @Provides
    @Singleton
    PersonGenreDao providePersonGenreDao(MovieDatabase database) {
        return database.personGenreDao();
    }

    @Provides
    @Singleton
    PersonDao providePersonDao(MovieDatabase database) {
        return database.personDao();
    }

    @Provides
    @Singleton
    GenreDao provideGenreDao(MovieDatabase database) {
        return database.genreDao();
    }

    @Provides
    @Singleton
    MovieGenreDao provideMovieGenreDao(MovieDatabase database) {
        return database.movieGenreDao();
    }

    // --- REPOSITORY INJECTION ---

    @Provides
    Executor provideExecutor() {
        return Executors.newSingleThreadExecutor();
    }

    @Provides
    @Singleton
    MovieRepository provideMovieRepository(
            MovieDao movieDao,
            PersonDao personDao,
            GenreDao genreDao,
            PersonGenreDao personGenreDao,
            MovieGenreDao movieGenreDao,
            MovieWebService webservice,
            Executor executor) {

        return new MovieRepository(
                movieDao,
                personDao,
                genreDao,
                personGenreDao,
                movieGenreDao,
                webservice,
                executor);
    }

    // --- NETWORK INJECTION ---
    // TODO: 10.04.2019  Make Retrofit database update from server
    private static String BASE_URL = "http://192.168.43.220:8080/";

    @Provides
    Gson provideGson() {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {

            @Override
            public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                    throws JsonParseException {

                SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                String date = json.getAsJsonPrimitive().getAsString();
                try {
                    return format.parse(date);
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        return builder.create();
    }

    @Provides
    Retrofit provideRetrofit(Gson gson) {
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create(gson))
                .baseUrl(BASE_URL)
                .build();
        return retrofit;
    }

    @Provides
    @Singleton
    MovieWebService provideApiWebService(Retrofit restAdapter) {
        return restAdapter.create(MovieWebService.class);
    }
}
