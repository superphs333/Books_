package com.remon.books;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class Activity_Detail_My_Book extends AppCompatActivity {

    /*
    뷰변수
     */
    TextView txt_title;

    // unique_book_value
    String unique_book_value;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__detail__my__book);

        /*
        데이터 불러오기
         */
        unique_book_value = getIntent().getStringExtra("unique_book_value");
        Log.d("실행", "unique_book_value="+unique_book_value);



        /*
        뷰연결
         */
        txt_title = findViewById(R.id.txt_title);



    }
}
