package com.remon.books;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class Activity_Underline_Picture extends AppCompatActivity {

    // 이전 액티비티에서 받아온 이미지 주소
    String img_url;
    // 이전 액티비티에서 받아온 이미지 위치
    String position;

    // 뷰변수
    MyCanvas my_canvas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__underline__picture);

        img_url = getIntent().getStringExtra("img_url");
        Log.d("실행", "(in Activity_Underline_Picture onCreate)img_url="+img_url);
        position = getIntent().getStringExtra("position");
        Log.d("실행", "(in Activity_Underline_Picture onCreate)position="+position);


        my_canvas = (MyCanvas) findViewById(R.id.my_canvas);
        my_canvas.m_filename = img_url;
        my_canvas.context = getApplicationContext();


    }

    public void onClick(View v){
        Log.d("실행", "onClick");

        switch (v.getId()){
            case R.id.image_back: // 뒤로가기
                finish();
                break;
            case R.id.btn_ok: // 이미지 경로 전송하고, 액티비티 끝내기
                Intent intent = new Intent();
                intent.putExtra("result", my_canvas.Save_Send());
                intent.putExtra("position", position);
                setResult(RESULT_OK, intent);
                finish();
                break;
        }
    } // end onClick
}
