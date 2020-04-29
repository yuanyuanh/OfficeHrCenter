package com.example.officehrcenter.activities;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import androidx.appcompat.app.AppCompatActivity;

import com.example.officehrcenter.R;
import com.example.officehrcenter.adapters.ProfOverviewAdapter;
import com.example.officehrcenter.application.App;
import com.example.officehrcenter.objects.JDBCHelper;
import com.example.officehrcenter.objects.ProfOverviewDataModel;
import com.example.officehrcenter.objects.ProfileDataModel;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

public class ProfOverviewActivity extends AppCompatActivity implements OnClickListener, OnItemClickListener {

    private App myApp; // current application

    // widgets
    private ListView profList;
    private Button searchBtn;
    private EditText keyWord;

    private ArrayList<ProfOverviewDataModel> profOverview = new ArrayList<ProfOverviewDataModel>();
    private ArrayList<ProfOverviewDataModel> resultList = new ArrayList<ProfOverviewDataModel>();

    private final String TAG = "Professor Overview"; // for the use of log
    private Thread t = null;
    private ArrayAdapter<ProfOverviewDataModel> profOverviewAdapter;
    private JDBCHelper dbConn = new JDBCHelper(); // JDBC helper for connecting and making queries to DB
    private ProfOverviewDataModel currentData;
    private AlertDialog dialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profoverview);
        myApp = (App)getApplication();

        keyWord= (EditText)findViewById(R.id.editTextsearch);
        searchBtn=(Button)findViewById(R.id.search_button);
        searchBtn.setOnClickListener(this);

        profList = (ListView)findViewById(R.id.profList);
        profList.setOnItemClickListener(this);
        profOverviewAdapter = new ProfOverviewAdapter(resultList, getApplicationContext());
        profList.setAdapter(profOverviewAdapter);

        t = new Thread(background);
        t.start();

    }

    private Runnable background = new Runnable() {
        public void run() {

            dbConn.connenctDB();
            String query = "select id, name, office from users where occupation = \"professor\";";

            ResultSet result = dbConn.select(query);
            try {
                if (result.wasNull()) {
                    Log.i(TAG, "No records found");
                    handler.sendEmptyMessage(0);
                } else {
                    while (result.next()) {
                        int id = result.getInt("id");
                        String name = result.getString("name");
                        String office = result.getString("office");
                        currentData = new ProfOverviewDataModel(id, name, office);
                        profOverview.add(currentData);
                        resultList.add(currentData);
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
                    Log.i(TAG, "no professor in the database");
                    // A toast indicating no professors
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(ProfOverviewActivity.this,"No available professor",Toast.LENGTH_LONG).show();
                        }
                    });
                case 1:
                    profOverviewAdapter.notifyDataSetChanged();
            }

        }
    };

    @Override
    public void onClick(View v) {
        String searchText = keyWord.getText().toString().trim();
        Log.i(TAG, "Searching for " + searchText);
        if (searchText.equals("")){
            resultList.clear();
            for(ProfOverviewDataModel prof : profOverview){
                resultList.add(prof);
            }
            Log.i(TAG, "Return the original list ");
        }else{
            resultList.clear();
            for(ProfOverviewDataModel prof : profOverview) {
                if (prof.getName().toLowerCase().contains(searchText.toLowerCase())) {
                    resultList.add(prof);
                    Log.i(TAG, "Professor " + prof.getName() + " is found.");
                }
            }
            Log.i(TAG, "Search finished");
        }
        profOverviewAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        currentData = resultList.get(position);
        Intent intent= new Intent(ProfOverviewActivity.this, AvailabilityActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt("profId", currentData.getId());
        bundle.putString("profName", currentData.getName());
        intent.putExtras(bundle);
        startActivity(intent);
        finish();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.prof_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.logOut:
                dialog = new AlertDialog.Builder(this).create();
                dialog.setTitle("Log out");
                dialog.setMessage("Are you sure that you want to log out?");
                dialog.setButton(DialogInterface.BUTTON_POSITIVE, "Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        myApp.setID(0);
                        myApp.setIfProf("student");
                        Intent logOut = new Intent(ProfOverviewActivity.this, LoginActivity.class);
                        startActivity(logOut);
                        finish();
                        Log.i(TAG, "Log out finished.");
                    }
                });
                dialog.setButton(DialogInterface.BUTTON_NEUTRAL, "Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {  }
                });
                dialog.show();

                return true;
        }
        return true;
    }


}
