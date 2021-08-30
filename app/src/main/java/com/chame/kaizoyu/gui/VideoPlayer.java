package com.chame.kaizoyu.gui;

import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.view.*;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.ViewModelProvider;
import com.chame.kaizolib.irc.DCCDownloader;
import com.chame.kaizolib.irc.IrcClient;
import com.chame.kaizoyu.R;
import com.chame.kaizoyu.gui.modelholder.VideoDownloadHolder;

import com.google.android.material.snackbar.Snackbar;
import io.github.tonnyl.spark.Spark;

import java.io.File;

public class VideoPlayer extends AppCompatActivity {
    private Spark spark;

    //Main progress bars
    private ProgressBar initialProgressBar;
    private TextView downloadSpeed;

    //Secondary progress indicators
    private TextView secondaryProgress;
    private TextView secondarySpeed;
    private LinearLayout secondaryIndicatorContainer;

    private VideoDownloadHolder videoDataHolder;
    private VideoView videoView;

    private File downloadFile;

    private boolean isPlaying = false;
    private boolean hasFinished = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        setContentView(R.layout.activity_video_player);

        downloadFile = getCacheDir();
        if (savedInstanceState != null){
            downloadFile = new File(savedInstanceState.getString("file"));
            isPlaying = savedInstanceState.getBoolean("isPlaying");
            if (isPlaying) startPlayer();
        }

        //Main progress bars
        initialProgressBar = findViewById(R.id.initial_download_progress);
        initialProgressBar.setIndeterminate(false);
        initialProgressBar.setProgress(0);
        initialProgressBar.setMax(11);
        downloadSpeed = findViewById(R.id.download_speed);

        //Secondary progress indicators
        secondaryProgress = findViewById(R.id.small_progress);
        secondarySpeed = findViewById(R.id.small_speed);
        secondaryIndicatorContainer = findViewById(R.id.small_indicator_container);
        if (isPlaying && !hasFinished) {
            secondaryIndicatorContainer.setVisibility(View.VISIBLE);
        } else {
            secondaryIndicatorContainer.setVisibility(View.INVISIBLE);
        }

        videoDataHolder = new ViewModelProvider(this).get(VideoDownloadHolder.class);
        if (!videoDataHolder.hasStarted()){
            videoDataHolder.initialize(extras.getString("vCommand"), getCacheDir());
        }

        videoDataHolder.setIrcOnFailureListener(new IrcClient.IrcOnFailureListener() {
            @Override
            public void onFailure(IrcClient.FailureCode f) {
                //TODO Error handling
                String message;

                if (f == IrcClient.FailureCode.NoQuickRetry){
                    message = "This provider doesn't support quick retry, please try again later (150 seconds max).";
                } else if (f == IrcClient.FailureCode.TimeOut){
                    message = "Connection to IRC has timed out. Check your internet connection and retry later.";
                } else if (f == IrcClient.FailureCode.UnknownHost){
                    message = "The IRC handshake server couldn't be reached. Check your internet connection.";
                } else {
                    message = "There was a general I/O exception. Check your internet connection, and/or app permissions.";
                }

                Snackbar.make(findViewById(R.id.video_background),
                        message,
                        Snackbar.LENGTH_LONG).show();

                delayedExit();
            }
        });
        videoDataHolder.setDccDownloadListener(new DCCDownloader.DCCDownloadListener() {
            @Override
            public void onDownloadReadyToPlay(int progress, File df) {
                downloadFile = df;
                runOnUiThread(() -> startPlayer());
            }

            @Override
            public void onProgress(int progress, String speed) {
                if (isPlaying && !hasFinished){
                    runOnUiThread(() -> secondaryProgress.setText(progress+"%"));
                    runOnUiThread(() -> secondarySpeed.setText(speed));
                } else {
                    runOnUiThread(() -> initialProgressBar.setProgress(progress));
                    runOnUiThread(() -> downloadSpeed.setText(speed));
                }
                System.out.println(speed);
                System.out.println(progress);
            }

            @Override
            public void onFinished(File downloadFile) {
                runOnUiThread(() -> secondaryIndicatorContainer.setVisibility(View.GONE));
                hasFinished = true;
            }

            @Override
            public void onError(Exception error) {

            }
        });

        ConstraintLayout background = findViewById(R.id.video_background);

        spark = new Spark.Builder()
                .setView(background)   // set the layout of main screen
                .setDuration(4000) // set duration
                .setAnimList(Spark.ANIM_BLUE_PURPLE)  // set the color to change
                .build();

        //Configure player
        videoView = findViewById(R.id.video_frame);
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                MediaPlayer.TrackInfo[] a = mp.getTrackInfo();
                for (MediaPlayer.TrackInfo i : a){
                    System.out.println("Found Tracks:" + i.getTrackType());
                }
            }
        });

        MediaController ctlr = new MediaController(this){
            @Override
            public void hide() {
                super.hide();
            }

            @Override
            public boolean dispatchKeyEvent(KeyEvent event) {
                if(event.getKeyCode() == KeyEvent.KEYCODE_BACK){
                    super.hide();
                    finish();
                    return true;
                }
                return super.dispatchKeyEvent(event);
            }
        };

        ctlr.setMediaPlayer(videoView);
        videoView.setMediaController(ctlr);
        videoView.requestFocus();
        videoView.setVisibility(View.INVISIBLE);

        if (getSupportActionBar() != null) getSupportActionBar().hide();

        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        );

        videoDataHolder.start();
        drawOnCutOut();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isPlaying) spark.startAnimation(); // start animation on resume
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (!isPlaying) spark.stopAnimation(); // stop animation on pause
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        hideSystemUI();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("isPlaying", isPlaying);
        outState.putString("file", downloadFile.getAbsolutePath());
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemUI();
        }
    }

    private void drawOnCutOut(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P){
            final WindowManager.LayoutParams wManager = getWindow().getAttributes();
            wManager.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
        }
    }

    private void hideSystemUI(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            final WindowInsetsController controller = getWindow().getInsetsController();

            if (controller != null)
                controller.hide(WindowInsets.Type.statusBars());
        }
        else {
            //noinspection deprecation
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_IMMERSIVE
                            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        }
    }

    private void delayedExit(){
        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(this::finish, 5000);
    }

    private void startPlayer(){
        spark.stopAnimation();
        ConstraintLayout background = findViewById(R.id.video_background);
        background.setBackgroundColor(Color.BLACK);
        initialProgressBar.setVisibility(View.GONE);
        downloadSpeed.setVisibility(View.GONE);
        secondaryIndicatorContainer.setVisibility(View.VISIBLE);

        isPlaying = true;
        videoView.setVisibility(View.VISIBLE);
        videoView.setVideoPath(String.valueOf(Uri.fromFile(downloadFile)));
        videoView.start();
    }
}