package com.example.officehrcenter.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.officehrcenter.R;

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
    private EditText phoneEdit;
    private EditText emailEdit;
    private TextView phoneText;
    private TextView emailText;
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
        emailEdit = (EditText)findViewById(R.id.emailEdit);
        phoneEdit = (EditText)findViewById(R.id.phoneEdit);
        emailText = (TextView)findViewById(R.id.emailText);
        phoneText = (TextView)findViewById(R.id.phoneText);
        usernameInvalidTip = (TextView)findViewById(R.id.reuernameTip);


        toast.makeText(this, "Sign up failed. Please try again.", Toast.LENGTH_LONG);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        checkId = checkedId;
        if (checkedId == R.id.rbProf) {
            officeEdit.setVisibility(View.VISIBLE);
            officeText.setVisibility(View.VISIBLE);
            emailEdit.setVisibility(View.VISIBLE);
            phoneEdit.setVisibility(View.VISIBLE);
            emailText.setVisibility(View.VISIBLE);
            phoneText.setVisibility(View.VISIBLE);
        } else {
            officeText.setVisibility(View.INVISIBLE);
            officeEdit.setVisibility(View.INVISIBLE);
            emailEdit.setVisibility(View.INVISIBLE);
            phoneEdit.setVisibility(View.INVISIBLE);
            emailText.setVisibility(View.INVISIBLE);
            phoneText.setVisibility(View.INVISIBLE);
            officeTip.setVisibility(View.INVISIBLE);
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

    private Handler signUpHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    Log.e("JDBC", "Sign up succeeded");
                    finish();
                    break;
                case 1:
                    usernameInvalidTip.setVisibility(View.VISIBLE);
            }
        }
    };

    private Runnable background = new Runnable() {
        public void run() {
            JDBCHelper dbConn = new JDBCHelper();
            dbConn.connenctDB();
            // construct query based on occupation
            String updateStm = "insert into users (username, password, name, occupation";
            if (checkId == R.id.rbStudent) {
                updateStm += ")";
            } else {
                updateStm += ", office, email, phone)";
            }
            updateStm += " values(\'" + usernameEdit.getText().toString() +
                    "\', \'" + passwordEdit.getText().toString() + "\', \'" + nameEdit.getText().toString() +
                    "\', \'";
            if (occupationGroup.getCheckedRadioButtonId() == R.id.rbProf) {
                updateStm += "professor" + "\', \'" + officeEdit.getText().toString() + "\', \'" + emailEdit.getText().toString()
                        + "\', \'" + phoneEdit.getText().toString() + "\');";
            } else {
                updateStm += "student\');";
            }
            int count = dbConn.update(updateStm);
            if (count > 0) {
                signUpHandler.sendEmptyMessage(0);
            } else {
                signUpHandler.sendEmptyMessage(1);
            }
            dbConn.disConnect();
            t = null;

        }
    };



    @Override
    protected void onDestroy() {
        super.onDestroy();
        t = null;
    }
}
