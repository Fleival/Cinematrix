package com.denspark.strelets.cinematrix.view.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.vectordrawable.graphics.drawable.Animatable2Compat.AnimationCallback;
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat;

import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.denspark.strelets.cinematrix.R;
import com.denspark.strelets.cinematrix.adapters.ActorsInMovieRvAdapter;
import com.denspark.strelets.cinematrix.adapters.ActorsInMovieRvAdapter.ActorClickListener;
import com.denspark.strelets.cinematrix.adapters.playlist_adapter.PlaylistTreeViewBuilder;
import com.denspark.strelets.cinematrix.database.entity.FilmixMovie;
import com.denspark.strelets.cinematrix.database.entity.Genre;
import com.denspark.strelets.cinematrix.database.entity.Person;
import com.denspark.strelets.cinematrix.glide.GlideApp;
import com.denspark.strelets.cinematrix.not_for_production.HttpController;
import com.denspark.strelets.cinematrix.not_for_production.playlist.Playlist;
import com.denspark.strelets.cinematrix.utils.DimensionUtils;
import com.denspark.strelets.cinematrix.view_models.FactoryViewModel;
import com.denspark.strelets.cinematrix.view_models.MovieViewModel;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback;

import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dagger.android.AndroidInjection;
import jp.wasabeef.glide.transformations.CropTransformation;
import jp.wasabeef.glide.transformations.SupportRSBlurTransformation;

import static java.util.Comparator.comparing;
import static java.util.Comparator.naturalOrder;
import static java.util.Comparator.nullsFirst;
import static java.util.Comparator.nullsLast;

public class MovieActivity extends AppCompatActivity implements ActorClickListener {
    public static final String EXTRA_DESCRIPTION = "com.denspark.strelets.cinematrix.view.activities.movie.EXTRA_DESCRIPTION";
    public static final String EXTRA_ID = "com.denspark.strelets.cinematrix.view.activities.movie.EXTRA_ID";
    public static final String EXTRA_ONLINE_MODE = "com.denspark.strelets.cinematrix.view.activities.movie.EXTRA_ONLINE_MODE";
    public static final String EXTRA_POSTER_URL = "com.denspark.strelets.cinematrix.view.activities.movie.EXTRA_POSTER_URL";
    public static final String EXTRA_TITLE = "com.denspark.strelets.cinematrix.view.activities.movie.EXTRA_TITLE";
    private static final String TAG = "MovieActivity";


    /* access modifiers changed from: private */
    public ActorsInMovieRvAdapter actorsAdapter;
    LinearLayoutManager actorsLayoutManager;

    @BindView(R.id.actors_rec_view)
    RecyclerView actorsRecyclerView;

    AnimationCallback animationCallback;

    public HttpController controller;

    public MutableLiveData<Map<String, String>> data = new MutableLiveData<>();
    Drawable drawable;

    @BindView(R.id.movie_poster)
    ImageView movieLayoutPoster;
    @BindView(R.id.movie_bl_bg)
    ImageView movieLayoutPosterBG;
    @BindView(R.id.movie_layout_toolbar)
    Toolbar movieLayoutToolbar;

    public MutableLiveData<Playlist> moviePlsData = new MutableLiveData<>();
    public MovieViewModel movieViewModel;

    @BindView(R.id.movie_country)
    TextView movie_country_d_tv;
    @BindView(R.id.movie_description)
    TextView movie_description_d_tv;
    @BindView(R.id.movie_duration)
    TextView movie_duration_d_tv;
    @BindView(R.id.movie_genre)
    TextView movie_genre_d_tv;
    @BindView(R.id.movie_title_d_tv)
    TextView movie_title_d_tv;
    @BindView(R.id.movie_year)
    TextView movie_year_d_tv;
    @BindView(R.id.pos_rating_value_a_movie)
    TextView posRatingValueAMovie;
    @BindView(R.id.neg_rating_value_a_movie)
    TextView negRatingValueAMovie;
    @BindView(R.id.play_btn)
    Button playButton;
    @BindView(R.id.playlist_container)
    ViewGroup playListContainer;
    BottomSheetBehavior plsBottomSheetBehavior;

