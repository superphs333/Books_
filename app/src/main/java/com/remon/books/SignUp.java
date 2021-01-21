package com.remon.books;
import android.content.Context;
import android.os.AsyncTask;
import android.os.StrictMode;
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

import javax.mail.MessagingException;
import javax.mail.SendFailedException;

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

    // 이메일 보내는 객체
    GMailSender gMailSender;

    // 이메일 인증문자
    String temp_email_string;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        /*
        인터넷 사용을 위한 권한
         */
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .permitDiskReads()
                .permitDiskWrites()
                .permitNetwork().build());


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

                // 결과값이 unable인 경우 -> 함수 빠져나오기
                if(result.equals("unable")){
                    return;
                }

                /*
                이메일 전송
                 */
                gMailSender = new GMailSender("lee333dan@gmail.com","hipulkxqivsomwou");
                // 임시 문자 생성
                temp_email_string = gMailSender.getEmailCode();
                Log.d("실행", "생성 된 인증문자="+temp_email_string);
                // 이메일 보내기
                String email_title
                        = getString(R.string.app_title)+"에서 온 인증문자입니다";
                String email_content
                        = "다음의 인증문자를 입력하세요 :"+temp_email_string;
                try {
                    gMailSender.sendMail(email_title, email_content, email);
                } catch (SendFailedException e) {
                    Toast
                            .makeText(getApplicationContext(), "이메일 형식이 잘못되었습니다.", Toast.LENGTH_SHORT).show();
                }catch (MessagingException e) {
                    Toast.makeText(getApplicationContext(), "인터넷 연결을 확인해주십시오+", Toast.LENGTH_SHORT).show();
                    Log.d("실행","MessagingException=왜안나와..");
                    Log.d("실행","MessagingException=>"+e.getMessage());
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(),"예상치 못한 문제가 발생하였습니다. 다시 한 번 시도해주세요", Toast.LENGTH_SHORT).show();
                    Log.d("실행","Exception=>"+e.getMessage());
                } // 이메일 전송 catch문 끝


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
