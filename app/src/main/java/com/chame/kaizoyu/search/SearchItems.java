package com.chame.kaizoyu.search;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.chame.kaizolib.common.model.Result;
import com.chame.kaizoyu.R;
import com.chame.kaizoyu.gui.adapters.SearchRecyclerListener;

import java.util.ArrayList;
import java.util.List;

public class SearchItems implements Runnable{
    private final Context context;
    private final LinearLayout layoutList;
    private final SearchRecyclerListener pager;
    private final List<Result> results;

    public SearchItems(List<Result> results, Context context, LinearLayout layoutList, SearchRecyclerListener pager){
        this.results = results;
        this.context = context;
        this.layoutList = layoutList;
        this.pager = pager;
    }

    @Override
    public void run() {
        pager.setLoading(true);
        List<View> items = new ArrayList<>();
        for (Result result : results){
            if (Thread.currentThread().isInterrupted()) {
                pager.setLoading(false);
                return;
            }
            items.add(inflateSearchItem(result));
        }

        if (Thread.currentThread().isInterrupted()) {
            pager.setLoading(false);
            return;
        }

        Runnable task = () -> {
            for (View item : items){
                layoutList.addView(item);
            }
        };

        new Handler(Looper.getMainLooper()).post(task);
        pager.setLoading(false);
    }

    private View inflateSearchItem(Result searchResult){
        LayoutInflater inflater = LayoutInflater.from(context);
        View item = inflater.inflate(R.layout.item_result, null);

        TextView title = (TextView) item.findViewById(R.id.text_title);
        title.setText(searchResult.getFilename());

        TextView text_bot = (TextView) item.findViewById(R.id.text_bot);
        text_bot.setText(searchResult.getBot());

        TextView quality = (TextView) item.findViewById(R.id.text_quality);
        quality.setText(searchResult.getQuality());

        TextView format = (TextView) item.findViewById(R.id.text_format);
        format.setText(searchResult.getFormat());

        TextView size = (TextView) item.findViewById(R.id.text_size);
        size.setText(searchResult.getSize());

        return item;
    }
}
