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

    static final long ONE_MINUTE_IN_MILLIS=60000;

    private TextView profNameText;
    private Spinner  dateSpinner;
    private ArrayAdapter adapter;
    private Spinner timeSpinner;
    private ArrayAdapter timeAdapter;
    private EditText msgEdit;
    private TextView endTimeText;

    private Statement stmt = null;
    private Connection con = null;

    private int profId = 3;
    private ArrayList<Date> fullDateList = new ArrayList<Date>();
    private ArrayList<String> dateList =new ArrayList<String>();
    private ArrayList<String> timeList = new ArrayList<String>();

    private Thread t = null;
    private Toast toast;


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

        t = new Thread(background);
        t.start();


    }

    private Runnable background = new Runnable() {
        public void run() {
            String URL = "jdbc:mysql://frodo.bentley.edu:3306/officehrdb";
            String dbusername = "harry";
            String dbpassword = "harry";
            try { //load driver into VM memory
                Class.forName("com.mysql.jdbc.Driver");
            } catch (ClassNotFoundException e) {
                Log.e("JDBC", "Did not load driver");

            }

            try { //create connection and statement objects
                con = DriverManager.getConnection(
                        URL,
                        dbusername,
                        dbpassword);
                stmt = con.createStatement();
            } catch (SQLException e) {
                Log.e("JDBC", "problem connecting");
            }

            String query = "select * from reservation where professor_id = " + profId + " and student_id is NULL;";
            Log.e("JDBC", query);

            try {
                // execute SQL commands to create table, insert data, select contents
                ResultSet result = stmt.executeQuery(query);

                //read result set, write data to Log
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
                Log.e("JDBC", "problems with SQL sent to " + URL +
                        ": " + e.getMessage());
            } finally {
                try { //close connection, may throw checked exception
                    if (con != null)
                        con.close();
                } catch (SQLException e) {
                    Log.e("JDBC", "close connection failed");
                }
            }
        }
    };

    public void sendRequest(View view) {

    }

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    toast.makeText(BookingActivity.this, "No Timeslot Available for " + profNameText.getText().toString(),
                            Toast.LENGTH_LONG).show();
                case 1:
                    adapter.notifyDataSetChanged();
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
            LocalTime startTime = LocalTime.parse(st);
            LocalTime endTime = startTime.plusMinutes(30);
            endTimeText.setText(endTime.toString());
        }


    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


}
