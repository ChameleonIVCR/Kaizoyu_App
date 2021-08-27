package com.chame.kaizoyu.utils;

import com.chame.kaizoyu.search.scrappers.NiblSearch;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ThreadingAssistant {
    private final ExecutorService searchThread = Executors.newSingleThreadExecutor();
    private final ExecutorService downloadThread = Executors.newCachedThreadPool();
    private Future searchTask;

    public void submitToSearchThread(Runnable search){
        searchTask = searchThread.submit(search);
    }

    public boolean isSearchThreadRunning(){
        return searchTask != null;
    }

    public void cancelSearchThread(){
        if (searchTask != null && !searchTask.isDone()){
            searchTask.cancel(true);
        }
    }

    public Future submitToDownloadThread(Runnable download){
        return downloadThread.submit(download);
    }
}
