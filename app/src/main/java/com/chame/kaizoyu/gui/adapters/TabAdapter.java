package com.chame.kaizoyu.gui.adapters;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import com.chame.kaizoyu.gui.HomeFragment;
import com.chame.kaizoyu.gui.LoadingFragment;

public class TabAdapter extends FragmentStateAdapter {
    public TabAdapter(AppCompatActivity activity) {
        super(activity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0) {
            return new HomeFragment();
        } else if (position == 1) {
            return new LoadingFragment();
        }
        return null;
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
