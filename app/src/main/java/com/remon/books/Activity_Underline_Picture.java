package com.remon.books;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

public class Activity_Underline_Picture extends AppCompatActivity {

    // 이전 액티비티에서 받아온 이미지 주소
    String img_url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__underline__picture);

        img_url = getIntent().getStringExtra("img_url");
        Log.d("실행", "img_url="+img_url);

    }
}
