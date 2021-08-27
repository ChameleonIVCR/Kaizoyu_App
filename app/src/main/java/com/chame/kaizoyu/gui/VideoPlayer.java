package com.chame.kaizoyu.gui;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.widget.MediaController;
import android.widget.VideoView;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import com.chame.kaizolib.irc.DCCDownloader;
import com.chame.kaizolib.irc.model.DCC;
import com.chame.kaizoyu.MainActivity;
import com.chame.kaizoyu.R;
import com.chame.kaizoyu.download.scrappers.IRCGet;
import com.chame.kaizoyu.utils.ThreadingAssistant;

import java.io.File;
import java.util.concurrent.Future;

public class VideoPlayer extends AppCompatActivity {
    private ThreadingAssistant thAssistant;
    private String videoName;
    private String videoCommand;
    private VideoView videoView;
    private DCCDownloader download;
    private File downloadFile;
    private Future ircThread;
    private Future dccThread;


    private static final boolean AUTO_HIDE = true;
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private View mContentView;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };
    private View mControlsView;
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            mControlsView.setVisibility(View.VISIBLE);
        }
    };
    private boolean mVisible;
    private final Runnable mHideRunnable = this::hide;
    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (AUTO_HIDE) {
                        delayedHide(AUTO_HIDE_DELAY_MILLIS);
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    view.performClick();
                    break;
                default:
                    break;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);

        Bundle extras = getIntent().getExtras();

        videoCommand = extras.getString("vCommand");
        videoName = extras.getString("vname");

        //Configure player
        videoView = (VideoView) findViewById(R.id.video_frame);
        MediaController ctlr = new MediaController(this);
        ctlr.setMediaPlayer(videoView);
        videoView.setMediaController(ctlr);
        videoView.requestFocus();

        mVisible = true;

        mContentView = findViewById(R.id.video_frame);
        mControlsView = (View) ctlr;
        // Set up the user interaction to manually show or hide the system UI.
        mContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle();
            }
        });

        thAssistant = MainActivity.getInstance().getThreadingAssistant();
        getDCCHandler();

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        //findViewById(R.id.dummy_button).setOnTouchListener(mDelayHideTouchListener);
    }

    @Override
    public void onStop () {
        super.onStop();
        if (download != null){
            download.stop();
        } else if (ircThread != null && !ircThread.isDone()) {
            ircThread.cancel(true);
        } else if (dccThread != null && !dccThread.isDone()) {
            dccThread.cancel(true);
        } else if (downloadFile != null){
            downloadFile.delete();
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        delayedHide(100);
    }

    private void getDCCHandler(){
        IRCGet irc = new IRCGet(videoCommand);
        irc.setListener(new IRCGet.IRCListener() {
            @Override
            public void onIRCFailed(Exception e) {

            }

            @Override
            public void onIRCFinished(DCC dcc) {
                startDownload(dcc);
            }
        });
        ircThread = thAssistant.submitToDownloadThread(irc);
    }

    private void startDownload(DCC dcc){
        downloadFile = new File(getCacheDir(), dcc.getFilename());
        download = new DCCDownloader(dcc, downloadFile);
        download.setDCCDownloadListener(new DCCDownloader.DCCDownloadListener() {
            @Override
            public void onDownloadReadyToPlay(int progress, File downloadFile) {
                startPlayer(downloadFile);
            }

            @Override
            public void onProgress(int progress, String speed) {

            }

            @Override
            public void onFinished(File downloadFile) {

            }

            @Override
            public void onError(Exception error) {

            }
        });
        dccThread = thAssistant.submitToDownloadThread(download::start);
    }

    private void startPlayer(File downloadFile){
        videoView.setVideoPath(String.valueOf(Uri.fromFile(downloadFile)));
        videoView.start();
    }

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mControlsView.setVisibility(View.GONE);
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    private void show() {
        // Show the system bar
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    /**
     * Schedules a call to hide() in delay milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }
}