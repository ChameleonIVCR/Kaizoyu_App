package com.chame.kaizoyu.search.scrappers;

import android.os.Handler;
import android.os.Looper;
import com.chame.kaizolib.common.model.Result;
import com.chame.kaizolib.nibl.Nibl;
import com.chame.kaizoyu.gui.adapters.SearchRecyclerListener;

import java.util.List;

public class NiblSearch implements Runnable {
    private final Nibl nibl;
    private final String search;
    private final SearchRecyclerListener pager;

    public NiblSearch(String search, Nibl nibl, SearchRecyclerListener pager) {
        this.pager = pager;
        this.nibl = nibl;
        this.search = search;
    }

    @Override
    public void run() {
        pager.setLoading(true);
        List<Result> results;
        if (search == null) {
            results = nibl.getLatest();
        } else {
            results = nibl.search(search);
        }

        Runnable task = () -> {
            this.pager.setResults(results);
        };

        new Handler(Looper.getMainLooper()).post(task);
        pager.setLoading(false);
    }
}
