package com.chame.kaizoyu.utils;

import android.content.Context;
import com.chame.kaizoyu.R;

public class Translation {
    public static String getThemeTranslation(String text, Context context) {
        switch(text) {
            case "theme_system":
                return context.getResources().getString(R.string.theme_system);
            case "theme_crystal":
                return context.getResources().getString(R.string.theme_crystal);
            default:
                return context.getResources().getString(R.string.theme_default);
        }
    }

    public static String getNightThemeTranslation(String text, Context context) {
        switch(text) {
            case "night_theme_day":
                return context.getResources().getString(R.string.night_theme_day);
            case "night_theme_night":
                return context.getResources().getString(R.string.night_theme_night);
            default:
                return context.getResources().getString(R.string.night_theme_default);
        }
    }
}
