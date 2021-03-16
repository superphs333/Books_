package com.remon.books;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class Activity_Detail_My_Book extends AppCompatActivity {

    /*
    뷰변수
     */
    TextView txt_title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__detail__my__book);

        /*
        뷰연결
         */
        txt_title = findViewById(R.id.txt_title);

    }
}
