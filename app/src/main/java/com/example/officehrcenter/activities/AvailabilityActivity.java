package com.example.officehrcenter.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.officehrcenter.R;
import com.example.officehrcenter.adapters.AvailabilityAdapter;
import com.example.officehrcenter.adapters.ProfileAdapter;
import com.example.officehrcenter.application.App;
import com.example.officehrcenter.interfaces.OnDateSelectedListener;
import com.example.officehrcenter.objects.AvailabilityDataModel;
import com.example.officehrcenter.objects.CalendarDate;
import com.example.officehrcenter.objects.JDBCHelper;
import com.example.officehrcenter.views.CustomCalendarView;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class AvailabilityActivity extends AppCompatActivity implements OnDateSelectedListener, OnItemClickListener {

    private App myApp; // current application
    private Thread t = null;
    private JDBCHelper dbConn = new JDBCHelper(); // JDBC helper for connecting and making queries to DB

    private CustomCalendarView mCustomCalendar;
    private ListView availListView;
    private ArrayAdapter<AvailabilityDataModel> availabilityAdapter;
    private ArrayList<AvailabilityDataModel> hourAvail = new ArrayList();

    private int profId;
    private String profName;
    private final String TAG = "Availability"; // for the use of log
    private Date selectedDate;
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    private final int STARTHOUR = 9;
    private final int ENDHOUR = 18;
    private boolean halfHour = false;
    private AvailabilityDataModel currentHour;

    private String[] hourTable = new String[(ENDHOUR-STARTHOUR)*2];

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_availability);
        myApp = (App)getApplication();

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        profId = bundle.getInt("profId");
        profName = bundle.getString("profName");

        mCustomCalendar = (CustomCalendarView) findViewById(R.id.activity_main_view_custom_calendar);
        mCustomCalendar.setOnDateSelectedListener(this);

        int tempHour = STARTHOUR;
        for(int idx = 0; idx < hourTable.length; idx++) {
            if (tempHour < 10) {
                hourTable[idx] = "0" + tempHour;
            } else {
                hourTable[idx] = "" + tempHour;
            }
            if (!halfHour) {
                hourTable[idx] += ":00";
                halfHour = true;
            } else {
                hourTable[idx] += ":30";
                halfHour = false;
                tempHour++;
            }
        }

        for(String hour: hourTable){
            currentHour = new AvailabilityDataModel(hour);
            hourAvail.add(currentHour);
        }

        availListView = (ListView)findViewById(R.id.availList);
        availabilityAdapter = new AvailabilityAdapter(hourAvail,getApplicationContext());
        availListView.setAdapter(availabilityAdapter);
        availListView.setOnItemClickListener(this);

    }

    @Override
    public void onDateSelected(CalendarDate date) {
        selectedDate = date.getCalendar().getTime();
        Log.i(TAG, selectedDate.toString() + " is chosen");
        t = new Thread(background);
        t.start();
    }

    private Runnable background = new Runnable() {
        public void run() {

            dbConn.connenctDB();

            String query = "select cast(reserved_time as time) as reserved_daytime from reservation " +
                    "where date(reserved_time) = \'" + DATE_FORMAT.format(selectedDate) + "\'" +
                    "and professor_id = " + profId + ";";

            ResultSet result = dbConn.select(query);

            try {
                if (result.wasNull()) {
                    Log.i(TAG, "Available all day");
                    handler.sendEmptyMessage(0);
                } else {
                    while (result.next()) {
                        setAvailablity(result.getString("reserved_daytime"));
                    }
                    handler.sendEmptyMessage(1);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            t = null; // end of the current thread
            dbConn.disConnect();
        }
    };

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    break;
                case 1:
                    availabilityAdapter.notifyDataSetChanged();
                    break;
            }
        }
    };

    // listener methods for callbacks
    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
        currentHour = hourAvail.get(position);
        if(!currentHour.isAvailable()){
            Toast.makeText(this, "Please select an available time slot", Toast.LENGTH_LONG).show();
        }else{
            Intent intent= new Intent(AvailabilityActivity.this, BookingActivity.class);
            Bundle bundle = new Bundle();
            bundle.putInt("profId", profId);
            bundle.putString("profName", profName);
            bundle.putString("date", DATE_FORMAT.format(selectedDate));
            bundle.putString("startTime", currentHour.getStartTime());
            bundle.putString("endTime", currentHour.getEndTime());
            intent.putExtras(bundle);
            startActivity(intent);
        }
    }

    public void setAvailablity(String timeString){
        Log.i(TAG, "current hour:" + timeString);
        for(AvailabilityDataModel hour: hourAvail){
            if(hour.getStartTime().equals(timeString.substring(0,5))){
                hour.setUnavailable();
                Log.i(TAG, hour.getStartTime() + " is set to unavailable");
            }
        }
    }

}
