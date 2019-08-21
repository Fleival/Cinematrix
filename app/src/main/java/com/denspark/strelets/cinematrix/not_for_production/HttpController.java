package com.denspark.strelets.cinematrix.not_for_production;

import android.os.Build;
import android.os.Handler;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.denspark.strelets.cinematrix.api.FilmixApi;
import com.denspark.strelets.cinematrix.not_for_production.playlist.Episode;
import com.denspark.strelets.cinematrix.not_for_production.playlist.File;
import com.denspark.strelets.cinematrix.not_for_production.playlist.Playlist;
import com.denspark.strelets.cinematrix.not_for_production.playlist.Season;
import com.denspark.strelets.cinematrix.not_for_production.playlist.Translations;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class HttpController {
    private static final String TAG = "HTTP_CONTROLLER";
    private FilmixDecoder decoder = new FilmixDecoder();
    private String BASE_URL;
    private FilmixApi filmixApi;

    private HashSet<String> cookiesSet = new HashSet<>();
    private String FILMIXNET_cookie = "";
    private String x424_cookie = "";

    private Gson gson;

    private MutableLiveData<Playlist> moviePlsData;
    private MutableLiveData<Playlist> serialPlsData;
    private MutableLiveData<Map<String, String>> data;

    public HttpController(String BASE_URL,
                          MutableLiveData<Playlist> moviePlsData,
                          MutableLiveData<Playlist> serialPlsData,
                          MutableLiveData<Map<String, String>> data) {
        this.moviePlsData = moviePlsData;
        this.serialPlsData = serialPlsData;
        this.data = data;
        initHttpClient(BASE_URL);
    }

    private void initHttpClient(String BASE_URL) {
        this.BASE_URL = BASE_URL;
        gson = new GsonBuilder()
                .setLenient()
                .create();


        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public okhttp3.Response intercept(Chain chain) throws IOException {
                        okhttp3.Response originalResponse = chain.proceed(chain.request());

                        if (!originalResponse.headers("Set-Cookie").isEmpty()) {
                            HashSet<String> cookies = new HashSet<>();
                            for (String header : originalResponse.headers("Set-Cookie")) {
                                cookies.add(header);
                            }
                            cookiesSet.addAll(cookies);
                        }
                        return originalResponse;
                    }
                })
