package com.remon.books;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

import static java.lang.Thread.sleep;

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

        Log.d("실행", "===(Activity_Underline_Picture)onCreate===");

        img_url = getIntent().getStringExtra("img_url");
        Log.d("실행", "(in Activity_Underline_Picture onCreate)img_url="+img_url);
        position = getIntent().getStringExtra("position");
        Log.d("실행", "(in Activity_Underline_Picture onCreate)position="+position);

        // 뷰연결
        my_canvas = (MyCanvas) findViewById(R.id.my_canvas);
        my_canvas.m_filename = img_url;

        // 서버에서 온건지, 사용자의 기기에서 온 이미지인지 분기
//        if(img_url.substring(0,4).equals("http")){
//            Glide.with(getApplicationContext())
//                    .asBitmap()
//                    .load(img_url)
//                    .into(new CustomTarget<Bitmap>(){
//                        @Override
//                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
//                            Log.d("실행", "width="+resource.getWidth());
//                            Log.d("실행", "width="+resource.getHeight());
//
//                            // 뷰연결
//                            my_canvas = (MyCanvas) findViewById(R.id.my_canvas);
//                            my_canvas.bitmap = resource;
//
//                        }
//
//                        @Override
//                        public void onLoadCleared(@Nullable Drawable placeholder) {
//
//                        }
//                    });
//        }else{
//            // 뷰연결
//            my_canvas = (MyCanvas) findViewById(R.id.my_canvas);
//            my_canvas.m_filename = img_url;
//
//        }






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
            case R.id.btn_reset: // 펜 모두 지우기
                my_canvas.Eraser();
        }
    } // end onClick
}
