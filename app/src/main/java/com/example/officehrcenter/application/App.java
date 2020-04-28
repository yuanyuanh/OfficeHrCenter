package com.example.officehrcenter.application;

import android.app.Application;
import android.content.Context;
import android.util.Log;

public class App extends Application {

    private static Context sContext;
    private int userID;
    private boolean isProfessor;

    public static Context getAppContext() {
        return sContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        setID(0);
        setIfProf("student");
        sContext = getApplicationContext();
    }

    public int getID(){ return userID; }

    public void setID(int id){ this.userID = id; }

    public boolean isProf(){ return isProfessor;}

    public void setIfProf(String occupation){
        if (occupation.equals("professor")){
            this.isProfessor = true;
        }else if (occupation.equals("student")){
            this.isProfessor = false;
        }else{
            Log.e("Global variables", "wrong occupation");
        }
    }
}
