package com.remon.books;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class Find_Pw extends AppCompatActivity {

    /*
    뷰변수
     */
    Context context;
    EditText edit_email, edit_chk_char;
    Button btn_send, btn_confirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find__pw);

        /*
        뷰연결결
        */
        // DataBinding 알아보기(https://gun0912.tistory.com/71)


    }


    /* 인증코드 전송(btn_send)클릭
    edit_email에 입력된 내용 확인
        -> 이메일 형식인지 확인
            -> 존재하는 이메일인지 확인
                -> 해당 이메일로 인증번호 전송
     */
    public void send_email(View view) {
    }

    // 확인(btn_confirm) 클릭
        // 입력된 인증문자가 맞는지 확인한다
            // 맞다면 -> 비밀번호 변경 액티비티(Change_PW)로 이동
            // 틀리다면 -> TOAST
    public void validate_string_confirm(View view) {
    }


}
