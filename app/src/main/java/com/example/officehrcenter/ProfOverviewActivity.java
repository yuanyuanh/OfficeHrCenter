package com.example.officehrcenter;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;


public class ProfOverviewActivity extends AppCompatActivity implements View.OnClickListener {

    private ListView listview;

    private ArrayAdapter adapter;
    private Button btn;
    private EditText editText;

    private Statement stmt = null;
    private Connection con = null;


    private ArrayList<String> nameList =new ArrayList<String>();
    private ArrayList<String> backupList =new ArrayList<String>();

    private Thread t = null;
    private Toast toast;

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    toast.makeText(ProfOverviewActivity.this, "No Prof. available",
                            Toast.LENGTH_LONG).show();
                case 1:
                    adapter.notifyDataSetChanged();
            }

        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profoverview);

        btn=(Button)findViewById(R.id.search_button);
        btn.setOnClickListener(this);
        editText= (EditText)findViewById(R.id.editTextsearch);

        listview = (ListView)findViewById(R.id.list);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String s =nameList.get(position);
                //Toast.makeText(parent.getContext(), "List item selected "+s, Toast.LENGTH_LONG).show();
                String tokens[]=s.split(" ");
                int pos = s.indexOf(" ");
                String name = s.substring(pos+1);
                int profid=Integer.parseInt(tokens[0]);
                Intent intent= new Intent(ProfOverviewActivity.this , // aim class not created now
                        BookingActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("profId", profid);
                bundle.putString("profName", name);
                intent.putExtras(bundle);
                startActivity(intent);

            }
        });



        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, nameList);
        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item);
        listview.setAdapter(adapter);

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

            String query = "select * from users where occupation = \"professor\";";
            Log.e("JDBC", query);

            try {
                // execute SQL commands to create table, insert data, select contents
                ResultSet result = stmt.executeQuery(query);

                //read result set, write data to Log
                if (result.wasNull()) {
                    handler.sendEmptyMessage(0);
                } else {
                    while (result.next()) {
                        int id = result.getInt("id");
                        String name = result.getString("name");
                        nameList.add(String.valueOf(id)+" "+name);
                        backupList.add(String.valueOf(id)+" "+name);

                    }
                    handler.sendEmptyMessage(1);
                    System.out.println(String.valueOf(backupList.size()));
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


    @Override
    public void onClick(View v) {
        System.out.println("Listener: "+String.valueOf(backupList.size()));
        String s=editText.getText().toString();
        nameList.clear();
        for(String s1 : backupList){
            String[] t= s1.split(" ");
            int k=t[0].length()+1;
            String s2=s1.substring(k);
            System.out.println("Listener:"+s2);
            if(s2.equals(s)){
                nameList.add(s1);
            }
        }
        adapter.notifyDataSetChanged();
        Toast.makeText(this,
                "OnClickListener : "+ String.valueOf(nameList.size()),
                Toast.LENGTH_SHORT).show();
    }
}
