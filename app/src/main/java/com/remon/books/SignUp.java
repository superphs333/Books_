package com.remon.books;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUp extends AppCompatActivity {

    /*
    뷰변수
     */
    Context context;
    EditText edit_email, edit_email_chk, edit_pw, edit_pw_double, edit_nick;
    TextView txt_pw_info, txt_nick_info;
    Button btn_email_chk, btn_camera, btn_gallery, btn_picture_delete, btn_sign_up;
    ImageView img_profile;

    // 함수모음 객체
    Function_Set function_set;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        /*
        뷰연결
         */
        context = getApplicationContext();
        edit_email = findViewById(R.id.edit_email);
        edit_email_chk = findViewById(R.id.edit_email_chk);
        edit_pw = findViewById(R.id.edit_pw);
        edit_pw_double = findViewById(R.id.edit_pw_double);
        edit_nick = findViewById(R.id.edit_nick);
        txt_pw_info = findViewById(R.id.txt_pw_info);
        txt_nick_info = findViewById(R.id.txt_nick_info);
        btn_email_chk = findViewById(R.id.btn_email_chk);
        btn_camera = findViewById(R.id.btn_camera);
        btn_gallery = findViewById(R.id.btn_gallery);
        btn_picture_delete = findViewById(R.id.btn_picture_delete);
        btn_sign_up = findViewById(R.id.btn_sign_up);
        img_profile = findViewById(R.id.img_profile);

        // 함수모음 (닉네임, 이메일 중복체크 위해서)
        function_set = new Function_Set();



    }

    // 입력받은 이메일로 인증문자를 전송한다
    public void send_email(View view) {

        final String email = edit_email.getText().toString();

        // 이메일 형식인지 확인한다
        if(!is_Email(email)){ // 이메일 형식이 아닌 경우 토스트로 알림 -> 함수종료

            Toast
                    .makeText
                            (getApplicationContext()
                                    , "올바른 이메일을 입력해주세요",Toast.LENGTH_SHORT).show();

            return;
        } // end if(!is_Email(email))

        /*
        이메일 중복 여부 확인
        -> 이메일을 서버로 전송
         */
        // Function_Set객체의 context와 input변수 셋팅
        function_set.context = context;
        function_set.input = email;
        function_set.chk_double_email(new Function_Set.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                 Log.d("실행", "밖에서 result="+result);
            }
        });



    } // end send_email

    // 이메일 유효성 확인
    public boolean is_Email(String email){
        boolean return_Value = false;
        String regex = "^[_a-zA-Z0-9-\\.]+@[\\.a-zA-Z0-9-]+\\.[a-zA-Z]+$";
        Pattern p = Pattern.compile(regex);
            // Pattern.compile(String regex)
                // = 주어진 정규표현식으로부터 패턴을 만든다
        // 패턴과 입력받은 값이 일치하는지 확인
        Matcher m = p.matcher(email);
        if(m.matches()){ // 일치하는 경우에만 true
            return_Value = true;
        }
        return return_Value;
    } // end isEmail


}
