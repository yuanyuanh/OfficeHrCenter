package com.example.officehrcenter.application;

import android.app.Application;
import android.content.Context;


public class App extends Application {

    private static Context sContext;

    public static Context getAppContext() {
        return sContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        sContext = getApplicationContext();
    }
}
