package com.example.officehrcenter.activities;

/** This activity shows the users their upcoming and history reservations.
 *  Students can send email to professor to remind them of the meeting.
 *  The actionbar at the top right corner enables students to make new reservations and professor to update the schedules.
 * @version 1.0
 */

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import androidx.appcompat.app.AppCompatActivity;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import com.example.officehrcenter.R;
import com.example.officehrcenter.adapters.ProfileAdapter;
import com.example.officehrcenter.application.App;
import com.example.officehrcenter.objects.JDBCHelper;
import com.example.officehrcenter.objects.ProfileDataModel;

public class ProfileActivity extends AppCompatActivity implements OnItemClickListener{

    private App myApp; // current application

    private TabHost tabHost;
    private ListView upcomingListView;
    private ListView historyListView;
    private ArrayList<ProfileDataModel> upcomingList = new ArrayList<ProfileDataModel>();
    private ArrayList<ProfileDataModel> historyList = new ArrayList<ProfileDataModel>();

    private String emailAddress = "";
    private String emailDate = "";
    private String phone = "";

    private ArrayAdapter<ProfileDataModel> upcomingAdapter;
    private ArrayAdapter<ProfileDataModel> historyAdapter;

    private final String TAG = "Profile"; // for the use of log
    private Thread t = null;
    private JDBCHelper dbConn = new JDBCHelper(); // JDBC helper for connecting and making queries to DB
    private Date now= new Date();
    private ProfileDataModel currentData;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        myApp = (App)getApplication();

        tabHost=(TabHost)findViewById(R.id.profile);
        tabHost.setup();

        TabHost.TabSpec spec;

        // --------------------------------Tab 1-----------------------------------
        spec = tabHost.newTabSpec("upcoming");	//create new tab specification
        spec.setContent(R.id.tab1);                 //add tab view content
        spec.setIndicator("Upcoming");              //put text on tab
        tabHost.addTab(spec);                       //put tab in TabHost container
        upcomingListView = (ListView)findViewById(R.id.upcominglist);
        upcomingAdapter = new ProfileAdapter(upcomingList,getApplicationContext());
        upcomingListView.setAdapter(upcomingAdapter);
        upcomingListView.setOnItemClickListener(this);

        // --------------------------------Tab 2-----------------------------------
        spec = tabHost.newTabSpec("history");	//create new tab specification
        spec.setContent(R.id.tab2);                 //add tab view content
        spec.setIndicator("History");              //put text on tab
        tabHost.addTab(spec);                       //put tab in TabHost container
        historyListView = (ListView)findViewById(R.id.historylist);
        historyAdapter = new ProfileAdapter(historyList,getApplicationContext());
        historyListView.setAdapter(historyAdapter);
        historyListView.setOnItemClickListener(this);

