package com.remon.books;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class Activity_Underline_Picture extends AppCompatActivity {

    // 이전 액티비티에서 받아온 이미지 주소
    String img_url;

    // 뷰변수
    MyCanvas my_canvas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__underline__picture);

        img_url = getIntent().getStringExtra("img_url");
        Log.d("실행", "img_url="+img_url);

        my_canvas = (MyCanvas) findViewById(R.id.my_canvas);
        my_canvas.m_filename = img_url;


    }

    // 뒤로가기 버튼
    public void Back(View view) {
        finish();
    }
}
