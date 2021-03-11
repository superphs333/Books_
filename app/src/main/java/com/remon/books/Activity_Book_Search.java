package com.remon.books;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.HashMap;
import java.util.Map;

public class Activity_Book_Search extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__book__search);



        String url="https://dapi.kakao.com"+"/v3/search/book?query="+"9788996991342"+"&page="+1+"&size="+1+"&target=isbn";
        StringRequest request = new StringRequest(
                Request.Method.GET, // GET 방식으로 요청
                url,
                new Response.Listener<String>() {
                    //응답이 제대로 되었을 때
                    @Override
                    public void onResponse(String response) {
                        Log.i("실행","응답 ->" + response);

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //TODO 에러응답을 받았을 때
                         Log.d("실행", "error=>"+error.getMessage());

                    }
                }
        ) {
            @Override
            public Map<String,String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap();
                params.put("Authorization","KakaoAK e7175a312c372da8bf3b88df5fc3a144"); //카카오 개발자 홈페이지에서 본인의 앱의 Authorization key를 발급 받고 집어넣기
                return params;
            }
        };

        request.setShouldCache(false);
        AppHelper.requestQueue.add(request);

    }



}
