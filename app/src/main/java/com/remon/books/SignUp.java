package com.remon.books;
import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

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
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.remon.books.Function.Function_Set;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.MessagingException;
import javax.mail.SendFailedException;

import retrofit2.http.Url;

public class SignUp extends AppCompatActivity {

    /*
    뷰변수
     */
    Context context;
    Activity activity;
    EditText edit_email, edit_email_chk, edit_pw, edit_pw_double, edit_nick;
    TextView txt_pw_info, txt_nick_info,txt_pw_double_info;
    Button btn_email_chk,btn_nick_chk ,btn_camera, btn_gallery, btn_picture_delete, btn_sign_up;
    ImageView img_profile;

    // 함수모음 객체
    Function_Set function_set;
    GetUri getUri;

    // 이메일 보내는 객체
    GMailSender gMailSender;

    // 이메일 인증문자
    String temp_email_string;

    // 중복 확인 체크한(중복이 아닌) 이메일
    String temp_email_not_duplication;

    /*
    startAcitivyForResult용 변수
     */
    int PICK_FROM_GALLERY = 123;
    int REQUEST_IMAGE_CAPTURE = 456;


    /*
    회원가입 조건
     */
    boolean email_no_double =false;
    boolean regex_pw =false;
    boolean pw_equal =false;
    boolean nick_no_double =false;


    /*
    이미지 관련 변수
     */
    Bitmap image_bitmap;
    String image_Uri;

    /*
    파일전송
     */
    // 서버에 전송할 파일
    FileInputStream mFile_Input_Stream;




