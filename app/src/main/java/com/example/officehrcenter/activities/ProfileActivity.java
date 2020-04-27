package com.example.officehrcenter.activities;

import android.app.Activity;
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

import androidx.appcompat.app.AppCompatActivity;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import com.example.officehrcenter.R;
import com.example.officehrcenter.application.App;

public class ProfileActivity extends AppCompatActivity {

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    private App myApp;
    private TabHost tabHost;
    private ListView upcominglistview;
    private ListView historylistview;
    private String emailaddress;
    private String emaildate;
    private int studentid;
    private Statement stmt = null;
    private Connection con = null;
    private ArrayList<String> upcomingList =new ArrayList<String>();
    private ArrayList<String> historyList =new ArrayList<String>();
    private ArrayAdapter upcomingadapter;
    private ArrayAdapter historyadapter;

    private Thread t = null;


    @Override
    public void onCreate(Bundle savedInstanceState) {
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

        // --------------------------------Tab 2-----------------------------------
        spec = tabHost.newTabSpec("history");	//create new tab specification
        spec.setContent(R.id.tab2);                 //add tab view content
        spec.setIndicator("History");              //put text on tab
        tabHost.addTab(spec);                       //put tab in TabHost container

        upcominglistview = (ListView)findViewById(R.id.upcominglist);
        historylistview = (ListView)findViewById(R.id.historylist);

        //store email and date when click
        upcominglistview.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String s =upcomingList.get(position);
                String tokens[]=s.split(" ");
                emailaddress= tokens[tokens.length-1];
                emaildate=tokens[1]+" "+tokens[2];
            }
        });



        upcomingadapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, upcomingList);
        upcomingadapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item);
        upcominglistview.setAdapter(upcomingadapter);


        historyadapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, historyList);
        historyadapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item);
        historylistview.setAdapter(historyadapter);

        t = new Thread(background);
        t.start();



    }

    private Runnable background = new Runnable() {
        public void run() {
            Intent intent=getIntent();
            studentid=myApp.getID();
            System.out.println(studentid);

            DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
            System.out.println("studentid"+ studentid);
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

            String query = "select * from reservation join users on reservation.professor_id=users.id where student_id ="+String.valueOf(studentid)+" ;";
            Log.e("JDBC", query);

            try {
                // execute SQL commands to create table, insert data, select contents
                ResultSet result = stmt.executeQuery(query);

                //read result set, write data to Log
                if (result.wasNull()) {
                    handler.sendEmptyMessage(0);
                } else {
                    while (result.next()) {
                        String name = result.getString("name");
                        String email=result.getString("email");
                        Date date = result.getTimestamp("reserved_time");
                        Date now= new Date();
                        String strDate = dateFormat.format(date);
                        System.out.println(name + "  "+ strDate);
                        if(now.compareTo(date)>=0){
                            //history
                            historyList.add("Time: "+strDate+" Professor: "+name+" Email: "+ email);

                        }else{
                            //upcoming
                            upcomingList.add("Time: "+strDate+" Professor: "+name+" Email: "+ email);
                        }
                        System.out.println(historyList.size());
                        System.out.println(upcomingList.size());

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

                    System.out.println("no reservation history");
                case 1:
                    upcomingadapter.notifyDataSetChanged();
                    historyadapter.notifyDataSetChanged();
                    System.out.println("has reservation history");

            }

        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){

            //go to booking activity
            case R.id.booking:
                Intent i= new Intent(ProfileActivity.this, ProfOverviewActivity.class);
                i.putExtra("studentid",studentid);
                startActivity(i);
                return true;

            //send email
            case R.id.email:
                if(!emailaddress.isEmpty()){
                    Intent msg = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:"));
                    msg.putExtra(Intent.EXTRA_EMAIL, emailaddress);
                    msg.putExtra(Intent.EXTRA_TEXT, "Reminder: You have an appointment on"+emaildate);
                    msg.putExtra(Intent.EXTRA_SUBJECT, "Appointment Reminder");
                    if (msg.resolveActivity(getPackageManager()) != null) {
                        startActivity(msg);
                    }
                    return true;
                }


        }

        return true;
    }


}
