package com.chame.kaizoyu.search;

import android.content.Context;
import android.widget.LinearLayout;
import com.chame.kaizolib.common.model.Result;
import com.chame.kaizolib.nibl.Nibl;
import com.chame.kaizoyu.MainActivity;
import com.chame.kaizoyu.R;
import com.chame.kaizoyu.search.scrappers.NiblSearch;
import com.chame.kaizoyu.utils.ThreadingAssistant;

import java.util.List;

public class SearchResultsPager {
    private final Nibl nibl;
    private final Context context;
    private final LinearLayout layout;
    private final String searchTerm;
    private final ThreadingAssistant thAssistant;

    private List<Result> results = null;
    private boolean isLoadingResults = false;

    private final int ITEMS_TO_LOAD = 5;
    private int resultsIndex = 0;

    public SearchResultsPager(String searchTerm, Context context, LinearLayout layout){
        this.searchTerm = searchTerm;
        this.context = context;
        this.layout = layout;
        nibl = MainActivity.getInstance().getScrappersAssistant().getNibl();
        thAssistant = MainActivity.getInstance().getThreadingAssistant();
    }

    public void setLoading(boolean loading){
        isLoadingResults = loading;
    }

    public boolean isLoading(){
        return isLoadingResults;
    }

    public boolean hasResults(){
        return results != null;
    }

    public void setResults(List<Result> results){
        this.results = results;

        //For debugging purposes.
        isLoadingResults = true;
        loadMoreResults();
    }

    public void initialize(){
        if (thAssistant.isSearchThreadRunning()){
            thAssistant.cancelSearchThread();
        }
        NiblSearch search = new NiblSearch(searchTerm, nibl, context, layout, this);
        thAssistant.submitSearchtoThread(search);
    }

    public void loadMoreResults(){
        if (resultsIndex >= results.size()) return;

        int higherIndex = Math.min(resultsIndex + ITEMS_TO_LOAD, results.size());
        List<Result> subResults = results.subList(resultsIndex, higherIndex);
        resultsIndex = higherIndex;

        SearchItems sItems = new SearchItems(subResults, context, layout, this);
        thAssistant.submitSearchtoThread(sItems);
    }
}
