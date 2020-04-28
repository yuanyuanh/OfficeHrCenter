package com.example.officehrcenter.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.officehrcenter.R;
import com.example.officehrcenter.interfaces.OnDateSelectedListener;
import com.example.officehrcenter.objects.CalendarDate;
import com.example.officehrcenter.views.CustomCalendarView;

public class AvailabilityActivity extends AppCompatActivity implements OnDateSelectedListener {

    private CustomCalendarView mCustomCalendar;
    private int profId;
    private final String TAG = "Availability"; // for the use of log

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_availability);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        profId = bundle.getInt("profId");

        mCustomCalendar = (CustomCalendarView) findViewById(R.id.activity_main_view_custom_calendar);
        mCustomCalendar.setOnDateSelectedListener(this);
    }

    @Override
    public void onDateSelected(CalendarDate date) {
        Log.i(TAG, date.toString() + " is chosen");

    }

}
