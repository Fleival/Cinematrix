package com.denspark.strelets.cinematrix.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.denspark.strelets.cinematrix.R;
import com.denspark.strelets.cinematrix.database.entity.FilmixMovie;
import com.denspark.strelets.cinematrix.database.entity.Genre;
import com.denspark.strelets.cinematrix.glide.GlideApp;
import com.denspark.strelets.cinematrix.not_for_production.HttpController;
import com.denspark.strelets.cinematrix.utils.DimensionUtils;
import com.denspark.strelets.cinematrix.view_models.FactoryViewModel;
import com.denspark.strelets.cinematrix.view_models.MovieViewModel;
import dagger.android.AndroidInjection;
import jp.wasabeef.glide.transformations.CropTransformation;
import jp.wasabeef.glide.transformations.SupportRSBlurTransformation;

import javax.inject.Inject;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class MovieActivity extends AppCompatActivity {

    private static final String TAG = "MovieActivity";

    public static final String EXTRA_TITLE =
            "com.denspark.strelets.cinematrix.activities.movie.EXTRA_TITLE";

    public static final String EXTRA_DESCRIPTION =
            "com.denspark.strelets.cinematrix.activities.movie.EXTRA_DESCRIPTION";

    public static final String EXTRA_ID =
            "com.denspark.strelets.cinematrix.activities.movie.EXTRA_ID";

    public static final String EXTRA_POSTER_URL = "com.denspark.strelets.cinematrix.activities.movie.EXTRA_POSTER_URL";

    String referer;
    String URL;

    private MutableLiveData<Map<String, String>> data = new MutableLiveData<>();


    @Inject
    FactoryViewModel viewModelFactory;
    private MovieViewModel movieViewModel;

    @BindView(R.id.movie_layout_toolbar)
    Toolbar movieLayoutToolbar;

    @BindView(R.id.movie_poster)
    ImageView movieLayoutPoster;

    @BindView(R.id.movie_bl_bg)
    ImageView movieLayoutPosterBG;

    @BindView(R.id.movie_title_d_tv)
    TextView movie_title_d_tv;

    @BindView(R.id.movie_year)
    TextView movie_year_d_tv;

    @BindView(R.id.movie_country)
    TextView movie_country_d_tv;

    @BindView(R.id.movie_genre)
    TextView movie_genre_d_tv;

    @BindView(R.id.movie_duration)
    TextView movie_duration_d_tv;

    @BindView(R.id.movie_description)
    TextView movie_description_d_tv;

    @BindView(R.id.play_btn)
    Button playButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie);
        ButterKnife.bind(this);

        setSupportActionBar(movieLayoutToolbar);
        playButton.setEnabled(false);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Intent intent = getIntent();
        this.configureDagger();

        if (intent.hasExtra(EXTRA_ID)) {
            this.configureViewModel(intent.getIntExtra(EXTRA_ID, -1));
        }

//        setMovieActivityData(intent);
        data.observe(this, new Observer<Map<String, String>>() {
            @Override public void onChanged(Map<String, String> stringStringMap) {
                referer = stringStringMap.get("Referer");
                URL = stringStringMap.get("URL");
                Log.d(TAG, "data.onChanged:\n Referer= " + referer + "\n URL= " + URL);
                playButton.setEnabled(true);
            }
        });
    }

    @Override public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


    private void configureDagger() {
        AndroidInjection.inject(this);
    }


    private void configureViewModel(int id) {
        movieViewModel = ViewModelProviders.of(this, viewModelFactory)
                .get(MovieViewModel.class);

        movieViewModel.getCurrentMovie(id).observe(this, new Observer<FilmixMovie>() {
            @Override public void onChanged(FilmixMovie filmixMovie) {
                Log.d(TAG, "onChanged: movie = " + filmixMovie);
                setMovieActivityData(filmixMovie);
                String Url = filmixMovie.getFilmPageUrl();

                String baseUrl = null;
                String[] splitUrlArray = null;
                try {
                    Pattern regex = Pattern.compile("^.+?[^\\/:](?=[?\\/]|$)");
                    Matcher regexMatcher = regex.matcher(Url);
                    splitUrlArray = regex.split(Url);
                    if (regexMatcher.find()) {
                        baseUrl = regexMatcher.group();
                    }
                } catch (PatternSyntaxException ex) {
                    // Syntax error in the regular expression
                }

                String path = splitUrlArray[1];
                Log.d(TAG, "onChanged: movie baseURL = " + baseUrl + "  /  " + path);
                HttpController controller = new HttpController(baseUrl, data);

                controller.getMovieData(path);

            }
        });
    }

    private void setMovieActivityData(FilmixMovie movie) {


        MultiTransformation<Bitmap> bitmapMultiTransformation =
                new MultiTransformation<>(
                        new SupportRSBlurTransformation(10, 3),
                        new CropTransformation(
                                DimensionUtils.dpToPx(this, 360),
                                DimensionUtils.dpToPx(this, 200),
                                CropTransformation.CropType.TOP
                        )
                );

        MultiTransformation<Bitmap> posterMultiTransformation =
                new MultiTransformation<>(
                        new CenterCrop(),
                        new RoundedCorners(DimensionUtils.dpToPx(this, 7))
                );


        GlideApp.with(this)
                .asBitmap()
                .load(movie.getFilmPosterUrl())
                .placeholder(R.drawable.blur_poster_bg_default)
                .transform(bitmapMultiTransformation)
                .into(movieLayoutPosterBG);


        GlideApp.with(this)
                .load(movie.getFilmPosterUrl())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.poster_default)
                .transform(posterMultiTransformation)
                .override(
                        DimensionUtils.dpToPx(this, 156),
                        DimensionUtils.dpToPx(this, 216))
                .into(movieLayoutPoster);

        setTitle("");

        movie_title_d_tv.setText(movie.getName());
        movie_year_d_tv.setText(movie.getYear());
        movie_country_d_tv.setText(movie.getCountry());

// TODO: 05.06.2019  NPE here:
        if (movie.getGenres() != null) {
            String genreNames = "";
            for (Genre g : movie.getGenres()) {
                genreNames = genreNames + g.getName() + " ";
            }
            movie_genre_d_tv.setText(genreNames);
        }
        movie_duration_d_tv.setText(movie.getDuration());
        movie_description_d_tv.setText(movie.getDescriptionStory());


    }


    @OnClick(R.id.play_btn)
    void clickPlayBtn() {

        Toast.makeText(this, "Hello", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, PlayerActivity.class);
        intent.putExtra(PlayerActivity.EXTRA_REFERER, referer);
        intent.putExtra(PlayerActivity.EXTRA_VIDEO_URL, URL);
        this.startActivity(intent);
    }
}
