package com.example.officehrcenter.activities;

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
import android.widget.Toast;

import com.example.officehrcenter.R;
import com.example.officehrcenter.application.App;

public class LoginActivity extends AppCompatActivity {

    private App myApp;

    public static final int requestCode_235 = 235;
    public static int userId;

    private Thread t = null;
    private EditText usernameText;
    private EditText passwordText;
    private Button loginButt;
    private Button signupButt;
    private TextView signupText;

    private Statement stmt = null;
    private Connection con = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        myApp = (App)getApplication();

        usernameText = (EditText) findViewById(R.id.userName);
        passwordText = (EditText) findViewById(R.id.password);
        loginButt = (Button) findViewById(R.id.loginButt);
        signupButt = (Button) findViewById(R.id.signupButt);
        signupText = (TextView) findViewById(R.id.signupText);
    }

    public void login(View view) {
        t = new Thread(background);
        t.start();
    }

    public void signUp(View view) {
        Intent i = new Intent(this, SignupActivity.class);
        startActivityForResult(i, requestCode_235);
    }

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    signupText.setVisibility(View.VISIBLE);
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

    private Runnable background = new Runnable() {
        public void run() {

            String username = usernameText.getText().toString();
            String password = passwordText.getText().toString();

            JDBCHelper dbConn = new JDBCHelper();
            dbConn.connenctDB();

            String query = "select * from users where username=\'" + username + "\' and password=\'" + password + "\';";
            ResultSet result = dbConn.select(query);
            try {
                if (!result.next()) {
                    Log.e("JDBC", "No users found");
                    handler.sendEmptyMessage(0);
                } else {
                    userId = result.getInt("id");
                    myApp.setID(userId);
                    Log.e("JDBC", "success connection");
                    handler.sendEmptyMessage(1);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            t = null;
            dbConn.disConnect();
        }
    };


    @Override
    protected void onDestroy() {
        super.onDestroy();
        t = null;
    }

}
