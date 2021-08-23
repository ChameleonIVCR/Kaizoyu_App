package com.chame.kaizoyu.search.scrappers;

import android.content.Context;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import com.chame.kaizolib.common.model.Result;
import com.chame.kaizolib.nibl.Nibl;
import com.chame.kaizoyu.search.SearchItems;
import com.chame.kaizoyu.search.SearchResultsPager;

import java.util.ArrayList;
import java.util.List;

public class NiblSearch implements Runnable {
    private final Nibl nibl;
    private final Context context;
    private final String search;
    private final LinearLayout layoutList;
    private final SearchResultsPager pager;

    public NiblSearch(String search, Nibl nibl, Context context, LinearLayout layoutList, SearchResultsPager pager) {
        this.pager = pager;
        this.nibl = nibl;
        this.context = context;
        this.search = search;
        this.layoutList = layoutList;
    }

    @Override
    public void run() {
        List<Result> results;
        if (search == null) {
            results = nibl.getLatest();
        } else {
            results = nibl.search(search);
        }

        this.pager.setResults(results);
    }
}
