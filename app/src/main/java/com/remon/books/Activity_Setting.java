package com.remon.books;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.remon.books.Function.Function_SharedPreference;

import java.util.HashMap;
import java.util.Map;

public class Activity_Setting extends AppCompatActivity {

    // 뷰변수
    Context context;
    Activity activity;
    Button btn_change_pw;

    // 함수
    Function_SharedPreference fshared;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__setting);

        /*
        뷰연결
         */
        activity = this;
        context = getApplicationContext();

        // 함수셋팅
        fshared = new Function_SharedPreference(context);
        fshared.PREFERENCE = "member";
    }

    /*
    비밀번호 받아오기
    현재 비밀번호 입력
    -> (일치) Change_Pw 액티비티로 이동(비밀번호 변경)
     */
   public void change_pw(View view) {
       // login_Value
       final String login_value = fshared.getPreferenceString("login_value");

       // 비밀번호 받아오기
       String url = getString(R.string.server_url)+"get_member_info.php";

       StringRequest request = new StringRequest(
               Request.Method.POST,
               url,
               new com.android.volley.Response.Listener<String>() { // 정상 응답
                   @Override
                   public void onResponse(final String response) {
                       Log.d("실행","response=>"+response);

                       // 다이얼로그 띄우기
                       AlertDialog.Builder alert = new AlertDialog.Builder(activity);
                       alert.setTitle("알림");
                       alert.setMessage("현재 비밀번호를 입력하세요");

                       final EditText edit_pw = new EditText(activity);
                       alert.setView(edit_pw);

                       alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                           @Override
                           public void onClick(DialogInterface dialog, int which) {
                               String input_pw = edit_pw.getText().toString();
                               Log.d("실행", "input_pw="+input_pw);

                               // 현재 비밀번호와 입력값 일치시에만 페이지 이동
                               if(!response.equals(input_pw)){ // 불일치
                                   Toast.makeText(getApplicationContext(), "비밀번호를 잘 못 입력하셨습니다",Toast.LENGTH_LONG).show();
                                   return;
                               } // end if

                               // 페이지 이동
                               Intent intent = new Intent(context,Change_Pw.class);
                               startActivity(intent);
                           }
                       });
                       alert.show();


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
               params.put("sort", "pw");
               params.put("login_value", login_value);
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
}
