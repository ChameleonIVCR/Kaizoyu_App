package com.chame.kaizoyu.video;

import androidx.lifecycle.ViewModel;
import com.chame.kaizolib.irc.DCCDownloader;
import com.chame.kaizolib.irc.IrcClient;
import com.chame.kaizolib.irc.model.DCC;
import com.chame.kaizoyu.MainActivity;
import com.chame.kaizoyu.utils.ThreadingAssistant;

import java.io.File;
import java.util.concurrent.Future;

public class VideoDownloadHolder extends ViewModel {
    private ThreadingAssistant thAssistant = MainActivity.getInstance().getDataAssistant().getThreadingAssistant();
    private Future ircFuture;
    private Future dccFuture;

    private IrcClient.IrcOnFailureListener onFailureListener;
    private DCCDownloader.DCCDownloadListener dccListener;

    private File cachePath;
    private File downloadFile;

    private IrcClient irc;
    private DCCDownloader download;

    private boolean hasStarted = false;

    public void initialize(String videoCommand, File cachePath){
        final String username = MainActivity.getInstance().getDataAssistant().getConfiguration().getProperty("ircName");
        this.irc = new IrcClient(videoCommand, username);
        this.cachePath = cachePath;
    }

    public void setIrcOnFailureListener(IrcClient.IrcOnFailureListener onFailureListener){
        this.onFailureListener = onFailureListener;
        if (irc != null){
            irc.setIrcOnFailureListener(onFailureListener);
        }
    }

    public void setDccDownloadListener(DCCDownloader.DCCDownloadListener dccListener){
        this.dccListener = dccListener;
        if (download != null){
            download.setDCCDownloadListener(dccListener);
        }
    }

    public boolean hasStarted(){
        return hasStarted;
    }

    public void start(){
        if (hasStarted) return;
        irc.setIrcOnFailureListener(onFailureListener);
        irc.setIrcOnSuccessListener(this::startDownload);
        hasStarted = true;
        ircFuture = thAssistant.submitToDownloadThread(irc::execute);
    }

    @Override
    public void onCleared(){
        stop();
        super.onCleared();
    }

    public void stop(){
        onFailureListener = null;
        dccListener = null;

        if (download != null){
            download.stop();
        } else if (ircFuture != null) {
            ircFuture.cancel(true);
        } else if (dccFuture != null) {
            dccFuture.cancel(true);
        } else if (downloadFile != null && downloadFile.isFile()){
            //TODO if delete is not successfully completed, schedule cache cleaning at app close
            downloadFile.delete();
        }
    }

    private void startDownload(DCC dcc){
        downloadFile = new File(cachePath, dcc.getFilename());
        download = new DCCDownloader(dcc, downloadFile);
        download.setDCCDownloadListener(dccListener);
        dccFuture = thAssistant.submitToDownloadThread(download::start);
    }
}
