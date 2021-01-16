package com.remon.books;

import com.android.volley.RequestQueue;

/*
Volley는 RequestQueue객체로 서버 연결을 제어하는데,
이 객체는 애플리케이션에서 하나만 만들어지도록 하기 위해
별도의 클래스에 static메서드로 선언
 */
public class AppHelper {
    public static RequestQueue requestQueue;
}
