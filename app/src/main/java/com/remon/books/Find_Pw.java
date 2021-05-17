package com.remon.books;
import android.content.Intent;
import android.os.StrictMode;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.remon.books.Function.Function_Set;

import java.util.HashMap;
import java.util.Map;

import javax.mail.MessagingException;
import javax.mail.SendFailedException;

public class Find_Pw extends AppCompatActivity {


    /*
    뷰변수
     */
    Context context;
    EditText edit_email, edit_chk_char;
    Button btn_send, btn_confirm;

    // 함수
    Function_Set fs;

    // 이메일 전송
    GMailSender gMailSender;

    // 임시문자(이메일로 전송할)
    String temp_string="";

    // 인증된 이메일(인증코드를 전송할때의 이메일)
        // 이 이메일에 대한 회원의 비밀번호를 변경한다
    String validate_email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find__pw);

        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .permitDiskReads()
                .permitDiskWrites()
                .permitNetwork().build());

        /*
        뷰연결
        */
        context = getApplicationContext();
        edit_email = findViewById(R.id.edit_email);
        edit_chk_char = findViewById(R.id.edit_chk_char);
        btn_send = findViewById(R.id.btn_send);
        btn_confirm = findViewById(R.id.btn_confirm);

        // 함수
        fs = new Function_Set(context,Find_Pw.this);
        fs.context = context;

        // 이메일 객체
        gMailSender = new GMailSender("lee333dan@gmail.com","hipulkxqivsomwou");





    }


    /* 인증코드 전송(btn_send)클릭
    edit_email에 입력된 내용 확인
        -> 이메일 형식인지 확인
            -> 존재하는 이메일인지 확인
                -> 해당 이메일로 인증번호 전송
     */
    public void send_email(View view) {

        // 이메일 형식인지 확인한다
        final String email = edit_email.getText().toString();
        boolean chk = fs.is_Email(email);
        if(chk==false){
            Toast.makeText(getApplicationContext(), "이메일 형식에 맞게 입력해 주세요"
                    ,Toast.LENGTH_LONG).show();
            return;
        } // end if

        /*
        존재하는 이메일인지 확인
         */
        // 웹페이지 실행하기
        String url = getString(R.string.server_url)+"Is_Existing_Email.php";

        StringRequest request = new StringRequest(
                Request.Method.POST,
                url,
                new com.android.volley.Response.Listener<String>() { // 정상 응답
                    @Override
                    public void onResponse(String response) {
                        Log.d("실행","response=>"+response);

                        // reponse = 0 -> 존재하지 않는 이메일
                        if(response.equals("0")){
                            Toast.makeText(getApplicationContext()
                                    , "존재하지 않는 이메일입니다",Toast.LENGTH_LONG).show();
                            return;
                        } // end if
                        Log.d("실행", "존재하는 이메일");

                        // 인증문자 보내기
                        // 임시 문자 생성
                        temp_string = gMailSender.getEmailCode();
                        Log.d("실행", "생성 된 인증문자="+temp_string);
                        // 이메일 보내기
                        String email_title
                                = getString(R.string.app_title)+"에서 온 인증문자입니다";
                        String email_content
                                = "다음의 인증문자를 입력하세요 :"+temp_string;
                        try {
                            gMailSender.sendMail(email_title, email_content, email);
                            Toast.makeText(getApplicationContext(), "인증문자가 전송되었습니다!",Toast.LENGTH_LONG).show();

                        } catch (SendFailedException e) {
                            Toast
                                    .makeText(getApplicationContext(), "이메일 형식이 잘못되었습니다.", Toast.LENGTH_SHORT).show();
                            return;
                        }catch (MessagingException e) {
                            Toast.makeText(getApplicationContext(), "인터넷 연결을 확인해주십시오+", Toast.LENGTH_SHORT).show();
                            Log.d("실행","MessagingException=>"+e.getMessage());
                            return;
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(),"예상치 못한 문제가 발생하였습니다. 다시 한 번 시도해주세요", Toast.LENGTH_SHORT).show();
                            Log.d("실행","Exception=>"+e.getMessage());
                            return;
                        } // 이메일 전송 catch문 끝

                        // 인증된 이메일 셋팅
                        validate_email = email;


                    } // end onResponse
                },
                new com.android.volley.Response.ErrorListener() { // 에러 발생
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("실행","error=>"+error.getMessage());

                        Toast.makeText(getApplicationContext()
                                , "죄송합니다. 오류가 발생하였습니다. 다시 시도해주세요"
                                ,Toast.LENGTH_LONG)
                                .show();

                    }
                }

        ){ // Post 방식으로 body에 요청 파라미터를 넣어 전달하고 싶을 경우
            // 만약 헤더를 한 줄 추가하고 싶다면 getHeaders() override
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("email", email);
                return params;
            }
        };

        // 요청 객체를 만들었으니 이제 requestQueue 객체에 추가하면 됨.
        // Volley는 이전 결과를 캐싱하므로, 같은 결과가 있으면 그대로 보여줌
        // 하지만 아래 메소드를 false로 set하면 이전 결과가 있더라도 새로 요청해서 응답을 보여줌.
        request.setShouldCache(false);
        AppHelper.requestQueue = Volley.newRequestQueue(context);
        AppHelper.requestQueue.add(request);

    } // end send_email

    // 확인(btn_confirm) 클릭
        // 인증문자가 "" 이 아닌지 확인
        // 입력된 인증문자가 맞는지 확인한다
            // 맞다면 -> 비밀번호 변경 액티비티(Change_PW)로 이동
            // 틀리다면 -> TOAST
    public void validate_string_confirm(View view) {

        // 인증문자 빈값인지 확인
        if(temp_string.equals("")){ // 빈 값인 경우
            Toast.makeText(getApplicationContext(), "전송된 인증코드가 없습니다",Toast.LENGTH_LONG).show();
            return;
        }

        String temp = edit_chk_char.getText().toString();

        if(!temp_string.equals(temp)){ // 임시문자가 같지 않으면
            Toast.makeText(getApplicationContext(), "인증문자를 잘못 입력하셨습니다",Toast.LENGTH_LONG).show();
            return;
        } // end if

        // 페이지 이동
        Intent intent = new Intent(context,Change_Pw.class);
        intent.putExtra("email",validate_email);
        startActivity(intent);
    } // end validate_string_confirm


}
