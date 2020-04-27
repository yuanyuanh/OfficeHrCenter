package com.example.officehrcenter;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;

public class BookingActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private TextView profNameText;
    private Spinner  dateSpinner;
    private ArrayAdapter adapter;
    private Spinner timeSpinner;
    private ArrayAdapter timeAdapter;
    private EditText msgEdit;
    private TextView endTimeText;

    private Statement stmt = null;
    private Connection con = null;

    private int profId;
    private ArrayList<Date> fullDateList = new ArrayList<Date>();
    private ArrayList<String> dateList =new ArrayList<String>();
    private ArrayList<String> timeList = new ArrayList<String>();

    private Thread t = null;
    private Toast toast;

    private String selectedDate;
    private String selectedTime;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        profNameText = (TextView)findViewById(R.id.profNameText);
        msgEdit = (EditText)findViewById(R.id.msgEdit);
        endTimeText = (TextView)findViewById(R.id.endTimeText);

        dateList.add(" ");
        dateSpinner = (Spinner)findViewById(R.id.dateSpinner);
        dateSpinner.setOnItemSelectedListener(this);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, dateList);
        dateSpinner.setAdapter(adapter);  //connect ArrayAdapter to <Spinner>

        timeList.add(" ");
        timeSpinner = (Spinner)findViewById(R.id.startTimeSpinner);
        timeSpinner.setOnItemSelectedListener(this);
        timeAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, timeList);
        timeSpinner.setAdapter(timeAdapter);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        profId = bundle.getInt("profId");
        profNameText.setText(bundle.getString("profName"));

        t = new Thread(background);
        t.start();
    }

    private Runnable background = new Runnable() {
        public void run() {
            JDBCHelper dbConn = new JDBCHelper();
            dbConn.connenctDB();
            String query = "select * from reservation where professor_id = " + profId + " and student_id is NULL;";
            ResultSet result = dbConn.select(query);
            try {
                if (result.wasNull()) {
                    handler.sendEmptyMessage(0);
                } else {
                    while (result.next()) {
                        Date date = result.getTimestamp("reserved_time");
                        fullDateList.add(date);
                        String s[] = (date.toString()).split(" ");
                        String date1 = s[0];
                        if (dateList.contains(date1) == false) {
                            dateList.add(date1);
                        }
                    }
                    handler.sendEmptyMessage(1);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            dbConn.disConnect();
            t = null;
        }
    };

    public void sendRequest(View view) {
        t = new Thread(request);
        t.start();
    }

    private Runnable request = new Runnable() {
        public void run() {
            JDBCHelper dbConn = new JDBCHelper();
            dbConn.connenctDB();

            String reservedTime = selectedDate + " " + selectedTime + ":00";
            String query = "update reservation set student_id=" + LoginActivity.userId + ", reserved_time=\'" +
                    reservedTime + "\', reserved_status = \'booked\', request_time = current_timestamp(), msg = \'"
                    + msgEdit.getText().toString() + "\' where professor_id=" + profId
                    + " and reserved_time=\'" + reservedTime + "\' and reserved_status is null and student_id is null";
            int count = dbConn.update(query);
            switch (count) {
                case 0:
                    handler.sendEmptyMessage(2);
                default:
                    //TODO: back to profile activity
                    Log.e("JDBC", "Booking succeeed");
            }
            dbConn.disConnect();
            //clean up
            t = null;
        }

    };


    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    toast.makeText(BookingActivity.this, "No Timeslot Available for " + profNameText.getText().toString(),
                            Toast.LENGTH_LONG).show();
                case 1:
                    adapter.notifyDataSetChanged();
                case 2:
                    toast.makeText(BookingActivity.this,"The time slot is occupied. Try another one.",
                            Toast.LENGTH_LONG).show();
            }

        }
    };

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Spinner sp = (Spinner)parent;
        Spinner sp1 = (Spinner)parent;

        if (position == 0) return;
        if (sp.getId() == R.id.dateSpinner) {
            String selected = dateList.get(position);
            selectedDate = selected;
            timeList.clear();
            timeList.add(" ");
            Iterator<Date> iter = fullDateList.iterator();
            while (iter.hasNext()) {
                Date d = iter.next();
                String str[] = d.toString().split(" ");
                String date = str[0];
                if (date.equals(selected)) {
                    String time = str[1].substring(0, 5);
                    timeList.add(time);
                }
            }
            timeAdapter.notifyDataSetChanged();
        } else if (sp1.getId() == R.id.startTimeSpinner) {
            String st = timeList.get(position);
            selectedTime = st;
            LocalTime startTime = LocalTime.parse(st);
            LocalTime endTime = startTime.plusMinutes(30);
            endTimeText.setText(endTime.toString());
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


}
