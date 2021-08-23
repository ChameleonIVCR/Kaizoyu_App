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

    public Nibl getNibl(){
        return nibl;
    }
}
