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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;

public class BookingActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private TextView profNameText;
    private Spinner  dateSpinner;
    private ArrayAdapter adapter;

    private Statement stmt = null;
    private Connection con = null;

    private int profId = 3;
    private ArrayList<Date> dateList =new ArrayList<Date>();

    private Thread t = null;
    private Toast toast;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        profNameText = (TextView)findViewById(R.id.profNameText);
        dateSpinner = (Spinner)findViewById(R.id.dateSpinner);
        dateSpinner.setOnItemSelectedListener(this);
        adapter = new ArrayAdapter<Date>(this, android.R.layout.simple_spinner_item, dateList);
        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item);
        dateSpinner.setAdapter(adapter);  //connect ArrayAdapter to <Spinner>

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
                        Date date = result.getDate("reserved_time");
                        dateList.add(date);
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

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    toast.makeText(BookingActivity.this, "No Timeslot Available for " + profNameText.getText().toString(),
                            Toast.LENGTH_LONG).show();
                case 1:


            }

        }
    };

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
