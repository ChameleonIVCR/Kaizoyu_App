package com.chame.kaizoyu.gui;

import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.chame.kaizoyu.MainActivity;
import com.chame.kaizoyu.R;
import com.chame.kaizoyu.utils.Configuration;
import com.chame.kaizoyu.utils.Translation;


public class SettingsFragment extends Fragment {
    private View root;

    // region Initialization
    public SettingsFragment() {

    }

    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }
    // endregion

    @Override
    public void onViewCreated(@NonNull View root, Bundle savedState) {
        this.root = root;
        loadSettings();

        // Set click listeners
        root.findViewById(R.id.themeTrigger).setOnClickListener(this::showThemePopup);

        root.findViewById(R.id.nightThemeTrigger).setOnClickListener(this::showNightThemePopup);

        Switch analytics = root.findViewById(R.id.analyticsValue);
        analytics.setOnCheckedChangeListener(this::triggerSave);

        Switch newsFeed = root.findViewById(R.id.newsValue);
        newsFeed.setOnCheckedChangeListener(this::triggerSave);

        Switch ipv6Sources = root.findViewById(R.id.ipv6SorcesValue);
        ipv6Sources.setOnCheckedChangeListener(this::triggerSave);

        root.findViewById(R.id.clearCacheTrigger).setOnClickListener(view -> {
                Toast.makeText(getContext(), getString(R.string.cache_toast), Toast.LENGTH_SHORT).show();
                MainActivity.getInstance().getDataAssistant().clearCache();
        });

        // TODO Clear search history with button
        // TODO Clear watch history with button
        // TODO Search results limit
    }

    // region Event listeners
    private void showThemePopup(View v) {
        final String default_theme = getString(R.string.theme_default);
        final String system_theme = getString(R.string.theme_system);
        final String crystal_theme = getString(R.string.theme_crystal);

        final String[] themes = {
                default_theme,
                system_theme,
                crystal_theme
        };

        TextView theme = root.findViewById(R.id.themeValue);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(getString(R.string.theme_context));
        builder.setItems(themes, (dialog, index) -> {
            theme.setText(themes[index]);
            saveSettings();
        });
        builder.show();
    }

    private void showNightThemePopup(View v) {
        final String night_theme_default = getString(R.string.night_theme_default);
        final String night_theme_day = getString(R.string.night_theme_day);
        final String night_theme_night = getString(R.string.night_theme_night);

        final String[] themes = {
                night_theme_default,
                night_theme_day,
                night_theme_night
        };

        TextView theme = root.findViewById(R.id.nightThemeValue);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(getString(R.string.night_theme_context));
        builder.setItems(themes, (dialog, index) -> {
            theme.setText(themes[index]);
            saveSettings();
        });
        builder.show();
    }

    private void triggerSave(View v, boolean value) {
        saveSettings();
    }
    // endregion

    // region Configuration save and load
    private void saveSettings(){
        Configuration config = MainActivity.getInstance().getDataAssistant().getConfiguration();

        TextView theme = root.findViewById(R.id.themeValue);
        config.setProperty(
                "theme",
                theme.getText().toString()
        );

        TextView nightTheme = root.findViewById(R.id.nightThemeValue);
        config.setProperty(
                "night_theme",
                nightTheme.getText().toString()
        );

        Switch analytics = root.findViewById(R.id.analyticsValue);
        config.setBooleanProperty(
                "analytics",
                analytics.isChecked()
        );

        Switch newsFeed = root.findViewById(R.id.newsValue);
        config.setBooleanProperty(
                "news_feed",
                newsFeed.isChecked()
        );

        Switch ipv6Sources = root.findViewById(R.id.ipv6SorcesValue);
        config.setBooleanProperty(
                "show_ipv6",
                ipv6Sources.isChecked()
        );
    }

    private void loadSettings() {
        Configuration config = MainActivity.getInstance().getDataAssistant().getConfiguration();

        // Translated Values
        TextView theme = root.findViewById(R.id.themeValue);
        theme.setText(Translation.getThemeTranslation(
                config.getProperty("theme"),
                getContext()
        ));

        TextView nightTheme = root.findViewById(R.id.nightThemeValue);
        nightTheme.setText(Translation.getNightThemeTranslation(
                config.getProperty("night_theme"),
                getContext()
        ));

        // Switches
        Switch analytics = root.findViewById(R.id.analyticsValue);
        analytics.setChecked(config.getBooleanProperty("analytics"));

        Switch newsFeed = root.findViewById(R.id.newsValue);
        newsFeed.setChecked(config.getBooleanProperty("news_feed"));

        Switch ipv6Sources = root.findViewById(R.id.ipv6SorcesValue);
        ipv6Sources.setChecked(config.getBooleanProperty("show_ipv6"));
    }
    // endregion
}