        t = new Thread(background);
        t.start();

    }

    private Runnable background = new Runnable() {
        public void run() {

            dbConn.connenctDB();
            String query = "select users.id, username, email, phone, office, reserved_time, msg from reservation join users ";

            if (myApp.isProf()) {
                query += "on reservation.student_id = users.id where professor_id = " + myApp.getID() + ";";
            } else {
                query += "on reservation.professor_id = users.id where student_id = " + myApp.getID() + ";";
            }

            ResultSet result = dbConn.select(query);
            try {
                if (result.wasNull()) {
                    Log.i(TAG, "No records found");
                    handler.sendEmptyMessage(0);
                } else {
                    while (result.next()) {
                        int id = result.getInt("id");
                        String name = result.getString("username");
                        String email = result.getString("email");
                        String phone = result.getString("phone");
                        String office = result.getString("office");
                        Date dateTime = result.getTimestamp("reserved_time");
                        String msg = result.getString("msg");
                        currentData = new ProfileDataModel(id, name, email, phone, office, dateTime, msg);

                        if (now.compareTo(dateTime) >= 0) {
                            //history
                            historyList.add(currentData);
                        } else {
                            //upcoming
                            upcomingList.add(currentData);
                        }
                    }
                    Log.i(TAG, "Query results added to array lists");
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
                    Log.i(TAG, "no reservation history");
                    // A toast indicating no reservation
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(ProfileActivity.this,"You don't have any reservations",Toast.LENGTH_LONG).show();
                        }
                    });
                    break;
                case 1:
                    upcomingAdapter.notifyDataSetChanged();
                    historyAdapter.notifyDataSetChanged();
                    Log.i(TAG, "Reservation history updated successfully");
                    break;
                }
            }
        };

    // listener methods for callbacks
    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
        if(parent.getId() == R.id.upcominglist) {
            currentData = upcomingList.get(position);
            emailDate = currentData.getTime();
            emailAddress = currentData.getEmail();
            phone = currentData.getPhone();
            Log.i(TAG, "stored info: " + emailAddress + ", " + emailDate + ", " + phone);
        }else{
            currentData = historyList.get(position);
            emailDate = currentData.getTime();
            emailAddress = currentData.getEmail();
            phone = currentData.getPhone();
            Log.i(TAG, "stored info: " + emailAddress + ", " + emailDate + ", " + phone);
        }
        v.setSelected(true);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    public boolean onPrepareOptionsMenu(Menu menu)
    {
        MenuItem booking = menu.findItem(R.id.booking);
        MenuItem profAvail = menu.findItem(R.id.profAvail);
        if(myApp.isProf()){
            booking.setVisible(false);
            profAvail.setVisible(true);
        }else{
            booking.setVisible(true);
            profAvail.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){

            // go to booking activity
            case R.id.booking:
                Intent booking = new Intent(ProfileActivity.this, ProfOverviewActivity.class);
                startActivity(booking);
                return true;

            // go to profAvail activity
            case R.id.profAvail:
                Intent avail = new Intent(ProfileActivity.this, AvailabilityActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("profId", myApp.getID());
                avail.putExtras(bundle);
                startActivity(avail);
                return true;

            // send email
            case R.id.email:
                if (emailAddress.equals("Not available")) {
                    Toast.makeText(this, "Sorry, we don't have the requested email address", Toast.LENGTH_LONG).show();
                }else if(!emailAddress.equals("")){
                    String mailTo = "mailto:" + emailAddress + "?subject=" + Uri.encode("Appointment Reminder") +
                            "&body=" + Uri.encode("Reminder: You have an appointment at " + emailDate);
                    Intent mail = new Intent(Intent.ACTION_SENDTO);
                    mail.setData(Uri.parse(mailTo));
                    if (mail.resolveActivity(getPackageManager()) != null) {
                        startActivity(mail);
                    }
                }else{
                    Toast.makeText(this,"Please select an appointment to send a email to the person to meet", Toast.LENGTH_LONG).show();
                }
                return true;

            // dial
            case R.id.dial:
                if(phone.equals("Not available")){
                    Toast.makeText(this, "Sorry, we don't have the requested phone number", Toast.LENGTH_LONG).show();
                }else if(!phone.equals("")){
                    Intent dial = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone));
                    if (dial.resolveActivity(getPackageManager()) != null) {
                        startActivity(dial);
                    }
                }else{
                    Toast.makeText(this,"Please select an appointment to call the person to meet", Toast.LENGTH_LONG).show();
                }
                return true;
            case R.id.sms:
                if(phone.equals("Not available")){
                    Toast.makeText(this, "Sorry, we don't have the requested phone number", Toast.LENGTH_LONG).show();
                }else if(!phone.equals("")){
                    Intent sms = new Intent(Intent.ACTION_VIEW, Uri.parse("sms:" + phone));
                    if (sms.resolveActivity(getPackageManager()) != null) {
                        startActivity(sms);
                    }
                }else{
                    Toast.makeText(this,"Please select an appointment to call the person to meet", Toast.LENGTH_LONG).show();
                }
                return true;

        }
        return true;
    }


}
