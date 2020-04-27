package com.example.officehrcenter.application;

import android.app.Application;
import android.content.Context;


public class App extends Application {

    private static Context sContext;
    private int userID;

    public static Context getAppContext() {
        return sContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        setID(0);
        sContext = getApplicationContext();
    }

    public int getID(){ return userID; }

    public void setID(int id){ this.userID = id; }
}
