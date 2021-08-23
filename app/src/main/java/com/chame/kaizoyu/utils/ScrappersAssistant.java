package com.chame.kaizoyu.utils;

import android.content.Context;
import android.widget.LinearLayout;
import com.chame.kaizolib.common.network.UserHttpClient;
import com.chame.kaizolib.nibl.Nibl;
import com.chame.kaizoyu.search.scrappers.NiblSearch;

public class ScrappersAssistant {
    private final UserHttpClient httpClient = new UserHttpClient();
    private final Nibl nibl = new Nibl(httpClient);
    private final ThreadingAssistant thAssistant;

    public ScrappersAssistant(ThreadingAssistant thAssistant){
        this.thAssistant = thAssistant;
    }

    public void searchNibl(String searchTerm, Context context, LinearLayout layout){
        if (thAssistant.isSearchThreadRunning()){
            thAssistant.cancelSearchThread();
        }
        NiblSearch search = new NiblSearch(searchTerm, nibl, context, layout);
        thAssistant.submitSearchtoThread(search);
    }

    public void searchNibl(Context context, LinearLayout layout){
        if (thAssistant.isSearchThreadRunning()){
            thAssistant.cancelSearchThread();
        }
        NiblSearch search = new NiblSearch(nibl, context, layout);
        thAssistant.submitSearchtoThread(search);
    }

    public Nibl getNibl(){
        return nibl;
    }
}
