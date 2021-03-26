package com.remon.books;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

public class Activity_Management_Comment extends AppCompatActivity {

    String login_value = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__management__comment);

        login_value = getIntent().getStringExtra(getString(R.string.login_value));
        Log.d("실행", "login_value="+login_value);

    }
}
