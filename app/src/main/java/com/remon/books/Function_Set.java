package com.remon.books;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class Function_Set {

    /*
    변수
     */
    String input;
    Context context;

    /*
    생성자1
     */
    public Function_Set(){}


    /*
    interface
     */
    // volley callback용
    public interface VolleyCallback{
        void onSuccess(String result);
    }


    /*
    함수
     */
    // 이메일/닉네임 중복확인
    public void chk_double(final VolleyCallback callback, final String sort){

        // 결과값 리턴 -> able or unable
        final String[] result = {"unable"};

        // 웹페이지 실행하기
        String url = context.getString(R.string.server_url)+"double_chk.php";

        // 요청생성
            // 제공된 url에서 문자열 response를 요청한다
        StringRequest request = new StringRequest(
                Request.Method.POST,
                url,
                new com.android.volley.Response.Listener<String>() { // 정상 응답
                    @Override
                    public void onResponse(String response) {
                        Log.d("실행","response=>"+response);

                        if(response.equals("able")){ // 중복이메일 x => 인증문자 전송

                            result[0] = "able";

                        }else if(response.equals("unable")){ // 중복 이메일 존재
                            Toast
                                    .makeText(context, "중복된 이메일이 존재합니다",Toast.LENGTH_LONG).show();

                           result[0] = "unable";

                        } // end if

                        // 콜백에 결과값 전송
                        callback.onSuccess(result[0]);
                    } // end onResponse
                },
                new com.android.volley.Response.ErrorListener() { // 에러 발생
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("실행","error=>"+error.getMessage());

                        Toast.makeText(context
                                , "죄송합니다. 다시 시도 해주세요",Toast.LENGTH_LONG)
                                .show();
                    }
                }

        ){ // Post 방식으로 body에 요청 파라미터를 넣어 전달하고 싶을 경우
            // 만약 헤더를 한 줄 추가하고 싶다면 getHeaders() override
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("sort", sort); // 분류
                params.put("input", input); // 입력 된 값 전송
                return params;
            }
        };

        // 요청 객체를 만들었으니 이제 requestQueue 객체에 추가하면 됨.
        // Volley는 이전 결과를 캐싱하므로, 같은 결과가 있으면 그대로 보여줌
        // 하지만 아래 메소드를 false로 set하면 이전 결과가 있더라도 새로 요청해서 응답을 보여줌.
        request.setShouldCache(false);
        AppHelper.requestQueue = Volley.newRequestQueue(context);
        AppHelper.requestQueue.add(request);
    }

    // 비밀번호 정규식 확인
    public boolean validate_Pw(String pw){
        // 대소문자 구분 숫자 특수문자  조합 9 ~ 12 자리

        // 정규식
        String pw_Pattern
                = "^(?=.*[A-Za-z])(?=.*[0-9])(?=.*[$@$!%*#?&])[A-Za-z[0-9]$@$!%*#?&]{9,}$";

        Boolean check = Pattern.matches(pw_Pattern,pw);
        Log.d("실행","비밀번호 정규식 결과="+check);

        // 결과값 반환
        return check;
    } // end validate_pw


    // 비밀번호 = 비밀번호 확인 일치
    public boolean check_pw_equal(String pw, String pw_double){
        if(pw.equals(pw_double)){ // 일치
            return true;
        }else{ // 일치하지 않음
            return false;
        }
    } // end check_pw_equal




}
