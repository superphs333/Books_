package com.remon.books;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.remon.books.Function.Function_Set;

public class Change_Pw extends AppCompatActivity {

    /*
    뷰변수
     */
    Context context;
    EditText edit_pw, edit_pw_chk;
    TextView txt_pw_info, txt_chk_info;
    Button btn_chk;

    // 함수모음객체
    Function_Set fs;

    String email;

    // 비밀번호 체크용
    boolean pw_equal;

    // 체크 문구
    String pw_ok = "사용가능한 비밀번호 입니다";
    String pw_no = "대소문자 구분 숫자 특수문자  조합 9 ~ 12 자리를 입력해주세요";
    String pw_chk_ok = "비밀번호가 일치합니다";
    String pw_chk_no = "비밀번호와 비밀번호 확인은 동일해야 합니다";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change__pw);

        /*
        뷰연결
         */
        context = getApplicationContext();
        edit_pw = findViewById(R.id.edit_pw);
        edit_pw_chk = findViewById(R.id.edit_pw_chk);
        txt_pw_info = findViewById(R.id.txt_pw_info);
        txt_chk_info = findViewById(R.id.txt_chk_info);
        btn_chk = findViewById(R.id.btn_chk);

        // 함수객체
        fs = new Function_Set();
        fs.context = context;


        // 비밀번호 찾기 액티비티에서 온 경우
        email = getIntent().getStringExtra("email");
        Log.d("실행", "email="+email);

        /*
        비밀번호를 입력 할 때마다 비밀번호 정규식 체크
         */
        edit_pw.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                // 입력값
                String pw = edit_pw.getText().toString();

                /*
                비밀번호 정규식에 일치하는지 확인하기
                 */
                Boolean check = fs.validate_Pw(pw);
                if(check==true){// 적합할때
                    txt_pw_info.setText(pw_ok);
                }else{// 적합하지 않을 때
                    txt_pw_info.setText(pw_no);
                }


                /*
                비밀번호=비밀번호 확인 일치 여부
                 */
                String pw_chk = edit_pw_chk.getText().toString();
                boolean check2 = fs.check_pw_equal(pw,pw_chk);


            } // end afterTextChanged
        }); // end edit_pw.addTextChangedListener

        /*
        비밀번호=비밀번호 확인 체크하기
         */
        edit_pw_chk.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                // 입력값
                String pw = edit_pw.getText().toString(); // 비밀번호 값
                String pw_double = edit_pw_chk.getText().toString(); // 비밀번호 중복 체크 값

                // 일치 확인
                boolean check = fs.check_pw_equal(pw,pw_double);
                if(check==true){ // 일치
                    txt_chk_info.setText(pw_chk_ok);
                    pw_equal = true;
                }else{ // 일치하지 않음
                    txt_chk_info.setText(pw_chk_no);
                    pw_equal = false;
                }
            }
        }); // end edit_pw_chk.addTextChangedListener





    } // end onCreate
}
