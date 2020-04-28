package com.example.officehrcenter.activities;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.officehrcenter.R;

public class WelcomeActivity extends Activity implements Animation.AnimationListener {

    private RelativeLayout layout;
    private TextView welcomeText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        layout = (RelativeLayout)findViewById(R.id.layout);
        welcomeText = (TextView)findViewById(R.id.welcomeText);

        Animation animation = AnimationUtils.loadAnimation(this, R.anim.animation);
        animation.setAnimationListener(this);

        layout.startAnimation(animation);
    }


    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {
        Intent i = new Intent(this, LoginActivity.class);
        startActivity(i);
        finish();
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }
}
