package com.qfix;

import android.app.Application;

import com.qfix.utils.ThemeHelper;

public class QFixApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // Initialize theme
        ThemeHelper.applyTheme(this);
    }
}