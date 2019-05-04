package com.viscanner.app;

import android.content.Context;

import androidx.multidex.MultiDexApplication;

public class MainApplication extends MultiDexApplication {

    private static Context context;

    public void onCreate() {
        super.onCreate();
        MainApplication.context = getApplicationContext();
    }

    public static Context getAppContext() {
        return MainApplication.context;
    }
}
