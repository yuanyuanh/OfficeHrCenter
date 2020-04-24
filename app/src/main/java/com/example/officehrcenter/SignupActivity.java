package com.example.officehrcenter;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;

public class SignupActivity extends AppCompatActivity {

    private EditText usernameEdit;
    private EditText passwordEdit;
    private EditText nameEdit;
    private RadioGroup occupationGroup;
    private Button doneButt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        usernameEdit = (EditText) findViewById(R.id.usernameEditText);
        passwordEdit = (EditText) findViewById(R.id.passwordEdit);
        nameEdit = (EditText) findViewById(R.id.nameEdit);
        occupationGroup = (RadioGroup)findViewById(R.id.occupationRadio);
        doneButt = (Button)findViewById(R.id.doneButt);
    }
}
