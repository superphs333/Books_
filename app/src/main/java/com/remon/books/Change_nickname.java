package com.remon.books;

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

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.remon.books.Function.Function_Set;
import com.remon.books.Function.Function_SharedPreference;

import java.util.HashMap;
import java.util.Map;


public class Change_nickname extends AppCompatActivity {

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
    String login_value,nickname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_nickname);

        Log.d("실행", "Change_nickname onCreate");


        /*
        뷰연결
         */
        context = getApplicationContext();
        activity = this;
        edit_nick = findViewById(R.id.edit_nick);
        txt_nick_info = findViewById(R.id.txt_nick_info);
        btn_nick_chk = findViewById(R.id.btn_nick_chk);

        // 함수 모음 객체
        function_set = new Function_Set(context,activity);
        function_set.context = context; // context셋팅
        fs = new Function_SharedPreference(context);


        /*
        nickname, login_value 셋팅
         */
        login_value = fs.getPreferenceString("member","login_value");
        nickname = fs.getPreferenceString("member","nickname");
        Log.d("실행", "login_value="+login_value+", nickname="+nickname);

        // 기존 닉네임 셋팅
        edit_nick.setText(nickname);




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
                     if(edit_nick.getText().toString().equals(nickname)){
                         txt_nick_info.setText("현재 닉네임과 동일한 닉네임");
                     }else{
                         txt_nick_info.setText("중복확인 문구");
                     }
                     nick_no_double = false;
                 } // end if


             } // end afterTextChanged
         }); // end edit_nick.addTextChangedListener



    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Log.d("실행", "Change_nickname onDestroy");

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

        // 이전 닉네임과 같을때
        if(Nick.equals(nickname)){
            Toast.makeText(getApplicationContext(), "현재 닉네임과 같은 닉네임입니다",Toast.LENGTH_LONG).show();
            txt_nick_info.setText("현재 닉네임과 동일한 닉네임");
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
    public void change(View view) {



        // 만약, nick_no_double = false인 경우 toast하고 함수 빠져나오기)
        if(nick_no_double==false){
            Toast.makeText(getApplicationContext()
                    , "닉네임을 확인해 주세요",Toast.LENGTH_LONG).show();
            return;
        }



        // 닉네임
       final String change_nickname = edit_nick.getText().toString();

        // 웹페이지 실행하기
        String url = getString(R.string.server_url)+"change_nickname.php";

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
                                    , "닉네임 변경이 완료되었습니다",Toast.LENGTH_LONG).show();

                            // shared에 변경된 닉네임 저장
                            fs.setPreference("member","nickname",change_nickname);

                            // 구분 : 구글 로그인 or 마이페이지
                            if(getIntent().getStringExtra("sort").equals("from_mypage")){

                                fs.setPreference("member","platform_type","normal");

                                finish();
                            }else{
                                // shared에 google로 로그인 했다는 것 저장
                                fs.setPreference("member","platform_type","google");

                                // 액티비티 종료
                                finish();
                            }

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
                params.put("change_nickname", change_nickname);

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
