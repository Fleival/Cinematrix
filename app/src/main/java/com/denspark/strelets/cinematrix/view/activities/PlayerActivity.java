package com.denspark.strelets.cinematrix.view.activities;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.denspark.strelets.cinematrix.R;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerControlView;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PlayerActivity extends AppCompatActivity {

    public static final String EXTRA_VIDEO_URL = "com.denspark.strelets.cinematrix.view.activities.PlayerActivity.EXTRA_VIDEO_URL";
    public static final String EXTRA_REFERER = "com.denspark.strelets.cinematrix.view.activities.PlayerActivity.EXTRA_REFERER";

    @BindView(R.id.movie_layout_toolbar)
    Toolbar playerLayoutToolbar;

    @BindView(R.id.ep_video_view)
    PlayerView playerView;

    private SimpleExoPlayer player;
    private String videoURL;
    private String referer;
    private boolean playWhenReady = false;

    private boolean startAutoPlay;
    private boolean mExoPlayerFullscreen = false;
    private long startPosition;
    private int startWindow;
    private Intent intent;

    private Dialog mFullScreenDialog;
    private ImageView mFullScreenIcon;
    private FrameLayout mFullScreenButton;


    private String userAgent;

    private static final String KEY_TRACK_SELECTOR_PARAMETERS = "track_selector_parameters";
    private static final String KEY_WINDOW = "window";
    private static final String KEY_POSITION = "position";
    private static final String KEY_AUTO_PLAY = "auto_play";

    private final String STATE_PLAYER_FULLSCREEN = "playerFullscreen";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        ButterKnife.bind(this);

        setSupportActionBar(playerLayoutToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

//        playerView = findViewById(R.id.ep_video_view);
        if (savedInstanceState != null) {
            startAutoPlay = savedInstanceState.getBoolean(KEY_AUTO_PLAY);
            startWindow = savedInstanceState.getInt(KEY_WINDOW);
            startPosition = savedInstanceState.getLong(KEY_POSITION);
            mExoPlayerFullscreen = savedInstanceState.getBoolean(STATE_PLAYER_FULLSCREEN);
        }
        intent = getIntent();
        videoURL = intent.getStringExtra(EXTRA_VIDEO_URL);
        referer = intent.getStringExtra(EXTRA_REFERER);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        updateStartPosition();
        outState.putBoolean(KEY_AUTO_PLAY, startAutoPlay);
        outState.putInt(KEY_WINDOW, startWindow);
        outState.putLong(KEY_POSITION, startPosition);
        outState.putBoolean(STATE_PLAYER_FULLSCREEN, mExoPlayerFullscreen);
        super.onSaveInstanceState(outState);
    }

    private void updateStartPosition() {
        if (player != null) {
            startAutoPlay = player.getPlayWhenReady();
            startWindow = player.getCurrentWindowIndex();
            startPosition = Math.max(0, player.getContentPosition());
        }
    }

    private void initFullscreenDialog() {

        mFullScreenDialog = new Dialog(this, R.style.FullScreenDialogStyle) {
            public void onBackPressed() {
                if (mExoPlayerFullscreen) {
                    (PlayerActivity.this).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
                    closeFullscreenDialog();
                }
                super.onBackPressed();
            }
        };

        mFullScreenDialog.getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);

    }


    private void openFullscreenDialog() {

        ((ViewGroup) playerView.getParent()).removeView(playerView);
        mFullScreenDialog.addContentView(playerView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        mFullScreenIcon.setImageDrawable(ContextCompat.getDrawable(PlayerActivity.this, R.drawable.ic_fullscreen_skrink));
        mExoPlayerFullscreen = true;
        mFullScreenDialog.show();
    }


    private void closeFullscreenDialog() {

        ((ViewGroup) playerView.getParent()).removeView(playerView);
        ((FrameLayout) findViewById(R.id.main_media_frame)).addView(playerView);
        mExoPlayerFullscreen = false;
        mFullScreenDialog.dismiss();
        mFullScreenIcon.setImageDrawable(ContextCompat.getDrawable(PlayerActivity.this, R.drawable.ic_fullscreen_expand));
    }

    private void initFullscreenButton() {
        PlayerControlView controlView = playerView.findViewById(R.id.exo_controller);
        mFullScreenIcon = controlView.findViewById(R.id.exo_fullscreen_icon);
        mFullScreenButton = controlView.findViewById(R.id.exo_fullscreen_button);
        mFullScreenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mExoPlayerFullscreen) {
                    (PlayerActivity.this).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
                    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                            WindowManager.LayoutParams.FLAG_FULLSCREEN);
                    getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
                    openFullscreenDialog();
                } else {
                    (PlayerActivity.this).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
                    closeFullscreenDialog();
                }
            }
        });
    }

    private void initializePlayer() {
        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter.Builder(this).build();

        TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory();
        TrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);

        LoadControl loadControl = new DefaultLoadControl();

        userAgent = Util.getUserAgent(this, getApplicationContext().getApplicationInfo().packageName);

        player = ExoPlayerFactory.newSimpleInstance(
                this,
                new DefaultRenderersFactory(this),
                trackSelector,
                loadControl,
                null,
                bandwidthMeter);

        player.addListener(new PlayerEventListener());

        playerView.setPlayer(player);

        if (videoURL != null) {
            Uri uri = Uri.parse(videoURL);
            MediaSource mediaSource = buildMediaSource(uri);
            player.prepare(mediaSource, true, false);
        }

        player.setPlayWhenReady(playWhenReady);
        player.seekTo(startWindow, startPosition);
    }

    private MediaSource buildMediaSource(Uri uri) {

        DefaultHttpDataSourceFactory httpDataSourceFactory =
                new DefaultHttpDataSourceFactory(
                        userAgent,
                        null,
                        DefaultHttpDataSource.DEFAULT_CONNECT_TIMEOUT_MILLIS,
                        DefaultHttpDataSource.DEFAULT_READ_TIMEOUT_MILLIS,
                        true);

        httpDataSourceFactory.getDefaultRequestProperties()
                .set("Referer", referer);

        DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(
                this,
                null,
                httpDataSourceFactory);

        MediaSource videoSource = new ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(uri);

        return videoSource;
    }

    private void releasePlayer() {
        if (player != null) {
            startPosition = player.getCurrentPosition();
            startWindow = player.getCurrentWindowIndex();
            playWhenReady = player.getPlayWhenReady();
            player.release();
            player = null;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (Util.SDK_INT > 23) {
            initFullscreenDialog();
            initFullscreenButton();
            initializePlayer();

            if (mExoPlayerFullscreen) {
                ((ViewGroup) playerView.getParent()).removeView(playerView);
                mFullScreenDialog.addContentView(playerView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                mFullScreenIcon.setImageDrawable(ContextCompat.getDrawable(PlayerActivity.this, R.drawable.ic_fullscreen_skrink));
                mFullScreenDialog.show();
            }
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        if (Util.SDK_INT <= 23 || player == null) {
            playerView = findViewById(R.id.ep_video_view);
            initFullscreenDialog();
            initFullscreenButton();
            initializePlayer();

            if (mExoPlayerFullscreen) {
                ((ViewGroup) playerView.getParent()).removeView(playerView);
                mFullScreenDialog.addContentView(playerView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                mFullScreenIcon.setImageDrawable(ContextCompat.getDrawable(PlayerActivity.this, R.drawable.ic_fullscreen_skrink));
                mFullScreenDialog.show();
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (Util.SDK_INT <= 23) {
            releasePlayer();
        }

        if (mFullScreenDialog != null)
            mFullScreenDialog.dismiss();
    }


    @Override
    public void onStop() {
        super.onStop();
        if (Util.SDK_INT > 23) {
            releasePlayer();
        }
    }
    private class PlayerEventListener implements Player.EventListener {
        @Override
        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
            if (playbackState == Player.STATE_IDLE || playbackState == Player.STATE_ENDED ||
                    !playWhenReady) {

                playerView.setKeepScreenOn(false);
            } else { // STATE_IDLE, STATE_ENDED
                // This prevents the screen from getting dim/lock
                playerView.setKeepScreenOn(true);
            }
        }

        @Override
        public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {}

        @Override
        public void onLoadingChanged(boolean isLoading) {}

        @Override
        public void onRepeatModeChanged(int repeatMode) { }

        @Override
        public void onPlayerError(ExoPlaybackException error) { }

        @Override
        public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) { }
    }

}

