package com.denspark.strelets.cinematrix.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.denspark.strelets.cinematrix.R;
import com.google.android.exoplayer2.*;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.*;
import com.google.android.exoplayer2.util.Util;

public class PlayerActivity extends AppCompatActivity {

    public static final String EXTRA_VIDEO_URL = "com.denspark.strelets.cinematrix.activities.PlayerActivity.EXTRA_VIDEO_URL";
    public static final String EXTRA_REFERER = "com.denspark.strelets.cinematrix.activities.PlayerActivity.EXTRA_REFERER";

    private PlayerView playerView;
    private SimpleExoPlayer player;
    private String videoURL;
    private String referer;
    private boolean playWhenReady = false;

    private boolean startAutoPlay;
    private long startPosition;
    private int startWindow;
    private Intent intent;


    private String userAgent;

    private static final String KEY_TRACK_SELECTOR_PARAMETERS = "track_selector_parameters";
    private static final String KEY_WINDOW = "window";
    private static final String KEY_POSITION = "position";
    private static final String KEY_AUTO_PLAY = "auto_play";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        playerView = findViewById(R.id.ep_video_view);

        if (savedInstanceState != null) {
            startAutoPlay = savedInstanceState.getBoolean(KEY_AUTO_PLAY);
            startWindow = savedInstanceState.getInt(KEY_WINDOW);
            startPosition = savedInstanceState.getLong(KEY_POSITION);
        }
        intent = getIntent();
        videoURL= intent.getStringExtra(EXTRA_VIDEO_URL);
        referer=intent.getStringExtra(EXTRA_REFERER);
//        initializePlayer();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        updateStartPosition();
        outState.putBoolean(KEY_AUTO_PLAY, startAutoPlay);
        outState.putInt(KEY_WINDOW, startWindow);
        outState.putLong(KEY_POSITION, startPosition);
    }

    private void updateStartPosition() {
        if (player != null) {
            startAutoPlay = player.getPlayWhenReady();
            startWindow = player.getCurrentWindowIndex();
            startPosition = Math.max(0, player.getContentPosition());
        }
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
            initializePlayer();
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        if (Util.SDK_INT <= 23 || player == null) {
            initializePlayer();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (Util.SDK_INT <= 23) {
            releasePlayer();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (Util.SDK_INT > 23) {
            releasePlayer();
        }
    }
}

