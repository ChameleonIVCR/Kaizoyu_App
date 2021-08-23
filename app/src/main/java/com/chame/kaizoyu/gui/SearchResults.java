package com.chame.kaizoyu.gui;

import com.chame.kaizoyu.MainActivity;
import com.chame.kaizoyu.R;

import android.os.Bundle;
import android.view.View;
import android.widget.SearchView;

import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.AppCompatActivity;

public class SearchResults extends AppCompatActivity {

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

        String searchTerm = extras.getString("search");
        SearchView searchView = findViewById(R.id.main_search_bar);
        searchView.setQuery(searchTerm, false);
        searchView.setIconified(false);
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                return true;
            }
        });

        MainActivity.getInstance().getScrappersAssistant().searchNibl(
                searchTerm,
                getBaseContext(),
                findViewById(R.id.search_items_layout)
        );

    }
}