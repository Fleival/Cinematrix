package com.denspark.strelets.cinematrix.not_for_production;

import androidx.lifecycle.MutableLiveData;
import com.denspark.strelets.cinematrix.api.FilmixApi;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class HttpController {
    FilmixDecoder decoder = new FilmixDecoder();
    //    String BASE_URL = "http://filmix.cc";
    String BASE_URL;
    FilmixApi filmixApi;

    HashSet<String> cookiesSet = new HashSet<>();
    String FILMIXNET_cookie = "";
    String x424_cookie = "";

    Gson gson;
    private MutableLiveData<Map<String, String>> data;
    Map<String, String> decodedValues = new LinkedHashMap<>();

    public HttpController(String BASE_URL, MutableLiveData<Map<String, String>> data) {
        this.data = data;
        initHttpClient(BASE_URL);
    }

    public void initHttpClient(String BASE_URL) {
        this.BASE_URL = BASE_URL;
        gson = new GsonBuilder()
                .setLenient()
                .create();


        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
                    @Override public okhttp3.Response intercept(Chain chain) throws IOException {
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

    private void readResponse(String json) {
        Type mapType = new TypeToken<Map<String, String>>() {
        }.getType();
        Map<String, Map<String, String>> map = gson.fromJson(json, mapType);
        System.out.println(map);
    }

    private void readResponse_2(String json) { // TODO: 03.06.2019 null check needed
        Map<String, Object> data = gson.fromJson(json, Map.class); // parse
        JsonObject jsonTree = (JsonObject) gson.toJsonTree(data);
        JsonObject video = jsonTree.get("message").getAsJsonObject()
                .get("translations").getAsJsonObject()
                .get("video").getAsJsonObject();
        Set<Map.Entry<String, JsonElement>> es = video.entrySet();

        for (Map.Entry<String, JsonElement> data_1 : es) {
            String key = data_1.getKey();
            String value = decoder.urlDecodeBase64_v2(data_1.getValue().getAsString());
            ArrayList<String> stringArrayList = new ArrayList<>();
            try {
                for (String s : value.split("\\[[\\d]{3,4}[\\w]\\]")) {
                    if (!s.equals("")) {
                        if (s.endsWith(",")) {
                            s = s.substring(0, s.length() - 1);
                        }
                        stringArrayList.add(s);
                    }
                }


            } catch (PatternSyntaxException ex) {
                // Syntax error in the regular expression
            }

            decodedValues.put("URL", stringArrayList.get(0));
            this.data.setValue(decodedValues);
            System.out.println(key + " ***:*** " + stringArrayList);
        }
//        System.out.println(es);
    }

    private void getMovieData_1(String moviePath) {
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
        String stringPostBody = "post_id=" + id + "&showfull=true";
        decodedValues.put("Referer", BASE_URL + moviePath);
        Call<ResponseBody> movieDataWithHeadersCall = filmixApi.loadMovieDataWithHeaders(
                BASE_URL + moviePath,
                "XMLHttpRequest",
                "application/x-www-form-urlencoded; charset=UTF-8",
                "FILMIXNET=" + FILMIXNET_cookie + "; x424=" + x424_cookie,
                stringPostBody);
        movieDataWithHeadersCall.enqueue(new Callback<ResponseBody>() {
            @Override public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    String movieData = null;
                    try {
                        movieData = response.body().string();
                        readResponse_2(movieData);
//                        System.out.println(movieData);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                } else {
                    System.out.println(response.errorBody());
                }
            }

            @Override public void onFailure(Call<ResponseBody> call, Throwable throwable) {
                throwable.printStackTrace();
            }
        });
    }


    public void getMovieData(String path) {
        Call<ResponseBody> getCookies = filmixApi.getCookies(path);
        getCookies.enqueue(new Callback<ResponseBody>() {
            @Override public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
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
                getMovieData_1(path);
            }

            @Override public void onFailure(Call<ResponseBody> call, Throwable throwable) {

            }
        });
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
