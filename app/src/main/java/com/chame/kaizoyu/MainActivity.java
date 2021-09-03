package com.chame.kaizoyu;

import android.content.Intent;
import android.view.View;
import android.widget.SearchView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.viewpager2.widget.ViewPager2;
import com.chame.kaizoyu.search.gui.SearchResults;
import com.chame.kaizoyu.gui.adapters.TabAdapter;
import com.chame.kaizoyu.utils.DataAssistant;
import com.google.android.material.tabs.TabLayout;

import java.lang.ref.WeakReference;

public class MainActivity extends AppCompatActivity {
    public static WeakReference<MainActivity> weakActivity;
    private DataAssistant dataAssistant;

    private TabAdapter tabAdapter;
    private ViewPager2 viewPager;
    private TabLayout tabLayout;

    public DataAssistant getDataAssistant() {
        return dataAssistant;
    }

    public int getCurrentTabIndex(){
        return tabLayout.getSelectedTabPosition();
    }

    public void setCurrentTab(int index){
        viewPager.setCurrentItem(index);
    }

    public static MainActivity getInstance() {
        return weakActivity.get();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        weakActivity = new WeakReference<>(MainActivity.this);
        dataAssistant = new DataAssistant(this);

        setContentView(R.layout.activity_main);
        //Tabs
        configureTabAdapter();

        if(savedInstanceState != null) {
            int index = savedInstanceState.getInt("index");
            TabLayout.Tab tab = tabLayout.getTabAt(index);
            tab.select();
        }

        TextView title = findViewById(R.id.toolbar_title);

        SearchView searchView = findViewById(R.id.search_bar);

        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                title.setVisibility(View.INVISIBLE);
            }
        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                title.setVisibility(View.VISIBLE);
                return false;
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener(){
            @Override
            public boolean onQueryTextSubmit(String search) {
                Intent intent = new Intent(MainActivity.this, SearchResults.class);
                intent.putExtra("search", search);
                MainActivity.this.startActivity(intent);
                searchView.setQuery(null, false);
                searchView.setIconified(true);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        int i = tabLayout.getSelectedTabPosition();
        outState.putInt("index", i);
    }


    private void configureTabAdapter(){
        tabLayout = findViewById(R.id.bottom_tabs);
        tabAdapter = new TabAdapter(this);
        viewPager = findViewById(R.id.paginador_principal);
        viewPager.setUserInputEnabled(false);
        viewPager.setAdapter(tabAdapter);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }
}