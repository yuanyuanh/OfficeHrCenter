package com.example.officehrcenter;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ProfOverviewActivity extends AppCompatActivity implements  AdapterView.OnItemClickListener, View.OnClickListener {

    String URL = "jdbc:mysql://frodo.bentley.edu:3306/officehrdb";
    String dbusername = "harry";
    String dbpassword = "harry";

    private static final String tag = "Widgets";
    private CustomAdapter adapt = null;
    private String selected;
    private ListView listview;
    // init empty list
    private List<String> listoptions = new ArrayList<String>();
    private Button searchbtn;
    private EditText searchbox;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profoverview);
        searchbtn = (Button)findViewById(R.id.searchbtn);
        searchbox=(EditText) findViewById(R.id.searchbox);
        searchbtn.setOnClickListener(this);

        listview = (ListView)findViewById(R.id.list);
        listview.setOnItemClickListener(this);
        listoptions.clear();

        // connect Mysql DBMS
        try { //load driver into VM memory
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            Log.e("JDBC", "Did not load driver");

        }

        Statement stmt = null;
        Connection con = null;
        try { //create connection and statement objects
            con = DriverManager.getConnection(
                    URL,
                    dbusername,
                    dbpassword);
            stmt = con.createStatement();
        } catch (SQLException e) {
            Log.e("JDBC", "problem connecting");
        }
        String query = "select * from users where occupation=\'professor\';";
        Log.e("JDBC", query);
        try {
            // execute SQL commands to create table, insert data, select contents
            ResultSet result = stmt.executeQuery(query);

            //read result set, add to list
            while(result.next()){
                int id= result.getInt("id");
                String sid= String.valueOf(id);
                String name= result.getString("name");
                listoptions.add(sid+" "+name);
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

        adapt =new CustomAdapter (this, listoptions);
        listview.setAdapter(adapt);





    }





    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String selected=adapt.getItem(position);
        String[] tokens= selected.split(" ");
        String selectedid= tokens[0];
        Intent intent= new Intent(ProfOverviewActivity.this , // aim class not created now
                 ProfOverviewActivity.class);
        intent.putExtra("id",selectedid);
        startActivity(intent);


    }

    @Override
    public void onClick(View v) {
        String name1= searchbox.getText().toString();

        // connect Mysql DBMS
        try { //load driver into VM memory
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            Log.e("JDBC", "Did not load driver");

        }

        Statement stmt = null;
        Connection con = null;
        try { //create connection and statement objects
            con = DriverManager.getConnection(
                    URL,
                    dbusername,
                    dbpassword);
            stmt = con.createStatement();
        } catch (SQLException e) {
            Log.e("JDBC", "problem connecting");
        }
        String query = "select * from users where name = \'" + name1 + "\' and occupation=\'professor\';";
        Log.e("JDBC", query);
        try {
            // execute SQL commands to create table, insert data, select contents
            ResultSet result = stmt.executeQuery(query);

            //read result set, add to list
            listoptions.clear();
            while(result.next()){
                int id= result.getInt("id");
                String sid= String.valueOf(id);
                String name= result.getString("name");
                listoptions.add(sid+" "+name);
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

        adapt.notifyDataSetChanged();
        searchbox.setText("", TextView.BufferType.EDITABLE);
    }

    class CustomAdapter extends ArrayAdapter<String>
    {
        Context context;
        List<String> title;


        CustomAdapter(Context c, List<String> title)
        {

            super(c, R.layout.activity_profitem,title);
            this.context = c;
            this.title=title;

        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            LayoutInflater vi = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View row = vi.inflate(R.layout.activity_profitem, parent, false);
            TextView titlee = (TextView) row.findViewById(R.id.item1);
            int pos = position+1;
            titlee.setText( title.get(position));
            pos++;
            return row;
        }

    }



}
