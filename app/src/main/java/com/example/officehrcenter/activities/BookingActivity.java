package com.example.officehrcenter.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.officehrcenter.R;
import com.example.officehrcenter.application.App;
import com.example.officehrcenter.objects.JDBCHelper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Locale;

public class BookingActivity extends AppCompatActivity implements TextToSpeech.OnInitListener, View.OnClickListener {

    private App myApp;

    private TextView titleText;
    private TextView profNameText;
    private TextView dateText;
    private TextView startTimeText;
    private TextView endTimeText;
    private TextView messageTextView;
    private EditText messageText;
    private Button bookingBtn;

    private TextToSpeech speaker;

    private int profId;
    private String profName;
    private String date;
    private String startTime;
    private String endTime;
    private String message;
    private String reservedTime;

    private Thread t = null;
    private final String TAG = "booking activity";
    private final String TAG_SPEAKER = "speaker";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);
        myApp = (App)getApplication();

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        profId = bundle.getInt("profId");
        profName = bundle.getString("profName");
        date = bundle.getString("date");
        startTime = bundle.getString("startTime");
        endTime = bundle.getString("endTime");

        titleText = (TextView)findViewById(R.id.bookingTitle);
        profNameText = (TextView)findViewById(R.id.profNameText);
        dateText = (TextView)findViewById(R.id.dateText);
        startTimeText = (TextView)findViewById(R.id.startTimeText);
        endTimeText = (TextView)findViewById(R.id.endTimeText);
        messageTextView = (TextView)findViewById(R.id.messageTextView);
        messageText = (EditText)findViewById(R.id.messageText);
        bookingBtn = (Button)findViewById(R.id.bookingBtn);
        bookingBtn.setOnClickListener(this);

        if(myApp.isProf()){
            titleText.setText("Set my own time");
            profNameText.setVisibility(View.INVISIBLE);
            messageText.setVisibility(View.INVISIBLE);
            messageTextView.setVisibility(View.INVISIBLE);
        }else{
            titleText.setText("Reserve a meeting");
        }
        profNameText.setText("To meet: " + profName);
        dateText.setText(date);
        startTimeText.setText(startTime);
        endTimeText.setText(endTime);

        reservedTime = date + "T" + startTime + ":00";

        //Initialize Text to Speech engine (context, listener object)
        speaker = new TextToSpeech(this, this);
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
                Log.e(TAG_SPEAKER, "Language is not available.");
            } else {
                // The TTS engine has been successfully initialized
                Log.i(TAG_SPEAKER, "TTS Initialization successful.");
            }
        } else {
            // Initialization failed.
            Log.e(TAG_SPEAKER, "Could not initialize TextToSpeech.");
        }
    }

    @Override
    public void onClick(View v) {
        Log.i(TAG, "button clicked");
        message = messageText.getText().toString().trim();
        if (!message.isEmpty()) {
            // if speaker is talking, stop it
            if(speaker.isSpeaking()){
                Log.i(TAG_SPEAKER, "Speaker Speaking");
                speaker.stop();
                // else start speech
            } else {
                Log.i(TAG_SPEAKER, "Speaker Not Already Speaking");
                speak("Your message is: " + message);
            }
        }
        t = new Thread(checkAvail);
        t.start();
    }

    private Runnable checkAvail = new Runnable() {
        public void run() {
            JDBCHelper dbConn = new JDBCHelper();
            dbConn.connenctDB();

            String query = "select * from reservation where professor_id = " + profId +
                    " and reserved_time = \'" + reservedTime + "\';";

            ResultSet result = dbConn.select(query);
            try{
                if (!result.next()) {
                    bookingHandler.sendEmptyMessage(2);
                } else {
                    bookingHandler.sendEmptyMessage(3);
                }
            }catch (SQLException e) {
                e.printStackTrace();
            }
            t = null; // end of the current thread
            dbConn.disConnect();
        }

    };

    private Runnable request = new Runnable() {
        public void run() {
            JDBCHelper dbConn = new JDBCHelper();
            dbConn.connenctDB();

            String reservedTime = date + "T" + startTime + ":00";

            String query = "insert into reservation (professor_id, student_id, reserved_time, reserved_status, request_time, msg) values "+
                    "(" + profId + ", ";
            if(myApp.isProf()){
                query += "null, ";
            }else{
                query += myApp.getID() + ",";
            }
            query += "\'" + reservedTime + "\'," +
                    "\'booked\'," +
                    "current_timestamp(),";
            if(message.isEmpty()){
                query += "null);";
            }else{
                query += "\'" + message + "\');";
            }

            int count = dbConn.update(query);

            if (count > 0) {
                bookingHandler.sendEmptyMessage(0);
            } else {
                bookingHandler.sendEmptyMessage(1);
            }

            dbConn.disConnect();
            //clean up
            t = null;
        }

    };

    private Handler bookingHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    Log.i(TAG, "Book successfully");
                    // A toast indicating successful result
                    if (myApp.isProf()){
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(BookingActivity.this,"You've added an unavailable time slot",Toast.LENGTH_LONG).show();
                            }
                        });
                    }else{
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(BookingActivity.this,"You've reserved a meeting",Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                    Intent i = new Intent(BookingActivity.this, ProfileActivity.class);
                    startActivity(i);
                    finish();
                    break;
                case 1:
                    Log.i(TAG, "Fail to book");
                    finish();
                    break;
                case 2:
                    Log.i(TAG, "Time slot available");
                    t = new Thread(request);
                    t.start();
                    break;
                case 3:
                    Log.i(TAG, "Time slot occupied");
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(BookingActivity.this,"The requested time slot is already occupied",Toast.LENGTH_LONG).show();
                        }
                    });
                    finish();
                    break;
            }
        }
    };

    //speak methods will send text to be spoken
    public void speak(String output){
        //	speaker.speak(output, TextToSpeech.QUEUE_FLUSH, null);  //for APIs before 21
        speaker.speak(output, TextToSpeech.QUEUE_FLUSH, null, "Id 0");
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
