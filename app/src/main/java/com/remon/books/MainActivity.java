package com.remon.books;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    /*
    뷰변수
     */
    EditText edit_email, edit_pw;
    CheckBox chk_autologin;
    Button btn_login;
    TextView txt_signup, txt_find_pw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d("실행", "=====MainActivity onCreate=====");


        /*
        뷰연결
         */
        edit_email = findViewById(R.id.edit_email); // 이메일 입력칸
        edit_pw = findViewById(R.id.edit_pw); // 비밀번호 입력칸
        chk_autologin = findViewById(R.id.chk_autologin); // 자동로그인 체크박스
        btn_login = findViewById(R.id.btn_login); // 로그인 버튼
        txt_signup = findViewById(R.id.txt_signup); // 회원가입 버튼
        txt_find_pw = findViewById(R.id.txt_find_pw); // 비밀번호 찾기 버튼




    }

    /*
    txt_signup 버튼클릭 => 회원가입 페이지(SignUp.class)로 이동
     */
    public void go_to_Signup_Page(View view) {
        Log.d("실행", "txt_signup(회원가입) 클릭");
        Intent intent = new Intent(this,SignUp.class);
        startActivity(intent);
    }
}
