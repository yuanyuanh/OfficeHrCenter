package com.example.officehrcenter.activities;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.officehrcenter.R;
import com.example.officehrcenter.interfaces.OnDateSelectedListener;
import com.example.officehrcenter.objects.CalendarDate;
import com.example.officehrcenter.views.CustomCalendarView;

public class AvailabilityActivity extends AppCompatActivity implements OnDateSelectedListener {

    private CustomCalendarView mCustomCalendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_availability);

        mCustomCalendar = (CustomCalendarView) findViewById(R.id.activity_main_view_custom_calendar);
        mCustomCalendar.setOnDateSelectedListener(this);
    }

    @Override
    public void onDateSelected(CalendarDate date) {
        Toast.makeText(this, date.toString(), Toast.LENGTH_LONG).show();
    }

}
