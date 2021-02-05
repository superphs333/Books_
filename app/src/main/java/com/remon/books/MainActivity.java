package com.remon.books;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    /*
    뷰변수
     */
    Context context;
    Activity activity;
    EditText edit_email, edit_pw;
    CheckBox chk_autologin;
    Button btn_login;
    TextView txt_signup, txt_find_pw;

    // 함수모음 객체
    Function_Set function_set;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d("실행", "=====MainActivity onCreate=====");


        /*
        뷰연결
         */
        context = getApplicationContext();
        activity = MainActivity.this;
        edit_email = findViewById(R.id.edit_email); // 이메일 입력칸
        edit_pw = findViewById(R.id.edit_pw); // 비밀번호 입력칸
        chk_autologin = findViewById(R.id.chk_autologin); // 자동로그인 체크박스
        btn_login = findViewById(R.id.btn_login); // 로그인 버튼
        txt_signup = findViewById(R.id.txt_signup); // 회원가입 버튼
        txt_find_pw = findViewById(R.id.txt_find_pw); // 비밀번호 찾기 버튼

        // 함수모음
        function_set = new Function_Set();
        function_set.context = context; // context셋팅
    }

    /*
    txt_signup 버튼클릭 => 회원가입 페이지(SignUp.class)로 이동
     */
    public void go_to_Signup_Page(View view) {
        Log.d("실행", "txt_signup(회원가입) 클릭");
        Intent intent = new Intent(this,SignUp.class);
        startActivity(intent);
    }

    /*
    btn_login(로그인)버튼 클릭 => 아이디, 비밀번호 확인 후
    - 맞으면 : 다음 페이지로 이동
    - 틀리면 : 다시 한 번 확인해주세요 toast
     */
    public void login(View view) {
        final String email = edit_email.getText().toString();

        // 이메일 유효성 확인
        if(!function_set.is_Email(email)){
            // 이메일 유효성 = false
            Toast.makeText(getApplicationContext()
                    , "이메일 형식을 확인해주세요",Toast.LENGTH_LONG).show();
            return;
        }

        // 비밀번호 확인(비밀번호 정규식에 맞는지 확인)
        if(!function_set.validate_Pw(edit_pw.getText().toString())){
            Toast.makeText(getApplicationContext()
                    , "비밀번호를 확인해주세요",Toast.LENGTH_LONG).show();
            return;
        }


        final String pw = edit_pw.getText().toString();

        // 웹페이지 실행하기
        String url = getString(R.string.server_url)+"login.php";

        StringRequest request = new StringRequest(
                Request.Method.POST,
                url,
                new com.android.volley.Response.Listener<String>() { // 정상 응답
                    @Override
                    public void onResponse(String response) {
                        Log.d("실행","response=>"+response);

                    }
                },
                new com.android.volley.Response.ErrorListener() { // 에러 발생
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("실행","error=>"+error.getMessage());
                    }
                }

        ){ // Post 방식으로 body에 요청 파라미터를 넣어 전달하고 싶을 경우
            // 만약 헤더를 한 줄 추가하고 싶다면 getHeaders() override
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("email", email);
                params.put("pw", pw);
                return params;
            }
        };

        // 요청 객체를 만들었으니 이제 requestQueue 객체에 추가하면 됨.
        // Volley는 이전 결과를 캐싱하므로, 같은 결과가 있으면 그대로 보여줌
        // 하지만 아래 메소드를 false로 set하면 이전 결과가 있더라도 새로 요청해서 응답을 보여줌.
        request.setShouldCache(false);
        AppHelper.requestQueue = Volley.newRequestQueue(context);
        AppHelper.requestQueue.add(request);

    } // end login
}