    @BindView(R.id.pls_bottom_sheet)
    RelativeLayout plsBottomSheetLayout;
    @BindView(R.id.progress_indicator)
    ImageView progressIndicator;
    String referer;
    public MutableLiveData<Playlist> serialPlsData = new MutableLiveData<>();
    PlaylistTreeViewBuilder treeViewBuilder;
    @Inject
    FactoryViewModel viewModelFactory;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie);
        ButterKnife.bind((Activity) this);
        setSupportActionBar(this.movieLayoutToolbar);
        this.playButton.setEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        Intent intent = getIntent();
        configureDagger();
        String str = EXTRA_ID;
        if (intent.hasExtra(str)) {
            configureViewModel(intent.getIntExtra(str, -1), intent.getBooleanExtra(EXTRA_ONLINE_MODE, false));
        }
        this.data.observe(this, new Observer<Map<String, String>>() {
            public void onChanged(Map<String, String> stringStringMap) {
                referer = stringStringMap.get("Referer");
                Log.d(MovieActivity.TAG, "data.onChanged:\n Referer= " +
                        referer);
            }
        });
        this.plsBottomSheetBehavior = BottomSheetBehavior.from(this.plsBottomSheetLayout);
        this.plsBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        this.plsBottomSheetBehavior.setSkipCollapsed(true);
        this.plsBottomSheetBehavior.setBottomSheetCallback(new BottomSheetCallback() {
            public void onStateChanged(View view, int i) {
                if (i == BottomSheetBehavior.STATE_HIDDEN) {
                    MovieActivity.this.stopVectorAnimation();
                }
            }

            public void onSlide(View view, float v) {
            }
        });
        this.treeViewBuilder = new PlaylistTreeViewBuilder(this);
        this.playButton.setEnabled(true);
        this.actorsAdapter = new ActorsInMovieRvAdapter(this);
        this.actorsLayoutManager = (LinearLayoutManager) this.actorsRecyclerView.getLayoutManager();
        this.actorsRecyclerView.setAdapter(this.actorsAdapter);
        this.drawable = this.progressIndicator.getDrawable();
        this.animationCallback = new AnimationCallback() {
            public void onAnimationStart(Drawable drawable) {
                super.onAnimationStart(drawable);
            }

            public void onAnimationEnd(Drawable drawable) {
                ((Animatable) drawable).start();
            }
        };
        this.playListContainer.setVisibility(View.GONE);
    }

    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void configureDagger() {
        AndroidInjection.inject(this);
    }

    private void configureViewModel(int id, boolean isOnlineMode) {
        this.movieViewModel = new ViewModelProvider(this, viewModelFactory).get(MovieViewModel.class);
        if (!isOnlineMode) {
            this.movieViewModel.getCurrentMovie(id).observe(this, new Observer<FilmixMovie>() {
                public void onChanged(FilmixMovie filmixMovie) {
                    Log.d(MovieActivity.TAG, "onChanged: movie = " + filmixMovie);

                    movieViewModel.setCurrentMovie(filmixMovie);
                    movieViewModel.updateMovieData(filmixMovie);
                    setMovieActivityData(filmixMovie);

                    Log.d(MovieActivity.TAG, "onChanged: movie baseURL = " +
                            MovieActivity.this.movieViewModel.getBaseUrl() +
                            "  /  " +
                            MovieActivity.this.movieViewModel.getPath());

                    controller = new HttpController(movieViewModel.getBaseUrl(), moviePlsData, serialPlsData, data);

                    MovieActivity.this.moviePlsData.observe(MovieActivity.this, new Observer<Playlist>() {
                        public void onChanged(Playlist playlist) {
                            System.out.println("movie playlist obtained");
                            addPlaylistView(false);
                            treeViewBuilder.updateTreeViewData(playlist, false);
                            hidePlaylistWorking();
                        }
                    });
                    serialPlsData.observe(MovieActivity.this, new Observer<Playlist>() {
                        public void onChanged(Playlist playlist) {
                            System.out.println("serial playlist obtained");
                            addPlaylistView(true);
                            treeViewBuilder.updateTreeViewData(playlist, true);
                            hidePlaylistWorking();
                        }
                    });
                }
            });
            movieViewModel.getActorsForMovie(id).observe(this, new Observer<List<Person>>() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                public void onChanged(List<Person> people) {
                    List<Person> actors =
                            people.stream().map(
                                    (person -> {
                                        String photoUrl = person.getPhotoUrl();
                                        if (photoUrl != null && photoUrl.contains("none")) {
                                            person.setPhotoUrl(null);
                                        }
                                        return person;
                                    } )
                            ).collect(Collectors.toList());

                    actors.sort(nullsFirst(
                            comparing(Person::getPhotoUrl, nullsLast(naturalOrder()))
                    ));
                    actorsAdapter.submitList(actors);
                    actorsLayoutManager.scrollToPositionWithOffset(0, 0);
                }
            });
        }

        this.movieViewModel.getCurrentMovieRemote(id).observe(this, new Observer<FilmixMovie>() {
            public void onChanged(FilmixMovie filmixMovie) {
                Log.d(MovieActivity.TAG, "onChanged: movie = " +
                        filmixMovie);

                movieViewModel.updateMovieDataRemote(filmixMovie);
                movieViewModel.setCurrentMovie(filmixMovie);

                setMovieActivityOnlineData(filmixMovie);
                Log.d(MovieActivity.TAG, "onChanged: movie baseURL = " +
                        MovieActivity.this.movieViewModel.getBaseUrl() +
                        "  /  " + movieViewModel.getPath());
                controller = new HttpController(movieViewModel.getBaseUrl(), moviePlsData, serialPlsData, data);

                movieViewModel.getCurrentMovie().getActorsLiveData().observe(MovieActivity.this, new Observer<List<Person>>() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    public void onChanged(List<Person> people) {
                        List<Person> actors =
                                people.stream().map(
                                        (person -> {
                                            String photoUrl = person.getPhotoUrl();
                                            if (photoUrl != null && photoUrl.contains("none")) {
                                                person.setPhotoUrl(null);
                                            }
                                            return person;
                                        } )
                                ).collect(Collectors.toList());

                        actors.sort(nullsFirst(
                                comparing(Person::getPhotoUrl, nullsLast(naturalOrder()))
                        ));
                        actorsAdapter.submitList(actors);
                        actorsLayoutManager.scrollToPositionWithOffset(0, 0);
                    }
                });

                moviePlsData.observe(MovieActivity.this, new Observer<Playlist>() {
                    public void onChanged(Playlist playlist) {
                        System.out.println("movie playlist obtained");
                        addPlaylistView(false);
                        treeViewBuilder.updateTreeViewData(playlist, false);
                        hidePlaylistWorking();
                    }
                });
                serialPlsData.observe(MovieActivity.this, new Observer<Playlist>() {
                    public void onChanged(Playlist playlist) {
                        System.out.println("serial playlist obtained");
                        addPlaylistView(true);
                        treeViewBuilder.updateTreeViewData(playlist, true);
                        hidePlaylistWorking();
                    }
                });
            }
        });
    }

    public void setMovieActivityOnlineData(FilmixMovie movie) {
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

        movie.getGenresLiveData().observe(this, new Observer<List<Genre>>() {
            public void onChanged(List<Genre> genres) {
                if (genres != null) {
                    String genreNames = "";
                    for (Genre g : genres) {
                        genreNames = genreNames +
                                g.getName() +
                                StringUtils.SPACE;
                    }
                    movie_genre_d_tv.setText(genreNames);
                }
            }
        });
        this.movie_duration_d_tv.setText(movie.getDuration());
        this.movie_description_d_tv.setText(movie.getDescriptionStory());
        this.posRatingValueAMovie.setText(" +" + movie.getPosRating());
        this.negRatingValueAMovie.setText(" -" + movie.getNegRating());
    }


    public void addPlaylistView(boolean isSerial) {
        playListContainer.setVisibility(View.VISIBLE);
        if (playListContainer.getChildCount() < 1) {
            playListContainer.addView(treeViewBuilder.buildTreeView(isSerial));
        }
    }

    /* access modifiers changed from: private */
    public void setMovieActivityData(FilmixMovie movie) {MultiTransformation<Bitmap> bitmapMultiTransformation =
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

    private void showPlaylistWorking() {
        this.playListContainer.setVisibility(View.GONE);
        this.progressIndicator.setVisibility(View.VISIBLE);

        if (drawable instanceof Animatable) {
            ((Animatable) drawable).start();
            if (VERSION.SDK_INT >= 23) {
                AnimatedVectorDrawableCompat.registerAnimationCallback(this.drawable, this.animationCallback);
            }
        }
    }

    public void hidePlaylistWorking() {
        stopVectorAnimation();
        this.progressIndicator.setVisibility(View.GONE);
        this.playListContainer.setVisibility(View.VISIBLE);
    }

    public void stopVectorAnimation() {
        AnimatedVectorDrawableCompat.unregisterAnimationCallback(this.drawable, this.animationCallback);

        if (drawable instanceof Animatable) {
            ((Animatable) drawable).stop();
        }
    }

    @OnClick({R.id.play_btn})
    public void clickPlayBtn() {
        this.plsBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        showPlaylistWorking();
        this.controller.getMovieData(this.movieViewModel.getPath());
    }

    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: paused");
        stopVectorAnimation();
    }

    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: stopped");
        stopVectorAnimation();
    }

    public void onBackPressed() {
        if (this.plsBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            this.plsBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        } else {
            super.onBackPressed();
        }
    }

    public void onActorClick(Person actor) {
        showActorDialog(actor);
    }

    public void showActorDialog(Person currentPerson) {
        final Dialog MyDialog = new Dialog(this);
        MyDialog.requestWindowFeature(1);
        MyDialog.setContentView(R.layout.actor_movie_dialog);
        MyDialog.setTitle("My Custom Dialog");
        Button okButton = MyDialog.findViewById(R.id.close);
        TextView actorNameTextView = MyDialog.findViewById(R.id.actor_name);
        ImageView actorBigPhoto = MyDialog.findViewById(R.id.actor_big_photo);
        okButton.setEnabled(true);
        actorNameTextView.setText(currentPerson.getName());

        GlideApp.with(this)
                .asBitmap()
                .fallback(R.drawable.empty_actor_photo)
                .load(currentPerson.getPhotoUrl())
                .transform(new MultiTransformation<Bitmap>(new RoundedCorners(DimensionUtils.dpToPx(this, 7))))
                .override(500, 1024)
                .into(actorBigPhoto);

        okButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                MyDialog.cancel();
            }
        });
        MyDialog.show();
    }
}