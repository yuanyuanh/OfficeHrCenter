package com.example.officehrcenter.objects;

import android.util.Log;

import com.example.officehrcenter.utils.DateUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AvailabilityDataModel {

    private String startTime;
    private String endTime;
    private boolean available = true;
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("HH:mm");
    private static final String TAG = "Availability Data Model";
    final long ONE_MINUTE_IN_MILLIS = 60000;

    // 2 parameter constructor
    public AvailabilityDataModel(String startTime, boolean available){
        this.startTime = startTime;
        this.available = available;
        try{
            Date start = (Date)DATE_FORMAT.parse(startTime);
            long curTimeInMs = start.getTime();
            Date end = new Date(curTimeInMs + (30 * ONE_MINUTE_IN_MILLIS));
            this.endTime = DATE_FORMAT.format(end);
        }catch(Exception e){
            Log.e(TAG, e.getMessage());
        }
    }

    // 1 parameter constructor
    public AvailabilityDataModel(String startTime){
        this(startTime, true);
    }

    public void setUnavailable(){
        this.available = false;
    }

    public void setAvailable(){
        this.available = true;
    }

    public boolean isAvailable(){
        return this.available;
    }

    public String getStartTime(){
        return this.startTime;
    }

    public String getEndTime(){
        return this.endTime;
    }
}