    /*
    권한이 허가되거나 거부당했을 때,
    결과를 리턴해주는 리스너
     */
    PermissionListener permissionlistener = new PermissionListener() {

        /*
        권한이 모두 허용 되거나서 실행됨
         */
        @Override
        public void onPermissionGranted() {
            Log.d("실행", "권한허가");
        }

        /*
        요청한 권한중에서 거부당한 권한목록을 리턴해줌
        ex) 3개의 권한을 요청했는데 사용자가 1개만 허용하고 2개는 거부한 경우
        -> onPermissionDenied()에 2개의 거부당한 권한목록이 전달됨
         */
        @Override
        public void onPermissionDenied(ArrayList<String> deniedPermissions) {
            Log.d("실행", "거부 된 권한->"+deniedPermissions.toString());
        }


    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);


        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .permitDiskReads()
                .permitDiskWrites()
                .permitNetwork().build());


        /*
        권한체크 시작
         */
        TedPermission.with(this)
                .setPermissionListener(permissionlistener)
                .setRationaleMessage("카메라, 갤러리, 파일 액세스 권한이 필요합니다")
                    // 권한을 요청하기 전에 이 권한이 왜 필요한지 이유에 대해서 설명하는 메세지
                .setDeniedMessage("권한을 거부하셨습니다")
                    // 사용자가 권한을 거부했을 때 나타나는 메시지
                .setPermissions(
                        new String[] { Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.CAMERA
                        }
                )
                .check();
                    // 실제로 권한체크를 시작함




        /*
        뷰연결
        */
        context = getApplicationContext();
        activity = SignUp.this;
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



        // 함수모음
        function_set = new Function_Set();
        function_set.context = context; // context셋팅

        // 이미지 uri 받기위해
        getUri = new GetUri();


        /*
        이메일 중복 체크를 통과(email_no_double=true)
        => 이메일 변경시 경고창(이메일 중복체크를 다시 해야 합니다)

         */
        edit_email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {



            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {



                if(email_no_double==true && !edit_email.getText().toString().equals(temp_email_not_duplication)){
                    AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                    builder.setTitle("알림"); //AlertDialog의 제목 부분
                    builder.setMessage("중복체크가 완료된 이메일을 변경하시면 이메일 중복체크를 다시 해야합니다. 변경하시겠습니까?");
                    builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Log.d("실행","예 누름");

                            email_no_double = false;
                            temp_email_not_duplication = null; // 중복되지 않은 이메일 초기화
                            temp_email_string = null; // 인증문자 초기화
                            edit_email.setText("");
                        }
                    });
                    builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Log.d("실행","아니요 누름");

                            // 이전값(중복체크가 완료된)으로 되돌리기
                            edit_email.setText(temp_email_not_duplication);

                            // 만약에 -> ()이면 -> edit Text 변경하기


                        }
                    });
                    builder.setNeutralButton("취소", null);
                    builder.create().show(); //보이기

                } // end if
            } // end afterTextChanged
        });

        /*
        비밀번호를 입력 할 때마다 비밀번호 정규식 체크
         */
        edit_pw.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // 무언가 바뀐 시점 전에(글자 변화되기 전)
                 //Log.d("실행", "beforeTextChanged");
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // 무언가 바뀐 시점(글자 변화되는 중)
                //Log.d("실행", "onTextChanged");
            }

            @Override
            public void afterTextChanged(Editable s) {
                // 무언가 바뀐 이후(글자 변화된 후)
                //Log.d("실행", "afterTextChanged");

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


                    // 만약 이전 닉네임과 같은 경우가 아니라면 nick_no_double = false
                    // + txt_nick_info 변경
                    if(!edit_nick.equals(before_nick)){
                        txt_nick_info.setText("중복확인 문구");
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
        if(!function_set.is_Email(email)){ // 이메일 형식이 아닌 경우 토스트로 알림 -> 함수종료

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
        // Function_Set객체의 input변수 셋팅
        function_set.input = email;
        function_set.chk_double(new Function_Set.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                // 결과값이 unable인 경우 -> 함수 빠져나오기
                if(result.equals("unable")){
                    email_no_double = false;
                    return;
                }

                /*
                이메일 전송
                 */
                // 이메일 double 체크 true
                email_no_double = true;
                // 중복체크 확인한 이메일 임시 저장
                temp_email_not_duplication = email;
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

    // 닉네임 중복체크 버튼 클릭
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

    // 카메라 버튼 클릭
    public void camera_for_profile(View view) {
        // 카메라에서 이미지를 가져온다

        Intent intent
                = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            /*
            MediaStore.ACTION_IMAGE_CAPTURE
            = 카메라 응용 프로그램에서 이미지를 캡쳐하여 리턴해준다
             */

        /*
        암시적 인텐트를 처리 할 수 있는 앱이 단말에 설치 되었는지 확인하기 위해
        -> resolveAcitivty
        , 만약 설치되어 있지 않았다면, startActivity를 호출하는 순간, 앱은 크래시 된다
         */
        if(intent.resolveActivity(getPackageManager())!=null){
            // 설치가 되어 있는 경우에만

            // 1. 임의 경로에 파일 만들기
            File photo_File = null;
            try {
                photo_File = function_set.createImageFile();
            } catch (IOException e) {
                e.printStackTrace();
                Log.d("실행", "createImageFile 오류=>"+e.getMessage());
            }
            Log.d("실행", "uri=>"+photo_File.getAbsolutePath());
            image_Uri = photo_File.getAbsolutePath();


            // 2. FileProvider를 통해서 파일의 uri값을 만든다
                // 이런식으로 했을때 onAcitivty도달 가능능
           if(photo_File !=null){
                Uri photo_Uri = FileProvider.getUriForFile(context,getPackageName(),photo_File);
                intent.putExtra(MediaStore.EXTRA_OUTPUT,photo_Uri);
                startActivityForResult(intent,REQUEST_IMAGE_CAPTURE);
            }

        }else{
            Toast.makeText(getApplicationContext()
                    , "실행 할 수 있는 앱이 없습니다. 카메라 어플을 설치해주세요",Toast.LENGTH_SHORT).show();
        }
    }

    // 갤러리 버튼 클릭
    public void gallery_for_profile(View view) {
        // 갤러리에서 이미지를 가져온다

        Intent intent = new Intent();
        intent.setType("image/*");
            // 이미지르 열 수 있는 앱을 호출
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,PICK_FROM_GALLERY);

    }

    // 등록된 이미지 삭제
    public void delete_profile_picture(View view) {
        Log.d("실행", "delete_profile_picture");

        // 이미지에 기본 이미지 셋팅
        img_profile.setImageResource(R.drawable.basic_profile_img);

        // uri에 셋팅되어 있는 값 초기화
        image_Uri = null;

    }

    // 입력한 내용 서버로 전송
    public void send_to_server(View view) {

        /*
        검사 : 이메일, 비밀번호, 닉네임
         */
        // 이메일 중복 확인
        if(!email_no_double){
            Toast.makeText(getApplicationContext()
                    , "이메일 중복체크를 확인해주세요",Toast.LENGTH_LONG).show();
            return;
        }
        // 이메일 인증 문자 확인
        if(!edit_email_chk.getText().toString().equals(temp_email_string)){
            Toast.makeText(getApplicationContext()
                    , "이메일 인증 문자를 확인해주세요",Toast.LENGTH_LONG).show();
            return;
        }
        // 비밀번호 적합성 확인
        if(!regex_pw){
            Toast.makeText(getApplicationContext()
                    , "적합한 비밀번호인지 확인해주세요",Toast.LENGTH_LONG).show();
            return;
        }
        // 비밀번호 = 비밀번호 일치 확인
        if(!pw_equal){
            Toast.makeText(getApplicationContext()
                    , "비밀번호와 비밀번호 확인은 일치해야 합니다",Toast.LENGTH_LONG).show();
            return;
        }

        // 닉네임 중복 확인
        if(!nick_no_double){
            Toast.makeText(getApplicationContext()
                    , "닉네임을 확인해주세요",Toast.LENGTH_LONG).show();
            return;
        }


        /*
        서버로 보낼 데이터
        : 이메일, 비밀번호, 닉네임, 프로필 사진
        -> asynctastk에는 아이디, 비밀번호, 닉네임만 보내면 된다
        (이미지는 따로)
         */
        String email = edit_email.getText().toString();
        String pw = edit_pw.getText().toString();
        String nickname = edit_nick.getText().toString();

        InsertData task = new InsertData();
        task.execute(email, pw, nickname);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        /*
        갤러리 이미지 가져오기
         */
        if(requestCode==PICK_FROM_GALLERY && resultCode==RESULT_OK
        && data!=null && data.getData()!=null){

            Log.d("실행", "(갤러리)경로=>"+data.getData());

            // 이미지 크롭
            CropImage
                    .activity(data.getData())
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .start(activity);

            // 파일 절대 경로 얻어오기
            image_Uri = getUri.getPath(context,data.getData());
            Log.d("실행", "절대경로=>"+image_Uri);


        } // end 갤러리에서 이미지 가져오기

        /*
        카메라에서 이미지 가져오기 -> 이미지 회전 -> 크롭
         */
         if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK){
             Log.d("실행", "카메라에서 사진 가져오기");

             // 참고 : https://github.com/ArthurHub/Android-Image-Cropper/issues/466
             Uri original_uri = Uri.parse(image_Uri);
             Uri return_uri = null;
             if(original_uri.getScheme()==null){
                  Log.d("실행", "original_uri.getScheme()==null");
                 return_uri = Uri.fromFile(new File(image_Uri));
             }else{
                 Log.d("실행", "original_uri.getScheme()!=null");
                 return_uri = original_uri;
             }
             Log.d("실행", "return_uri="+return_uri);
                // 앞에 file://이 붙어서 나온다

             // 이미지 크롭하기
             CropImage.activity(return_uri).setGuidelines(CropImageView.Guidelines.ON).start(activity);

         }

        // 이미지 크롭
        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            Log.d("실행","requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE");

            // 내부저장소는 그냥 보낼때 오류 안나나? FileInput~~~일때
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if(resultCode == RESULT_OK){

                image_Uri = CropImage.getActivityResult(data).getUri().getPath();
                    // 참고 : https://sandn.tistory.com/3
                    // 이런식으로 받아야지, 비트맵 생성 가능
                    // 카메라, 갤러리에서 접근가능능
               Log.d("실행", "image_Uri=>"+image_Uri);

                // 비트맵 방향변경
                String temp = String.valueOf(image_Uri);
                image_bitmap = function_set.rotate_from(temp);

                // 이미지 셋팅
                img_profile.setImageBitmap(image_bitmap);

            }else if(resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE){
                Exception error = result.getError();
                Log.d("실행", "이미지 크롭 에러=>"+error);
            }
        } // end 이미지 크롭
    } // end onAcitivtyResult



    class InsertData extends AsyncTask<String, Void, String>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {

            // 서버주소
            String server_Url = getString(R.string.server_url)+"signup_chk.php";

            /*
            입력값
             */
            String email = edit_email.getText().toString();
            String pw = edit_pw.getText().toString();
            String nickname = edit_nick.getText().toString();

            /*
            HttpURLConnection을 위한 변수들
             */
            String params = "";
            String lineEnd = "\r\n";
            String twoHyphens = "--";
            String boundary = "*****";
                // 넘겨지는 각 인자를 구분하기 위한 구분자



            // 프로필 사진이 있는 경우에만
            if(image_Uri!=null){
                 Log.d("실행", "image_Uri!=null");

                try {
                    mFile_Input_Stream = new FileInputStream(image_Uri);
                    Log.d("실행","FileInputStream성공");
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    Log.d("실행","FileNotFoundException에러=>"+e.getMessage());
                }
            } // end if(image_Uri!=null)

            /*
            PHP파일을 실행시킬 수 있는 주소와 전송할 데이터를 준비한다
            - POST방식으로 데이터 전달시에는 데이터가 주소에 직접 입력되지X
                -> http메세지 본문에 포함되어 전송되기 때문에 따로 데이터를
                준비해야 한다
                - 전송 할 데이터는
                    - 이름-값 형식
                        -> 여기서 적어준 이름을 나중에 php를 사용하여
                        값을 얻게 된다
                    - 여러개를 보내야 하는 경우 -> 항목사이에 &를 추가
             */

            /*
            HttpURLConnection클래스를 사용하여 POST방식으로 데이터를 전송한다
             */
            try {
                URL connectUrl = new URL(server_Url);
                Log.d("실행","URL 에러X");

                try {
                    // HttpURLConnection 생성하기
                    HttpURLConnection httpURLConnection
                            = (HttpURLConnection)connectUrl.openConnection();
                    httpURLConnection.setReadTimeout(5000);
                        // 5초 안에 응답이 오지 않으면, 예외 발생
                    httpURLConnection.setConnectTimeout(5000);
                        // 5초 안에 연결이 안되면 예외가 발생
                    httpURLConnection.setRequestMethod("POST");
                        // post방식
                    httpURLConnection
                            .setRequestProperty(
                                    "Content-Type"
                                    , "multipart/form-data;charset=utf-8;boundary=" + boundary);
                        // Content-Type을 Multipart를 이용하면(Requestbody전달시) -> byte전송이 가능
                            // 멀티미디어 파일을 서버로 전송 가능
                        // 문자열로 boundary를 초기화(이때 사용되는 문자열은 어떤 것이든 상관x)
                            // 이 문자열은 넘겨지는 각 인자를 구분하기 위한 구분자
                    httpURLConnection.setRequestProperty("Connection", "Keep-Alive");
                        // http특성상 한번 연결하고 끊고, 재연결시 새롭게 연결하는데
                        // http1.1에서부터는 재연결시 재사용 할 수 있도록 한다
                        // + 서버에서 Keep-Alive를 지원해야 이용이 가능하다
                    httpURLConnection.setRequestProperty("Cache-Control", "no-cache");
                        // 컨트롤 캐쉬 설정
                    // allow input and output
                    httpURLConnection.setDoInput(true);
                        // 읽기모드 지정
                    httpURLConnection.setDoOutput(true);
                        // 쓰기 보드 지정
                    httpURLConnection.setUseCaches(false);
                        // 캐싱데이털르 받을지 안받을지

                    /*
                    데이터 쓰기
                    : post의 경우, 스트림을 통해서 파라미터를 전송해야 하기 때문에
                     URLConnection으로 부터 OutputStream을 구해야 한다
                     => outputstream을 통해 데이터를 보낸다
                     */
                    DataOutputStream dos
                            = new DataOutputStream(httpURLConnection.getOutputStream());

                    // 텍스트값 : 이메일, pw, 닉네임
                    StringBuffer pd = new StringBuffer();
                    pd.append(lineEnd);
                    pd.append(twoHyphens+boundary+lineEnd);
                    pd.append("Content-Disposition: form-data; name=\"email\""+lineEnd);
                    pd.append(lineEnd);
                    pd.append(email+lineEnd);

                    pd.append(twoHyphens + boundary + lineEnd);
                    pd.append("Content-Disposition: form-data; name=\"pw\""+lineEnd);
                    pd.append(lineEnd);
                    pd.append(pw+lineEnd);

                    pd.append(twoHyphens + boundary + lineEnd);
                    pd.append("Content-Disposition: form-data; name=\"nickname\""+lineEnd);
                    pd.append(lineEnd);
                    pd.append(nickname+lineEnd);



                    //Log.d("실행", "DATA=>\r\n"+pd.toString());
                    // 텍스트 쓰기
                    dos.writeUTF(pd.toString());

                    // 이미지가 있는 경우에만
                    if(image_Uri!=null){

                        // 이미지 파일
                        String fileName = "";
                        Random generator = new Random();
                        int n = 1000000;
                        n = generator.nextInt(n);
                        fileName = "Image_Profile-"+n+".jpg";

                        // 이미지
                        pd.append(twoHyphens+boundary+lineEnd);
                        pd.append("Content-Disposition: form-data; name=\"uploadedfile\"; filename=\""+fileName+"\"\r\n");
                        pd.append(lineEnd);
                        dos.writeUTF(pd.toString());


                        // create a buffer of maximum size
                        int byte_Available = mFile_Input_Stream.available();
                            // 입력스트림으로 읽을 수 있는 데이터의 바이트 수를 반환
                        Log.d("실행", "byte_Available="+byte_Available);
                        int maxBufferSize = 1024;
                        int bufferSize = Math.min(byte_Available,maxBufferSize);
                            // Math.min() : 입력받은 두 개의 인자값 중 작은 값 리턴

                        byte[] buffer = new byte[bufferSize];

                        // read file and write it into form
                        int bytesRead
                                = mFile_Input_Stream.read(buffer,0,bufferSize);
                            /* int read(byte[] b, int off, int len)
                            : 지정한 개수만큼 데이터 바이트를 읽고
                            , byte배열의 지정한 위치에서부터 데이터를 저장한다
                             */
                        // read image
                        while(bytesRead>0){
                            dos.write(buffer,0,bufferSize);
                            byte_Available  = mFile_Input_Stream.available();
                            bufferSize = Math.min(byte_Available , maxBufferSize);
                            bytesRead = mFile_Input_Stream.read(buffer,0,bufferSize);
                        } // end while

                        // send multipart form data necessary after file data
                        dos.writeBytes(lineEnd);
                        dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
                        Log.e("실행" , "File is written");
                        mFile_Input_Stream.close();
                    } // end if(image_Uri!=null)

                    // 전송마무리
                    dos.flush();


                    /*
                    응답을 읽는다
                     */
                    int responseStatusCode
                            = httpURLConnection.getResponseCode();
                    Log.d("실행", "POST response code - " + responseStatusCode);

                    // inputStream을 통해 데이터를 받아온다
                    InputStream inputStream;
                    if(responseStatusCode==HttpURLConnection.HTTP_OK){
                        // 정상적인 응답 데이터 인 경우
                        inputStream = httpURLConnection.getInputStream();
                    }else{
                        // 에러 발생
                        inputStream = httpURLConnection.getErrorStream();
                    }

                    /*
                    StringBuilder을 사용하여 수신되는 데이터를 저장한다
                     */
                    InputStreamReader inputStreamReader
                            = new InputStreamReader(inputStream,"UTF-8");
                        /* InputStreamReader(InputStream in, String charsetName)
                        = charsetName을 명명하는 인코딩을 사용하는 객체를 생성
                         */
                    BufferedReader  bufferedReader
                            = new BufferedReader (inputStreamReader);
                        /* BufferedReader(Reader in)
                        = 버퍼를 이용한 입력
                        -> 버퍼를 사용하기 때문에 입출력의 효율이 좋아진다
                        => 입력스트림에 대한 디폴트 크기의 내부 버퍼를 갖는
                        객체 생성
                         */

                    StringBuilder sb = new StringBuilder();
                    String line = null;
                    while((line=bufferedReader.readLine())!=null){
                        sb.append(line);
                    }// end while
                    bufferedReader.close();

                    /*
                    저장 된 데이터를 스트링으로 변환하여 리턴한다
                     */
                    return sb.toString();



                } catch (IOException e) {
                    e.printStackTrace();
                    Log.d("실행","HttpURLConnection 오류=>"+e.getMessage());
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
                Log.d("실행","url 오류=>"+e.getMessage());
            }


            return null;
        }

        @Override
        protected void onPostExecute(String s) {

            super.onPostExecute(s);

            Log.d("실행", "POST response  - " + s);

            String[] string_array= s.split(getString(R.string.seperator));
            String result = string_array[string_array.length-1];
            Log.d("실행", "result = "+result);

            if(result.equals("success")){
                Toast.makeText(getApplicationContext()
                        , "회원가입이 완료되었습니다",Toast.LENGTH_LONG).show();

                // 로그인 페이지로 이동
                Intent intent = new Intent(context,MainActivity.class);
                startActivity(intent);
            }else{
                Toast.makeText(getApplicationContext()
                        , "죄송합니다. 문제가 생겼습니다. 다시 시도해주세요",Toast.LENGTH_LONG).show();
            }

        } // end onPostExecute
    }





}
