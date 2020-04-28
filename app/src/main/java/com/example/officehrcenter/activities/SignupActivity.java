package com.example.officehrcenter.activities;

/** This is the signup activity where users can create new accounts.
 * @version 1.0
 */

import androidx.appcompat.app.AppCompatActivity;

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

import com.example.officehrcenter.R;

public class SignupActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener, View.OnClickListener {

    // widgets
    private EditText userEdit;
    private EditText passwordEdit1;
    private EditText passwordEdit2;
    private EditText nameEdit;
    private RadioGroup occupationGroup;
    private EditText officeEdit;
    private EditText phoneEdit;
    private EditText emailEdit;

    private TextView officeText;
    private TextView phoneText;
    private TextView emailText;

    private Button doneButt;

    private TextView userTip;
    private TextView passwordTip1;
    private TextView passwordTip2;
    private TextView nameTip;
    private TextView occupationTip;
    private TextView officeTip;

    private int checkId;
    private boolean requiredFilled;

    private Thread t = null;
    private static final String TAG = "Sign Up";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Set tips and variables for professors as invisible
        userEdit = (EditText) findViewById(R.id.userEdit);
        passwordEdit1 = (EditText) findViewById(R.id.passwordEdit1);
        passwordEdit2 = (EditText) findViewById(R.id.passwordEdit2);
        nameEdit = (EditText) findViewById(R.id.nameEdit);
        occupationGroup = (RadioGroup)findViewById(R.id.occupationRadio);
        occupationGroup.setOnCheckedChangeListener(this);
        officeEdit = (EditText)findViewById(R.id.officeEdit);
        officeEdit.setVisibility(View.INVISIBLE);
        emailEdit = (EditText)findViewById(R.id.emailEdit);
        emailEdit.setVisibility(View.INVISIBLE);
        phoneEdit = (EditText)findViewById(R.id.phoneEdit);
        phoneEdit.setVisibility(View.INVISIBLE);

        officeTip.setVisibility(View.INVISIBLE);
        officeText = (TextView)findViewById(R.id.officeText);
        officeText.setVisibility(View.INVISIBLE);
        phoneText = (TextView)findViewById(R.id.phoneText);
        phoneText.setVisibility(View.INVISIBLE);
        emailText = (TextView)findViewById(R.id.emailText);
        emailText.setVisibility(View.INVISIBLE);

        userTip = (TextView)findViewById(R.id.userTip1);
        userTip.setVisibility(View.INVISIBLE);
        passwordTip1 = (TextView)findViewById(R.id.passwordTip1);
        passwordTip1.setVisibility(View.INVISIBLE);
        passwordTip2 = (TextView)findViewById(R.id.passwordTip2);
        passwordTip2.setVisibility(View.INVISIBLE);
        nameTip = (TextView)findViewById(R.id.nameTip);
        nameTip.setVisibility(View.INVISIBLE);
        occupationTip = (TextView)findViewById(R.id.occupationTIp);
        occupationTip.setVisibility(View.INVISIBLE);
        officeTip = (TextView)findViewById(R.id.officeTip);
        officeTip.setVisibility(View.INVISIBLE);

        doneButt = (Button)findViewById(R.id.doneButt);
        doneButt.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        try{
            signUp(v);
        }catch(Exception e){
            Toast.makeText(this, "Sign up failed. Please try again.", Toast.LENGTH_LONG).show();
        }
    }

    /* If the new user is a professor, collect other required information.
     * If a student, no need to collect those information.
     */
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
        requiredFilled = true;
        //check all field is filled
        if (userEdit.getText().toString().isEmpty()) {
            userTip.setVisibility(View.VISIBLE);
            requiredFilled = false;
        }
        if (passwordEdit1.getText().toString().isEmpty()) {
            passwordTip1.setVisibility(View.VISIBLE);
            requiredFilled = false;
        }
        if (!passwordEdit1.getText().toString().equals(passwordEdit2.getText().toString())) {
            passwordTip2.setVisibility(View.VISIBLE);
            requiredFilled = false;
        }
        if (nameEdit.getText().toString().isEmpty()) {
            nameTip.setVisibility(View.VISIBLE);
            requiredFilled = false;
        }
        if (occupationGroup.getCheckedRadioButtonId() == -1) {
            occupationTip.setVisibility(View.VISIBLE);
            requiredFilled = false;
        } else {
            if (checkId == R.id.rbProf) {
                if (officeEdit.getText().toString().isEmpty()) {
                    officeTip.setVisibility(View.VISIBLE);
                    requiredFilled = false;
                }
            }
        }

        if (!requiredFilled) {
            Log.i(TAG, "All required filled, start to update the database");
            t = new Thread(background);
            t.start();
        }

    }

    private Handler signUpHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    Log.i(TAG, "Sign up successfully");
                    finish();
                    break;
                case 1:
                    Log.i(TAG, "Redundant user");
                    userTip.setText(getString(R.string.userTip2));
                    userTip.setVisibility(View.VISIBLE);
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
            updateStm += " values(\'" + userEdit.getText().toString() +
                    "\', \'" + passwordEdit1.getText().toString() +
                    "\', \'" + nameEdit.getText().toString() +
                    "\', \'";
            if (occupationGroup.getCheckedRadioButtonId() == R.id.rbProf) {
                updateStm += "professor" +
                        "\', \'" + officeEdit.getText().toString() +
                        "\', \'" + emailEdit.getText().toString() +
                        "\', \'" + phoneEdit.getText().toString() + "\');";
            } else {
                updateStm += "student\');";
            }
            int count = dbConn.update(updateStm);

            /* As the column for user name is set as unique in the database,
             * the error for duplicate user name will be caught when updating.
             */
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
