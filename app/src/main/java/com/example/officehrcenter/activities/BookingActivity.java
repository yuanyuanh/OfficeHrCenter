package com.example.officehrcenter.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.officehrcenter.R;
import com.example.officehrcenter.application.App;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;

public class BookingActivity extends AppCompatActivity implements TextToSpeech.OnInitListener, AdapterView.OnItemSelectedListener {

    private App myApp;

    private TextView profNameText;
    private Spinner  dateSpinner;
    private ArrayAdapter adapter;
    private Spinner timeSpinner;
    private ArrayAdapter timeAdapter;
    private EditText msgEdit;
    private TextView endTimeText;
    private Button sendButt;

    private TextToSpeech speaker;

    private int profId;
    private ArrayList<Date> fullDateList = new ArrayList<Date>();
    private ArrayList<String> dateList =new ArrayList<String>();
    private ArrayList<String> timeList = new ArrayList<String>();

    private Thread t = null;

    private String selectedDate;
    private String selectedTime;

    private Handler bookingHandler = new Handler() {
        public void handleMessage(Message msg) {
            int i = msg.what;
            if (i == 0) {
                Toast.makeText(BookingActivity.this, "No Timeslot Available for " + profNameText.getText().toString(),
                        Toast.LENGTH_LONG).show();
            } else if (i == 1) {
                adapter.notifyDataSetChanged();
            } else if (i == 2) {
                Log.e("JDBC", msg.toString());
                Toast.makeText(BookingActivity.this,"The time slot is occupied. Try another one.",
                        Toast.LENGTH_LONG).show();
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);
        myApp = (App)getApplication();
        Toast.makeText(this, "Dealing with id: " + myApp.getID(),Toast.LENGTH_SHORT).show();

        profNameText = (TextView)findViewById(R.id.profNameText);
        msgEdit = (EditText)findViewById(R.id.msgEdit);
        endTimeText = (TextView)findViewById(R.id.endTimeText);
        sendButt = (Button)findViewById(R.id.sendButt);

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

        //Initialize Text to Speech engine (context, listener object)
        speaker = new TextToSpeech(this, this);

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
                    bookingHandler.sendEmptyMessage(0);
                } else {
                    while (result.next()) {
                        Date date = result.getTimestamp("reserved_time");
                        fullDateList.add(date);
                        String s[] = (date.toString()).split(" ");
                        String date1 = s[0];
                        if (dateList.contains(date1) == false) {
                            dateList.add(date1);
                            bookingHandler.sendEmptyMessage(1);
                        }
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            dbConn.disConnect();
            t = null;
        }
    };

    public void sendRequest(View view) {
        Log.e("JDBC", "button clicked");
        if (!(msgEdit.getText().toString().isEmpty())) {
            // if speaker is talking, stop it
            if(speaker.isSpeaking()){
                Log.i("Speaker", "Speaker Speaking");
                speaker.stop();
                // else start speech
            } else {
                Log.i("Speaker", "Speaker Not Already Speaking");
                speak("Your message is: " + msgEdit.getText().toString());
            }
        }

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
            if (count <= 0) {
                Log.e("JDBC", "inside count");
                bookingHandler.sendEmptyMessage(2);
            } else {
                //TODO: back to profile activity
                Log.e("JDBC", "Booking succeeed");
            }
            dbConn.disConnect();
            //clean up
            t = null;
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

    //speak methods will send text to be spoken
    public void speak(String output){
        //	speaker.speak(output, TextToSpeech.QUEUE_FLUSH, null);  //for APIs before 21
        speaker.speak(output, TextToSpeech.QUEUE_FLUSH, null, "Id 0");
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            // Set preferred language to US english.
            // If a language is not be available, the result will indicate it.
            int result = speaker.setLanguage(Locale.US);

            //int result = speaker.setLanguage(Locale.FRANCE);
            if (result == TextToSpeech.LANG_MISSING_DATA ||
                    result == TextToSpeech.LANG_NOT_SUPPORTED) {
                // Language data is missing or the language is not supported.
                Log.e("Speak", "Language is not available.");
            } else {
                // The TTS engine has been successfully initialized
                speak("Please enter your bill amount");
                Log.i("Speaker", "TTS Initialization successful.");
            }
        } else {
            // Initialization failed.
            Log.e("Speaker", "Could not initialize TextToSpeech.");
        }
    }

    public void onDestroy(){

        // shut down TTS engine
        if(speaker != null){
            speaker.stop();
            speaker.shutdown();
        }
        super.onDestroy();
    }
}
