package com.example.officehrcenter;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SignupActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener{

    private EditText usernameEdit;
    private EditText passwordEdit;
    private EditText nameEdit;
    private RadioGroup occupationGroup;
    private TextView officeText;
    private EditText officeEdit;
    private Button doneButt;
    private TextView usernameTip;
    private TextView passwordTip;
    private TextView nameTip;
    private TextView occupationTip;
    private TextView officeTip;
    private TextView usernameInvalidTip;
    private int checkId;

    private Thread t = null;
    private static final String SIGNUPMSG = "signup";
    private final String success = "SUCCESS";
    private final String fail = "FAIL";
    private final String taken = "username already exists";

    private Toast toast;
    private Statement stmt = null;
    private Connection con = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        usernameEdit = (EditText) findViewById(R.id.usernameEditText);
        passwordEdit = (EditText) findViewById(R.id.passwordEdit);
        nameEdit = (EditText) findViewById(R.id.nameEdit);
        occupationGroup = (RadioGroup)findViewById(R.id.occupationRadio);
        occupationGroup.setOnCheckedChangeListener(this);
        doneButt = (Button)findViewById(R.id.doneButt);
        officeText = (TextView)findViewById(R.id.officeText);
        officeEdit = (EditText)findViewById(R.id.officeEdit);
        usernameTip = (TextView)findViewById(R.id.usernameTip);
        passwordTip = (TextView)findViewById(R.id.passwordTip);
        nameTip = (TextView)findViewById(R.id.nameTip);
        occupationTip = (TextView)findViewById(R.id.occupationTIp);
        officeTip = (TextView)findViewById(R.id.officeTip);

        toast.makeText(this, "Sign up failed. Please try again.", Toast.LENGTH_LONG);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        checkId = checkedId;
        if (checkedId == R.id.rbProf) {
            officeEdit.setVisibility(View.VISIBLE);
            officeText.setVisibility(View.VISIBLE);
        } else {
            officeText.setVisibility(View.INVISIBLE);
            officeEdit.setVisibility(View.INVISIBLE);
        }
    }

    public void signUp(View view) {
        int filled = 0;
        //check all field is filled
        if (usernameEdit.getText().toString().isEmpty()) {
            usernameTip.setVisibility(View.VISIBLE);
        } else {
            filled += 1;
        }
        if (passwordEdit.getText().toString().isEmpty()) {
            passwordTip.setVisibility(View.VISIBLE);
        } else {
            filled += 1;
        }
        if (nameEdit.getText().toString().isEmpty()) {
            nameTip.setVisibility(View.VISIBLE);
        } else {
            filled += 1;
        }
        if (occupationGroup.getCheckedRadioButtonId() == -1) {
            occupationTip.setVisibility(View.VISIBLE);
        } else {
            filled += 1;
            if (checkId == R.id.rbProf) {
                if (officeEdit.getText().toString().isEmpty()) {
                    officeTip.setVisibility(View.VISIBLE);
                } else {
                    filled +=1;
                }
            }
        }

        if ((checkId == R.id.rbProf && filled == 5 ) ||
                (checkId == R.id.rbStudent && filled == 4)) {
            t = new Thread(background);
            t.start();
        }

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

            String query = "select * from users where username=\'" + usernameEdit.getText().toString() + "\';";
            Log.e("JDBC", query);
            try {
                // execute SQL commands to create table, insert data, select contents
                ResultSet result = stmt.executeQuery(query);

                //read result set, write data to Log

                if (result.next() == false) {
                    Log.e("JDBC", "No users found");
                    String updateStm = "insert into users (username, password, name, occupation";
                    if (checkId == R.id.rbStudent) {
                        updateStm += ")";
                    } else {
                        updateStm += ", office)";
                    }
                    updateStm += " values(\'" + usernameEdit.getText().toString() +
                            "\', \'" + passwordEdit.getText().toString() + "\', \'" + nameEdit.getText().toString() +
                            "\', \'";
                    if (occupationGroup.getCheckedRadioButtonId() == R.id.rbProf) {
                        updateStm += "professor" + "\', \'" + officeEdit.getText().toString() + "\');";
                    } else {
                        updateStm += "student\');";
                    }
                    Log.e("JDBC", updateStm);
                    int sqlStatus = stmt.executeUpdate(updateStm);
                    //check update status
                    if (sqlStatus > 0) {
                        Message msg = handler.obtainMessage();
                        Bundle bundle = new Bundle();
                        bundle.putString(SIGNUPMSG, "SUCCESS");
                        msg.setData(bundle);
                        handler.sendMessage(msg);
                    } else {
                        Message msg = handler.obtainMessage();
                        Bundle bundle = new Bundle();
                        bundle.putString(SIGNUPMSG, "failed.");
                        msg.setData(bundle);
                        handler.sendMessage(msg);
                    }
                } else {
                    Log.e("JDBC", "success connection");
                    Message msg = handler.obtainMessage();
                    Bundle bundle = new Bundle();
                    bundle.putString(SIGNUPMSG, "username existed");
                    msg.setData(bundle);
                    handler.sendMessage(msg);
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
        @SuppressLint("HandlerLeak")
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            String str = bundle.getString(SIGNUPMSG);
            switch (str) {
                case success:
                    Log.e("JDBC", "called");
                    finish();
                    break;
                case fail:
                    toast.show();
                    break;
                case taken:
                    usernameInvalidTip.setVisibility(View.VISIBLE);
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
