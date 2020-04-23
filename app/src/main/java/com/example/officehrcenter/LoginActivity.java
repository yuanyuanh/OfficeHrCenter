package com.example.officehrcenter;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
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

    private Thread t = null;
    private EditText usernameText;
    private EditText passwordText;
    private Button loginButt;
    private TextView signupText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernameText = (EditText) findViewById(R.id.userName);
        passwordText = (EditText) findViewById(R.id.password);
        loginButt = (Button) findViewById(R.id.loginButt);
        signupText = (TextView) findViewById(R.id.signupText);
    }

    public void login(View view) {
        t = new Thread(background);
        t.start();
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

            String query = "select * from users where username=\'" + username + "\' and password=\'" + password + "\';";
            Log.e("JDBC", query);
            try {
                // execute SQL commands to create table, insert data, select contents
                ResultSet result = stmt.executeQuery(query);

                //read result set, write data to Log
                if (result.next() == false) {
                    signupText.setVisibility(View.VISIBLE);
                } else {
                    // go to next activity
                    Log.e("JDBC", "success");
                }

                //read result set, write data to Log
                /*
                if (result.next() == false) {
                    signupText.setVisibility(View.VISIBLE);
                } else {
                    while (result.next()) {
                        Log.e("JDBC", "enter");
                        String id = result.getString("id");
                        String str = String.format("%d    %s", result.getInt("id"), result.getString("username"));
                        Log.e("JDBC", str);
                        Log.e("JDBC", "success connection");
                    }
                }*/

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


}
