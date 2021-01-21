package com.remon.books;
import android.content.Context;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.text.Editable;
import android.text.TextWatcher;
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
    TextView txt_pw_info, txt_nick_info,txt_pw_double_info;
    Button btn_email_chk,btn_nick_chk ,btn_camera, btn_gallery, btn_picture_delete, btn_sign_up;
    ImageView img_profile;

    // 함수모음 객체
    Function_Set function_set;

    // 이메일 보내는 객체
    GMailSender gMailSender;

    // 이메일 인증문자
    String temp_email_string;

    /*
    회원가입 조건
     */
    boolean validate_email =false;
    boolean regex_pw =false;
    boolean pw_equal =false;
    boolean nick_no_double =false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);


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
        txt_pw_double_info = findViewById(R.id.txt_pw_double_info);
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

        /*
        비밀번호를 입력 할 때마다 비밀번호 정규식 체크
         */
        edit_pw.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // 무언가 바뀐 시점 전에(글자 변화되기 전)
                 Log.d("실행", "beforeTextChanged");
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // 무언가 바뀐 시점(글자 변화되는 중)
                Log.d("실행", "onTextChanged");
            }

            @Override
            public void afterTextChanged(Editable s) {
                // 무언가 바뀐 이후(글자 변화된 후)
                Log.d("실행", "afterTextChanged");




                // 입력값
                String pw = edit_pw.getText().toString();



                /*
                비밀번호 정규식에 일치하는지 확인하기
                 */
                Boolean check = function_set.validate_Pw(pw);
                if(check==true){ // 적합할때
                    txt_pw_info.setText("사용가능한 비밀번호 입니다");
                    regex_pw = true;
                }else{ // 적합하지 않을 때
                    txt_pw_info.setText("비밀번호 형식을 확인해주세요");
                    regex_pw = false;
                }


                /*
                만약, 비밀번호=비밀번호 확인이 일치한 후에 비밀번호를 변경하는 경우,
                일치하는지 확인하는 문구를 변경해준다 + pw_equal(비밀번호 일치 확인 boolen) -> false
                (pw_equal이 true인 경우)
                 */
                // 비밀번호 = 비밀번호 확인도 일치하는지 확인해 봐야 하는지의 여부
                if(pw_equal==true){
                    txt_pw_double_info.setText("입력한 비밀번호를 확인해주세요");
                    pw_equal = false;

                }

                /*
                비밀번호 = 비밀번호 일치 확인
                 */
                boolean check_double2
                        = function_set.check_pw_equal(pw, edit_pw_double.getText().toString());
                if(check_double2==true){ // 일치
                    txt_pw_double_info.setText("비밀번호가 일치합니다");
                    pw_equal = true;
                }else{ // 일치하지 않음
                    txt_pw_double_info.setText("입력한 비밀번호를 확인해주세요");
                    pw_equal = false;
                }

            }
        }); // end edit_pw.addTextChangedListener


        /*
        비밀번호=비밀번호 확인 체크하기
         */
        edit_pw_double.addTextChangedListener(new TextWatcher() {
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
                String pw_double = edit_pw_double.getText().toString(); // 비밀번호 중복 체크 값

                // 일치 확인
                boolean check = function_set.check_pw_equal(pw, pw_double);
                if(check==true){ // 일치
                    txt_pw_double_info.setText("비밀번호가 일치합니다");
                    pw_equal = true;
                }else{ // 일치하지 않음
                    txt_pw_double_info.setText("입력한 비밀번호를 확인해주세요");
                    pw_equal = false;
                }
            }
        }); // end edit_pw_double.addTextChangedListener

        /*
        중복확인 후에(+ nick_no_double = ture),
        닉네임입력칸(edit_nick)에 입력값이 있을 경우
        중복확인을 다시 해줘야 한다
         */
        edit_nick.addTextChangedListener(new TextWatcher() {

            String before_nick = edit_nick.getText().toString();

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(nick_no_double==true){ // 중복확인체크가 끝난경우
                    txt_nick_info.setText("중복확인 문구");

                    // 만약 이전 닉네임과 같은 경우가 아니라면 nick_no_double = false
                    // + txt_nick_info 변경
                    if(!edit_nick.equals(before_nick)){
                        nick_no_double = false;
                    }

                } // end if
            }
        });





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
        function_set.chk_double(new Function_Set.VolleyCallback() {
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


            }
        }, "email");



    } // end send_email

    public void Check_Nick_Double(View view) {
        // 입력값
        String Nick = edit_nick.getText().toString();

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
