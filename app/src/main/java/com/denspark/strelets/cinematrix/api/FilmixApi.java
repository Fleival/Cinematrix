package com.denspark.strelets.cinematrix.api;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.*;

public interface FilmixApi {

    @Headers({
            "Referer: http://filmix.cc/music/133920-neizvestnyy-sluchay-s-bratyami-bash-2019.html",
            "X-Requested-With: XMLHttpRequest",
            "Content-Type: application/x-www-form-urlencoded; charset=UTF-8",
            "Cookie: FILMIXNET=8q50sfsqhkhcdmdq9b2cn985c5; muidn=j42E2lxDtzH9; sc34-market=f096179b826ae18999aca75bbcbe3a33; x424=a5fdca5bc5b395464ca620d5f84a7799"
    })
    @POST("/api/movies/player_data")
    Call<ResponseBody> loadMovieData(@Body String body);

    @POST("/api/movies/player_data")
    Call<ResponseBody> loadMovieDataWithHeaders(
            @Header("Referer") String referer,
            @Header("X-Requested-With") String xReq_With,
            @Header("Content-Type") String contentType,
            @Header("Cookie") String cookie,
            @Body String body);

    @GET("{path}")
    Call<ResponseBody> getCookies(@Path("path") String path);

    @GET("{path}")
    Call<ResponseBody> getPlaylist(@Path("path") String path);
}
