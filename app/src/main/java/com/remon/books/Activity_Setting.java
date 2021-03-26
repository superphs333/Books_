package com.remon.books;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

    // 구글 로그인 용(로그아웃/회원탈퇴)
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__setting);

        /*
        뷰연결
         */
        activity = this;
        context = getApplicationContext();
        btn_change_pw = findViewById(R.id.btn_change_pw);


        // 함수셋팅
        fshared = new Function_SharedPreference(context);

        // 일반로그인인지 구글로그인인지 확인
            // 일반로그인일때만 비밀번호 변경 버튼 보인다
        if(fshared.getPreferenceString("member","platform_type").equals("google")){
            // 구글로 로그인 한 경우
            btn_change_pw.setVisibility(View.GONE);
        }

    }

    /*
    비밀번호 받아오기
    현재 비밀번호 입력
    -> (일치) Change_Pw 액티비티로 이동(비밀번호 변경)
     */
   public void change_pw(View view) {
       // login_Value
       final String login_value = fshared.getPreferenceString("member","login_value");

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

    // 로그아웃
    public void logout(View view) {

       // 구글로 로그인 한 회원인 경우
       mAuth = FirebaseAuth.getInstance();
       FirebaseUser currentUser = mAuth.getCurrentUser();
       if(currentUser!=null){
           // 구글 로그인 회원
           FirebaseAuth.getInstance().signOut();
       }

        delete_and_intent();
    }

    /*
    회원탈퇴
    - 알림창 ->
     */
    public void withdrawal(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("알림"); //AlertDialog의 제목 부분
        builder.setMessage("정말로 탈퇴하시겠습니까?"); //AlertDialog의 내용 부분
        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d("실행","예 누름");

                // 회원정보 삭제
                withdrawal();

            }
        });

        builder.setNeutralButton("취소", null);
        builder.create().show(); //보이기

    }

    // 회원정보 삭제
    public void withdrawal(){
        // 현재 회원 login_value값
        final String login_value = fshared.getPreferenceString("member","login_value");
        Log.d("실행", "login_value="+login_value);

        // 서버에서 회원정보 삭제
        // 웹페이지 실행하기
        String url = getString(R.string.server_url)+"withdrawal.php";

        StringRequest request = new StringRequest(
                Request.Method.POST,
                url,
                new com.android.volley.Response.Listener<String>() { // 정상 응답
                    @Override
                    public void onResponse(String response) {
                        Log.d("실행","response=>"+response);

                        if(response.equals("success")){
                            // 회원정보 삭제 성공

                            // 구글 or 일반 로그인 분기
                            if(fshared.getPreferenceString("member","platform_type").equals("google")){
                                // 구글로 로그인 한 경우
                                Log.d("실행", "구글로그인");

                                // 계정 삭제
                                FirebaseUser user
                                        = FirebaseAuth.getInstance().getCurrentUser();
                                user.delete()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                Log.d("실행","User account deleted");

                                                delete_and_intent();

                                            }
                                        });

                            }else{
                                // 일반로그인
                                delete_and_intent();
                            }


                        }else{
                            // 회원정보 삭제 실패
                            Toast.makeText(getApplicationContext(), "문제가 생겼습니다. 다시 시도해주세요",Toast.LENGTH_LONG).show();
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
                return params;
            }
        };

        // 요청 객체를 만들었으니 이제 requestQueue 객체에 추가하면 됨.
        // Volley는 이전 결과를 캐싱하므로, 같은 결과가 있으면 그대로 보여줌
        // 하지만 아래 메소드를 false로 set하면 이전 결과가 있더라도 새로 요청해서 응답을 보여줌.
        request.setShouldCache(false);
        AppHelper.requestQueue = Volley.newRequestQueue(context);
        AppHelper.requestQueue.add(request);

    } // end withdrawal()


    private void delete_and_intent(){
        // shared - auto_login-auto_login - false
        fshared.setPreference("auto_login","auto_login",false);

        // shared에 저장되어 있는 정보 삭제
        fshared.setPreferenceClear("member");

        // MainAcitvity 페이지로 이동
        Intent intent = new Intent(context,MainActivity.class);
        ActivityCompat.finishAffinity(this);
        startActivity(intent);
    }

    // Activity_Management_Comment로 이동
    public void Go_To_Management_Comment(View view) {
        Intent intent = new Intent(context,Activity_Management_Comment.class);
        intent.putExtra(getString(R.string.login_value),fshared.get_login_value());
        startActivity(intent);
    }

    // Activity_Management_Follow로 이동
    public void Go_To_Management_Follow(View view) {
        Intent intent = new Intent(context,Activity_Management_Follow.class);
        intent.putExtra(getString(R.string.login_value),fshared.get_login_value());
        startActivity(intent);
    }
}
