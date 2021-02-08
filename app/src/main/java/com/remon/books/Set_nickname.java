package com.remon.books;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Set_nickname extends AppCompatActivity {

    Context context;
    Activity activity;
    EditText edit_nick;
    TextView txt_nick_info;
    Button btn_nick_chk;

    Function_Set function_set;

    // 닉네임 중복체크
    boolean nick_no_double = false;

    // login

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_nickname);

        /*
        뷰연결
         */
        context = getApplicationContext();
        activity = this;
        edit_nick = findViewById(R.id.edit_nick);
        txt_nick_info = findViewById(R.id.txt_nick_info);
        btn_nick_chk = findViewById(R.id.btn_nick_chk);

        // 함수 모음 객체
        function_set = new Function_Set();
        function_set.context = context; // context셋팅

        /*
        Intent로 부터 값 받아오기(login_value, profile_url)
         */
        String login_va;

        /*
        중복확인 후에,
        닉네임 입력칸(edit_nick)에 입력값이 있을 경우
        중복확인을 다시 해줘야 한다
         */
         edit_nick.addTextChangedListener(new TextWatcher() {

             @Override
             public void beforeTextChanged(CharSequence s, int start, int count, int after) {

             }

             @Override
             public void onTextChanged(CharSequence s, int start, int before, int count) {

             }

             @Override
             public void afterTextChanged(Editable s) {
                 if(nick_no_double==true){
                     nick_no_double = false;
                 } // end if
             } // end afterTextChanged
         }); // end edit_nick.addTextChangedListener



    }

    public void Check_Nick_Double(View view) {
        // 입력값
        String Nick = edit_nick.getText().toString();

        // 닉네임 적합성 판단
        if(!function_set.validate_Nick(Nick)){
            Toast.makeText(getApplicationContext()
                    , "닉네임이 적절하지 않습니다",Toast.LENGTH_LONG).show();
            return;
        }

        // 중복체크
        function_set.input = Nick;
        function_set.chk_double(new Function_Set.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                // 결과값이 unable인 경우 -> 함수 빠져나오기
                if(result.equals("unable")){
                    txt_nick_info.setText("사용 불가능한 닉네임입니다");
                    nick_no_double = false;
                    return;
                }

                // 중복확인 문구 변경해주기
                txt_nick_info.setText("사용 가능한 닉네임입니다");
                nick_no_double = true;
            }
        }, "nickname");

    } // end Check_Nick_Double

    public void sign_up(View view) {

    }
}