//                .addInterceptor(logging)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .client(client)
                .build();

        filmixApi = retrofit.create(FilmixApi.class);

    }


    private Map<String, String> getFiles(String inputLine) {
        String[] splitArray = null;
        Map<String, String> qualityPls = new LinkedHashMap<>();
        try {
            Pattern regex = Pattern.compile("\\[[\\d]{3,4}(\\s?)([\\w]{1,2})\\]");
            Matcher regexMatcher = regex.matcher(inputLine);
            splitArray = regex.split(inputLine);
            int j = 1;
            if (splitArray.length == 1) {
                j = 0;
            }

            while (regexMatcher.find()) {
                String quality = regexMatcher.group();
                String file = splitArray[j];
                if (!file.equals("")) {
                    if (file.endsWith(",")) {
                        file = file.substring(0, file.length() - 1);
                    }
                    if (!file.endsWith("txt")) {
                        qualityPls.put(quality, file);
                    }
                    j++;
                }
            }
        } catch (PatternSyntaxException ex) {
        }
        return qualityPls;
    }

    private List<File> getFileList(String inputLine) {
        List<File> fileList = new ArrayList<>();
        for (Map.Entry<String, String> entry : getFiles(inputLine).entrySet()) {
            File file = new File(entry.getKey(), entry.getValue());
            fileList.add(file);
        }
        return fileList;
    }

    private void getMovieDataWithCookies(String moviePath) {
        int id = getMovieId(moviePath);
        String stringPostBody = "post_id=" + id + "&showfull=true";
        Map<String,String> value = new LinkedHashMap<String, String>();
        value.put("Referer", BASE_URL + moviePath);
        this.data.setValue(value);
        Call<ResponseBody> movieDataWithHeadersCall = filmixApi.loadMovieDataWithHeaders(
                BASE_URL + moviePath,
                "XMLHttpRequest",
                "application/x-www-form-urlencoded; charset=UTF-8",
                "FILMIXNET=" + FILMIXNET_cookie + "; x424=" + x424_cookie,
                stringPostBody);
        movieDataWithHeadersCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    String movieData = null;
                    try {
                        movieData = response.body().string();
                        Playlist playlistObj = gson.fromJson(movieData, new TypeToken<Playlist>() {
                        }.getType());
                        Translations translations = playlistObj.getMessage().getTranslations();
                        Map<String, String> videoMap = translations.getVideo();
                        for (String translationName : videoMap.keySet()) {
                            String decodedValue = decoder.urlDecodeBase64_v3(videoMap.get(translationName));
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                videoMap.replace(translationName, decodedValue);
                            }
                        }

                        if (translations.getPl().equals("yes")) {
                            System.out.println("This is serial");
                            makeSerialPlaylist(playlistObj);

                        }
                        if (translations.getPl().equals("no")) {
                            System.out.println("This is movie");
                            makeMoviePlaylist(playlistObj);

                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }catch (JsonSyntaxException e1){
                        Log.e(TAG, "onResponse: JSON is incorrect , might be some mistakes in source API server");
                    }

                } else {
                    System.out.println(response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable throwable) {
                throwable.printStackTrace();
            }
        });
    }

    private void makeSerialPlaylist(Playlist playlist) {
        Translations translations =playlist.getMessage().getTranslations();
                System.out.println(translations);
        Map<String, List<Season>> translationSeasonMap = new LinkedHashMap<>();
        for (String translation : translations.getVideo().keySet()) {
            String plsUrl = translations.getVideo().get(translation).replaceAll(BASE_URL, "");
            filmixApi.getPlaylist(plsUrl).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                    try {
                        String decodedValue = decoder.urlDecodeBase64_v3(Objects.requireNonNull(response.body()).string());
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            translations.getVideo().replace(translation,decodedValue);
                        }
                        List<Season> seasons = Arrays.asList(gson.fromJson(decodedValue, Season[].class));

                        for (Season season : seasons) {
                            for (Episode episode : season.getFolder()) {
                                episode.setFiles(getFileList(episode.getFile()));
                            }
                        }
                        translationSeasonMap.put(translation,seasons);
                        translations.setSeasons(translationSeasonMap);
                        serialPlsData.postValue(playlist);
//                        System.out.println("End of making");

                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (NullPointerException nullException){
                        Log.e(TAG, "onResponse: Incorrect response obtained");
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Log.d(TAG, "onFailure: Serial playlist txt url incorrect: " + call);
                }
            });
        }
    }

    private void makeMoviePlaylist(Playlist playlist) {
        Translations translations =playlist.getMessage().getTranslations();
        Map<String, List<File>> fileList = new LinkedHashMap<>();
        for (String translation : translations.getVideo().keySet()) {
            fileList.put(translation, getFileList(translations.getVideo().get(translation)));
            translations.setVideoFileList(fileList);
        }
        moviePlsData.postValue(playlist);

    }

    private int getMovieId(String moviePath) {
        int id = 0;
        Pattern pattern = Pattern.compile("/(\\d+)");
        Matcher matcher = pattern.matcher(moviePath);
        if (matcher.find()) {
            Pattern pattern1 = Pattern.compile("(\\d+)");
            Matcher matcher2 = pattern1.matcher(matcher.group(1));
            if (matcher2.find()) {
                id = Integer.valueOf(matcher.group(1));
            }
        }
        return id;
    }


    public void getMovieData(String path) {
        Call<ResponseBody> getCookies = filmixApi.getCookies(path);
        getCookies.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                for (String s : cookiesSet) {
                    if (s.contains("FILMIXNET")) {
                        FILMIXNET_cookie = getCookieValue("FILMIXNET", s);
                        System.out.println(FILMIXNET_cookie);
                    }
                    if (s.contains("x424")) {
                        x424_cookie = getCookieValue("x424", s);
                        System.out.println(x424_cookie);
                    }
                }
                getMovieDataWithCookies(path);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable throwable) {

            }
        });
    }
    public void getMovieData(String path, long delay) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getMovieData(path);
            }
        }, delay);
    }

    private String getCookieValue(String key, String cookieLine) {
        String cookieValue = "0";
        Pattern pattern = Pattern.compile(key + "=(.*?);");
        Matcher matcher = pattern.matcher(cookieLine);
        while (matcher.find()) {
            return matcher.group(1);
        }
        return cookieValue;
    }

}
