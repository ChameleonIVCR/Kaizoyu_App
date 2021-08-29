package com.chame.kaizoyu.gui;

import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.view.*;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.ViewModelProvider;
import com.chame.kaizolib.irc.DCCDownloader;
import com.chame.kaizolib.irc.IrcClient;
import com.chame.kaizoyu.R;
import com.chame.kaizoyu.gui.modelholder.VideoDownloadHolder;

import io.github.tonnyl.spark.Spark;

import java.io.File;

public class VideoPlayer extends AppCompatActivity {
    private Spark spark;
    private ProgressBar initialProgressBar;
    private TextView downloadSpeed;

    private VideoDownloadHolder videoDataHolder;

    private boolean isPlaying = false;
    private VideoView videoView;
    private File downloadFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        setContentView(R.layout.activity_video_player);

        initialProgressBar = findViewById(R.id.initial_download_progress);
        initialProgressBar.setIndeterminate(false);
        initialProgressBar.setProgress(0);
        initialProgressBar.setMax(11);
        downloadSpeed = findViewById(R.id.download_speed);

        //Temporary value
        downloadFile = getCacheDir();
        if (savedInstanceState != null){
            downloadFile = new File(savedInstanceState.getString("file"));
            isPlaying = savedInstanceState.getBoolean("isPlaying");
            if (isPlaying) startPlayer();
        }

        videoDataHolder = new ViewModelProvider(this).get(VideoDownloadHolder.class);
        if (!videoDataHolder.hasStarted()){
            videoDataHolder.initialize(extras.getString("vCommand"), getCacheDir());
        }

        videoDataHolder.setIrcOnFailureListener(new IrcClient.IrcOnFailureListener() {
            @Override
            public void onFailure(IrcClient.FailureCode f) {
                //TODO Error handling
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
                if (isPlaying){
                    //TODO update the widget
                } else {
                    runOnUiThread(() -> initialProgressBar.setProgress(progress));
                    runOnUiThread(() -> downloadSpeed.setText(speed));
                }
                System.out.println(speed);
                System.out.println(progress);
            }

            @Override
            public void onFinished(File downloadFile) {

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
        videoView = (VideoView) findViewById(R.id.video_frame);
        MediaController ctlr = new MediaController(this);
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

    private void startPlayer(){
        spark.stopAnimation();
        ConstraintLayout background = findViewById(R.id.video_background);
        background.setBackgroundColor(Color.BLACK);
        initialProgressBar.setVisibility(View.GONE);
        downloadSpeed.setVisibility(View.GONE);

        isPlaying = true;
        videoView.setVisibility(View.VISIBLE);
        videoView.setVideoPath(String.valueOf(Uri.fromFile(downloadFile)));
        videoView.start();
    }
}