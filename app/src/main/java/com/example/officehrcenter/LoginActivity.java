package com.example.officehrcenter;

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

public class LoginActivity extends AppCompatActivity {

    public static final int requestCode_235 = 235;

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

    private Runnable background = new Runnable() {
        public void run() {
            String URL = "jdbc:mysql://frodo.bentley.edu:3306/officehrdb";
            String dbusername = "harry";
            String dbpassword = "harry";
            String username = usernameText.getText().toString();
            String password = passwordText.getText().toString();

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

            String query = "select * from users where username=\'" + username + "\' and password=\'" + password + "\';";
            Log.e("JDBC", query);
            try {
                // execute SQL commands to create table, insert data, select contents
                ResultSet result = stmt.executeQuery(query);

                //read result set, write data to Log

                if (result.next() == false) {
                    Log.e("JDBC", "No users found");
                    handler.sendEmptyMessage(0);
                } else {
                    Log.e("JDBC", "success connection");
                    handler.sendEmptyMessage(1);
                }

                //clean up
                t = null;

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
                    signupText.setVisibility(View.VISIBLE);
                case 1:
                    Intent i = new Intent(LoginActivity.this, BookingActivity.class);
                    startActivity(i);
            }

        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        t = null;
        try { //close connection, may throw checked exception
            if (con != null)
                con.close();
        } catch (SQLException e) {
            Log.e("JDBC", "close connection failed");
        }
    }


}
