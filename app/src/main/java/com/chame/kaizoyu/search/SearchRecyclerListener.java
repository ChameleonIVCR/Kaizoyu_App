package com.chame.kaizoyu.search;

import android.content.Context;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.chame.kaizolib.common.model.Result;
import com.chame.kaizolib.nibl.Nibl;
import com.chame.kaizoyu.MainActivity;
import com.chame.kaizoyu.R;
import com.chame.kaizoyu.gui.adapters.RecyclerAdapter;
import com.chame.kaizoyu.search.scrappers.NiblSearch;
import com.chame.kaizoyu.utils.ThreadingAssistant;

import java.util.List;

public class SearchRecyclerListener extends RecyclerView.OnScrollListener {
    private final NiblSearch niblSearch;
    private final RecyclerAdapter adapter;
    private final ThreadingAssistant thAssistant;

    private List<Result> results = null;
    private boolean isLoadingResults = false;

    public SearchRecyclerListener(String searchTerm, RecyclerAdapter adapter){
        this.adapter = adapter;
        this.niblSearch = new NiblSearch(
                searchTerm,
                MainActivity.getInstance().getScrappersAssistant().getNibl(),
                this
        );
        thAssistant = MainActivity.getInstance().getThreadingAssistant();
    }

    @Override
    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy){
        super.onScrolled(recyclerView, dx, dy);
        LinearLayoutManager mLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        int totalItemCount = mLayoutManager.getItemCount();
        int lastVisible = mLayoutManager.findLastVisibleItemPosition();
        if (recyclerView.getAdapter() == null) return;

        if (!isLoading() && hasResults() && lastVisible + 5 >= totalItemCount){
            loadMoreResults();
        }
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
        loadMoreResults();
    }

    public void initialize(){
        if (thAssistant.isSearchThreadRunning()){
            thAssistant.cancelSearchThread();
        }

        thAssistant.submitSearchtoThread(niblSearch);
    }

    public void newSearch(String searchTerm){
        if (thAssistant.isSearchThreadRunning()){
            thAssistant.cancelSearchThread();
        }

        adapter.clearData();
        adapter.getRecyclerView().post(adapter::notifyDataSetChanged);

        thAssistant.submitSearchtoThread(new NiblSearch(
                searchTerm,
                MainActivity.getInstance().getScrappersAssistant().getNibl(),
                this
        ));
    }

    private void loadMoreResults(){
        int totalItemCount = adapter.getItemCount();
        if (totalItemCount >= results.size()) return;

        int ITEMS_TO_LOAD = 5;
        int higherIndex = Math.min(totalItemCount + ITEMS_TO_LOAD, results.size());
        List<Result> subResults = results.subList(0, higherIndex);

        adapter.replaceData(subResults);
        adapter.getRecyclerView().post(adapter::notifyDataSetChanged);
    }
}
