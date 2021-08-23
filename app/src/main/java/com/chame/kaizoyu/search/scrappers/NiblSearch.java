package com.chame.kaizoyu.search.scrappers;

import android.content.Context;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.chame.kaizolib.common.model.Result;
import com.chame.kaizolib.nibl.Nibl;
import com.chame.kaizoyu.R;
import com.chame.kaizoyu.search.SearchItem;

import java.util.ArrayList;
import java.util.List;

public class NiblSearch implements Runnable {
    private final Nibl nibl;
    private final Context context;
    private final String search;
    private final LinearLayout layoutList;

    public NiblSearch(String search, Nibl nibl, Context context, LinearLayout layoutList) {
        this.nibl = nibl;
        this.context = context;
        this.search = search;
        this.layoutList = layoutList;
    }

    public NiblSearch(Nibl nibl, Context context, LinearLayout layoutList) {
        this.nibl = nibl;
        this.context = context;
        this.search = null;
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

        LayoutInflater inflater = LayoutInflater.from(context);
        List<View> items = new ArrayList<>();

        for (Result result : results){
            if (Thread.currentThread().isInterrupted()) {
                return;
            }

            items.add(SearchItem.inflateSearchItem(inflater, result));
        }

        if (Thread.currentThread().isInterrupted()) {
            return;
        }

        Runnable task = () -> {
            for (View item : items){
                layoutList.addView(item);
            }
        };

        new Handler(Looper.getMainLooper()).post(task);
    }
}
