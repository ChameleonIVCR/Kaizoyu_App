package com.chame.kaizoyu.utils;

import android.content.Context;
import com.chame.kaizolib.common.network.UserHttpClient;

import java.io.File;

public class DataAssistant {
    private final UserHttpClient httpClient = new UserHttpClient();
    private final ThreadingAssistant thAssistant = new ThreadingAssistant();
    private final Configuration configuration;

    private final Context mainContext;

    public DataAssistant(Context context){
        this.mainContext = context;
        configuration = new Configuration(context);
    }

    public ThreadingAssistant getThreadingAssistant(){
        return thAssistant;
    }

    public UserHttpClient getUserHttpClient(){
        return httpClient;
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public void close(){
        configuration.save();
        thAssistant.close();
        httpClient.close();
        clearCache();
    }

    public void clearCache(){
        File[] files = mainContext.getCacheDir().listFiles();
        if(files != null) {
            for(File f : files) {
                f.delete();
            }
        }
    }
}
