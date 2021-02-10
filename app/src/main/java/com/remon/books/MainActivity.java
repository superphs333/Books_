package com.remon.books;

import android.content.SharedPreferences;
import android.net.Uri;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.remon.books.Function.Function_Set;
import com.remon.books.Function.Function_SharedPreference;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
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
    Function_SharedPreference function_sharedPreference;





    /*
    구글로그인
    참고) https://dvlv.tistory.com/27
     */
    private SignInButton signInButton;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    private int RC_SIGN_IN=123;

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
        signInButton = findViewById(R.id.sign_in_button); // 구글로그인 버튼


        // 함수모음
        function_set = new Function_Set();
        function_set.context = context; // context셋팅
        function_sharedPreference = new Function_SharedPreference(context);






        /*
        버튼연결
         */
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("실행", "구글 로그인 버튼");

                // 구글 로그인이 이미 되어 있는 경우에는,
                /*
                사용자에게 로그인할 Google계정을 선택하라는 메세지 표시
                + profile, email, openid 이외의 범위를 요청한 경우
                사용자에게 요청 된 리소스에 대한 액세스 권한을 부여하라는 메세지도 표시됨
                 */
                Intent intent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(intent,RC_SIGN_IN);
            }
        });

        /*
        구글로그인 및 GoogleSignInClient 개체 구성
        : 로그인 활동의 onCreate메서드에서 앱에 필요한 사용자 데이터를 요청하도록
        Google로그인을 구성
        ex) 사용자 id및 기본 프로필 정보를 요청하도록 google로그인을 구성
        -> DEFAULT_SIGN_IN매개변수 사용하여 GoogleSignInOptions개체를 만든다
        ex) 이메일 주소도 요청하기
        -> reqeustEmail옵션을 사용하여 객체를 만든다
         */
        //
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                    // 이메일도 사용하기기
               .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        /*
        로그인 작업의 onCreate메서드에서 FirebaseAuth객체의 공유 인스턴스를 가져온다
         */
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Check if user is signed in (non-null) and update UI accordingly.
        /* 구글 로그인
        : onStart에서 사용자가 이미 Google로 앱에 로그인했는지 확인
         */
        FirebaseUser currentUser = mAuth.getCurrentUser();
        //updateUI(currentUser);

        if(currentUser != null){
            // User is signed in
            Log.d("실행", "User is signed in");
            String name = currentUser.getDisplayName();
            String email = currentUser.getEmail();
            Uri photoUrl = currentUser.getPhotoUrl();
            String platform_id =  currentUser.getUid();
            Log.d("실행", "name="+name+", email="+email+", photoUrl="+photoUrl+", platform_id="+platform_id);


            // 페이지 이동
            //Intent intent = new Intent(context,Main.class);
            //startActivity(intent);

        }else{
            // No user is signed in
            Log.d("실행", "No user is signed in");

        }
    } // end onStart

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        /* 구글로그인
        : 사용자가 로그인 한 후 활동의 onAcitivtyResult메서드에서 사용자에 대한
        GoogleSignInAccount 개체를 가져올 수 있다다
        */
        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if(requestCode==RC_SIGN_IN){
                // The Task returned from this call is always completed, no need to attach
                // a listener.
            Task<GoogleSignInAccount> task=
                    GoogleSignIn.getSignedInAccountFromIntent(data);

            /*
            GoogleSignInAcoount개체에는 사요자 이름과 같이 로그인 한 사용자에 대한 정보가
            포함된다
            */
            try{
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);

                /*
                사용자가 정상적으로 로그인하면, GoogleSignInAccount객체에서 ID토큰을 가져와서
                Firebase사용자 인증 정보로 교환하고 해당 정보를 사용해 Firebase에 인증한다
                 */
                firebaseAuthWithGoogle(account);
            }catch (Exception e){
                // Google Sign In failed, update UI appropriately
                Log.w("실행", "Google sign in failed", e);
                Toast.makeText(getApplicationContext(), "Google sign in Failed", Toast.LENGTH_LONG).show();

            }

        } // end requestCode==RC_SIGN_IN
    }

    // [START auth_with_google]
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d("실행", "firebaseAuthWithGoogle:" + acct.getId());
        // [START_EXCLUDE silent]
        //showProgressDialog();
        // [END_EXCLUDE]



        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // signInWithCredential에 대한 호출이 성공하면,
                            // getCurrentUser메서드로 사용자의 계정 데이터를 가져 올 수 있다

                            Log.d("실행", "signInWithCredential:success");

                            /*
                            사용자가 처음으로 로그인하면, 신규 사용자 계정이 사용되고
                            사용자 인증 정보(사용자가 로그인할 때 사용한 사용자 이름과 비밀번호,
                            전화번호 또는 인증제공업체 정보)에 연결된다
                            -> 이 신규계정은 Firebase프로젝트에 저장되어,
                            사용자의 로그인 방법과 무관하게 프로젝트 내의 모든 앱에서 사용자 본인 확인에
                            사용할 수 있다
                             */
                            FirebaseUser user = mAuth.getCurrentUser();
                                // FirebaseUser객체에서 사용자의 기본 프로필을 가져올 수 있다다
                            //updateUI(user);
                           Toast.makeText(getApplicationContext(), "Complete", Toast.LENGTH_LONG).show();

                           /*
                           사용자 프로필 가져오기
                           : name, email address, profile photo url
                            */
                           if(user != null){
                               String profile_url = String.valueOf(user.getPhotoUrl());
                               String login_value =  user.getUid();
                               //Log.d("실행", "profile_url="+profile_url);

                               // Check if user's email is verified
                               boolean emailVerified = user.isEmailVerified();
                               Log.d("실행", "emailVerified="+emailVerified);

                               /*
                               신규회원인지 이미 회원가입한 회원인지를 분기한다
                                */
                               validate_new(user.getUid(),profile_url);




                           } // end user!=null






                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("실행", "signInWithCredential:failure", task.getException());
                            Toast.makeText(getApplicationContext(), "Authentication Failed", Toast.LENGTH_LONG).show();

                            // updateUI(null);
                        }

                        // [START_EXCLUDE]
                        // hideProgressDialog();
                        // [END_EXCLUDE]
                    }
                });
    }
    // [END auth_with_google]

    /*
    txt_signup 버튼클릭 => 회원가입 페이지(SignUp.class)로 이동
     */
    public void go_to_Signup_Page(View view) {
        Log.d("실행", "txt_signup(회원가입) 클릭");
        Intent intent = new Intent(this,SignUp.class);
        startActivity(intent);
        finish();
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


                        if(response.equals("1")){ // 값존재 -> 로그인 성공
                            Toast.makeText(getApplicationContext()
                                    , "로그인 되었습니다",Toast.LENGTH_LONG).show();

                            /*
                            Shared에 회원 Unique_Value 저장(편의)
                             */
                            function_sharedPreference.PREFERENCE = "member";
                            function_sharedPreference.setPreference("Unique_Value",email);


                            // 페이지 이동
                            Intent intent = new Intent(context,Main.class);
                            finish();
                            startActivity(intent);
                        }else{ // 값없음 -> 로그인 실패
                            Toast.makeText(getApplicationContext()
                                    , "이메일 또는 비밀번호를 확인해주세요",Toast.LENGTH_LONG).show();
                        }
                    } // end onResponse
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

    // 신규회원인지, 기존회원인지 분류
    private void validate_new(final String login_value, final String profile_url){
        /// 웹페이지 실행하기
        String url = getString(R.string.server_url)+"google_login_validate_new.php";

        StringRequest request = new StringRequest(
                Request.Method.POST,
                url,
                new com.android.volley.Response.Listener<String>() { // 정상 응답
                    @Override
                    public void onResponse(String response) {
                        Log.d("실행","response=>"+response);

                        if(response.equals("1")){
                            // 존재하는 회원(구글-로그하웃 했다가, 다시 로그인하는 경우)

                            // Shared - member 에 저장
                            function_sharedPreference.PREFERENCE = "member";
                            function_sharedPreference.setPreference("Unique_Value",login_value);

                            // Main페이지로 이동
                            Intent intent = new Intent(context,Main.class);
                            intent.putExtra("login_value",login_value);
                            startActivity(intent);
                            finish();

                        }else if(response.equals("0")){ // 존재하지 않는 신규회원
                            // 닉네임 설정 액티비티로 이동
                            Intent intent = new Intent(context,Set_nickname.class);
                            intent.putExtra("profile_url",profile_url);
                            intent.putExtra("login_value",login_value);
                            startActivity(intent);
                            finish();
                        }else{
                            Toast.makeText(getApplicationContext()
                                    , "문제가 발생하였습니다. 다시 시도해주세요",Toast.LENGTH_LONG).show();
                        }

                    }// end onResponse
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

    }

    // 구글 로그인 탈퇴(임시코드)
    public void google_unlink(View view) {
        mAuth.getCurrentUser().delete();
    } // end google_unlink


    // 비밀번호 찾기
    public void find_pw(View view) {
        Intent intent = new Intent(context,Find_Pw.class);
        startActivity(intent);
    } // end find_pw
}
