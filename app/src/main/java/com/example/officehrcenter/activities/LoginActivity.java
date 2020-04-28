package com.example.officehrcenter.activities;

/** This is the login activity where user type in their name and password to get access to the reservation service.
 * @version 1.0
 */

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.officehrcenter.R;
import com.example.officehrcenter.application.App;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private App myApp; // current application
    private Thread t = null;
    private JDBCHelper dbConn = new JDBCHelper(); // JDBC helper for connecting and making queries to DB

    // widgets
    private EditText usernameText;
    private EditText passwordText;
    private TextView incorrectMessage;
    private Button loginButt;
    private Button signupButt;

    public static final int REQUEST_CODE_235 = 235;
    private final String TAG = "Login"; // for the use of log

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        myApp = (App)getApplication();

        usernameText = (EditText) findViewById(R.id.userName);
        passwordText = (EditText) findViewById(R.id.password);
        incorrectMessage = (TextView) findViewById(R.id.incorrectMessage);
        incorrectMessage.setVisibility(View.INVISIBLE);

        loginButt = (Button) findViewById(R.id.loginButt);
        loginButt.setOnClickListener(this);
        signupButt = (Button) findViewById(R.id.signupButt);
        signupButt.setOnClickListener(this);

    }

    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.loginButt:
                Log.i(TAG, "A log-in attempt");
                login(v);
                break;

            case R.id.signupButt:
                Log.i(TAG, "A sign-up attempt");
                signUp(v);
                break;
        }
    }

    /* When the user attempt to log in, the thread for connecting to the database will be created.
     * If the username and password match the record in the database, the user will be led to his/her current profile.
     */
    public void login(View view) {
        t = new Thread(background);
        t.start();
    }

    private Runnable background = new Runnable() {
        public void run() {

            // get the user inputs
            String username = usernameText.getText().toString();
            String password = passwordText.getText().toString();

            dbConn.connenctDB();

            String query = "select * from users where username=\'" + username + "\' and password=\'" + password + "\';";
            ResultSet result = dbConn.select(query);
            try {
                if (!result.next()) {
                    Log.i(TAG, "No users found or wrong password");
                    handler.sendEmptyMessage(0);
                } else {
                    myApp.setID(result.getInt("id"));
                    myApp.setIfProf(result.getString("occupation"));
                    Log.i(TAG, "UserID: " + myApp.getID() + " log in successfully.");
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
                    incorrectMessage.setVisibility(View.VISIBLE);
                    break;
                case 1:
                    //Intent i = new Intent(LoginActivity.this, BookingActivity.class);
                    Intent i = new Intent(LoginActivity.this, ProfileActivity.class);
                    startActivity(i);
                    finish();
                    break;
            }
        }
    };

    /* When the user attempt to sign up, he/she will be led to signUp activity.
     */
    public void signUp(View view) {
        Intent i = new Intent(this, SignupActivity.class);
        startActivityForResult(i, REQUEST_CODE_235);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        t = null;
    }

}
