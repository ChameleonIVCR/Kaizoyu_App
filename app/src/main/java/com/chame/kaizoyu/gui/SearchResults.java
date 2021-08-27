package com.chame.kaizoyu.gui;

import android.content.Intent;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.chame.kaizolib.common.model.Result;
import com.chame.kaizoyu.MainActivity;
import com.chame.kaizoyu.R;

import android.os.Bundle;
import android.view.View;
import android.widget.SearchView;

import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.AppCompatActivity;
import com.chame.kaizoyu.gui.adapters.RecyclerAdapter;
import com.chame.kaizoyu.gui.adapters.SearchRecyclerListener;

import java.util.logging.Level;
import java.util.logging.Logger;

public class SearchResults extends AppCompatActivity {
    private SearchRecyclerListener pager;
    private String lastSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        setContentView(R.layout.activity_search_results);

        Toolbar toolbar = (Toolbar) findViewById(R.id.search_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                MainActivity.getInstance().getThreadingAssistant().cancelSearchThread();
            }
        });

        lastSearch = extras.getString("search");
        SearchView searchView = findViewById(R.id.main_search_bar);
        searchView.setQuery(lastSearch, false);
        searchView.setIconified(false);
        searchView.setOnCloseListener(() -> true);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.search_items_layout);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        RecyclerAdapter adapter = new RecyclerAdapter(this);
        adapter.setClickListener(new RecyclerAdapter.ItemClickListener() {
            @Override
            public void onItemClick(Result result) {
                Intent intent = new Intent(SearchResults.this, VideoPlayer.class);
                intent.putExtra("vCommand", result.getCommand());
                intent.putExtra("vname", result.getFilename());
                SearchResults.this.startActivity(intent);
            }
        });

        recyclerView.setAdapter(adapter);

        SearchRecyclerListener pager = new SearchRecyclerListener(lastSearch, adapter);
        pager.initialize();
        recyclerView.addOnScrollListener(pager);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                ((LinearLayoutManager)recyclerView.getLayoutManager()).getOrientation());

        recyclerView.addItemDecoration(dividerItemDecoration);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener(){
            @Override
            public boolean onQueryTextSubmit(String search) {
                if (lastSearch.equals(search)){
                    return false;
                }
                pager.newSearch(search);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
    }
}