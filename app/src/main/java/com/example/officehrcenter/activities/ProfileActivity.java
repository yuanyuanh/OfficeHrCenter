package com.example.officehrcenter.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.Toast;

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

public class ProfileActivity extends Activity {

    private App myApp;

    private TabHost tabHost;
    private ListView upcominglistview;
    private ListView historylistview;
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
        Toast.makeText(this, "" + myApp.getID(),Toast.LENGTH_SHORT).show();

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

            String query = "select * from reservation join users on reservation.professor_id=users.id where student_id ="+String.valueOf(myApp.getID())+" ;";
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

                        Date date = result.getTimestamp("reserved_time");
                        Date now= new Date();
                        String strDate = dateFormat.format(date);
                        System.out.println(name + "  "+ strDate);
                        if(now.compareTo(date)>=0){
                            //history
                            historyList.add("Time: "+strDate+" Professor: "+name);

                        }else{
                            //upcoming
                            upcomingList.add("Time: "+strDate+" Professor: "+name);
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

                    System.out.println("has reservation history");

            }

        }
    };


}
