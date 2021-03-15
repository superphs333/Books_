package com.remon.books;

import androidx.appcompat.app.AppCompatActivity;

import android.net.http.SslError;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class Activity_Book_URL extends AppCompatActivity {

    // 뷰변수
    WebView webView;

    // url
    String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__book__url);

        // 뷰연결
        webView = findViewById(R.id.webView);


        // 받은값(url)
        url = getIntent().getStringExtra("url");
        Log.d("실행","url="+url);


        // webView 초기화
        init_webView(webView,url);




    }

    public class SslWebViewConnect extends WebViewClient {
        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            handler.proceed(); // SSL 에러가 발생해도 계속 진행!
        }
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;//응용프로그램이 직접 url를 처리함
        }
    }

    public void init_webView(WebView webView,String url) {

        // html을 안드로이드 웹뷰에 모두 보이게 하기
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);

        // JavaScript 허용
        webView.getSettings().setJavaScriptEnabled(true);
        // JavaScript의 window.open 허용
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        // 로컬저장소에서 허용할지 여부
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setSupportMultipleWindows(true);
        // JavaScript이벤트에 대응할 함수를 정의 한 클래스를 붙여줌

        // web client 를 chrome 으로 설정
        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new SslWebViewConnect());
        // webview url load. php 파일 주소
        webView.loadUrl(url);
    }
}
