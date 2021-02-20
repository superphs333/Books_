package com.remon.books;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.remon.books.Function.Function_Set;
import com.remon.books.Function.Function_SharedPreference;

import java.util.HashMap;
import java.util.Map;

public class Set_nickname extends AppCompatActivity {

    Context context;
    Activity activity;
    EditText edit_nick;
    TextView txt_nick_info;
    Button btn_nick_chk;

    Function_Set function_set;
    Function_SharedPreference fs;

    // 닉네임 중복체크
    boolean nick_no_double = false;

    // login 정보
    String login_value,profile_url;

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
        fs = new Function_SharedPreference(context);


        /*
        Intent로 부터 값 받아오기(login_value, profile_url)
         */
        login_value = getIntent().getStringExtra("login_value");
        profile_url = getIntent().getStringExtra("profile_url");

        /*
        어디로부터 왔는지 확인 -> 마이페이지 or 로그인
         */

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
                     txt_nick_info.setText("중복확인 문구");
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

    // 회원가입
    public void sign_up(View view) {

        // 만약, nick_no_double = false인 경우 toast하고 함수 빠져나오기)
        if(nick_no_double==false){
            Toast.makeText(getApplicationContext()
                    , "닉네임을 확인해 주세요",Toast.LENGTH_LONG).show();
            return;
        }

        // 닉네임
       final String nickname = edit_nick.getText().toString();

        // 웹페이지 실행하기
        String url = getString(R.string.server_url)+"signup_google_chk.php";

        StringRequest request = new StringRequest(
                Request.Method.POST,
                url,
                new com.android.volley.Response.Listener<String>() { // 정상 응답
                    @Override
                    public void onResponse(String response) {
                        Log.d("실행","response=>"+response);

                        // 성공인경우
                        if(response.equals("success")){
                            Toast.makeText(getApplicationContext()
                                    , "회원가입이 완료되었습니다",Toast.LENGTH_LONG).show();

                            // Shared에 회원 Unique_Value 저장
                            fs.setPreference("member","login_value",login_value);

                            // shared에 google로 로그인 했다는 것 저장
                            fs.setPreference("member","platform_type","google");

                            // 페이지 이동
                            Intent intent = new Intent(context,Main.class);
                            finish();
                            startActivity(intent);


                        }else{
                            Toast.makeText(getApplicationContext()
                                    , "죄송합니다. 오류가 발생하였습니다. 다시 시도해 주세요",Toast.LENGTH_LONG).show();
                        }

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
                params.put("login_value", login_value);
                params.put("profile_url", profile_url);
                params.put("nickname", nickname);


                return params;
            }
        };

        // 요청 객체를 만들었으니 이제 requestQueue 객체에 추가하면 됨.
        // Volley는 이전 결과를 캐싱하므로, 같은 결과가 있으면 그대로 보여줌
        // 하지만 아래 메소드를 false로 set하면 이전 결과가 있더라도 새로 요청해서 응답을 보여줌.
        request.setShouldCache(false);
        AppHelper.requestQueue = Volley.newRequestQueue(context);
        AppHelper.requestQueue.add(request);

    } // end sign_up
}
