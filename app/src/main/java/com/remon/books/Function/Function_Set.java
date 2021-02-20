package com.remon.books.Function;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.remon.books.AppHelper;
import com.remon.books.Change_Pw;
import com.remon.books.MainActivity;
import com.remon.books.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Retrofit;

public class Function_Set {

    /*
    변수
     */
    public String input;
    public Context context;
    public Activity activity;

    // 함수
    Function_SharedPreference fshared;



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
    // log
    public void log(String input){
        Log.d("실행", input);
    }
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
                = "^(?=.*[A-Za-z])(?=.*[0-9])(?=.*[$@$!%*#?&])[A-Za-z[0-9]$@$!%*#?&]{9,12}$";

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

    // 닉네임 정규식 확인
    public boolean validate_Nick(String nickname){

        Boolean check = false;

        // 정규식
            // 영문, 숫자,한글, '_'로만 이루어진 4~12자 이하
//        String nick_Pattern
//                    = "^[a-zA-Z0-9]{1}[a-zA-Z0-9_]{3,11}$";
        String nick_Pattern = "^[a-z|A-Z|ㄱ-ㅎ|ㅏ-ㅣ|가-힣|0-9]{1}[a-z|A-Z|ㄱ-ㅎ|ㅏ-ㅣ|가-힣|0-9_]{3,11}$";
        check = Pattern.matches(nick_Pattern,nickname);

        return check;
    }

    // 회원 정보 가져오기 (by Retrofit)



    /*
    임시파일 반환하기
     */
    public File createImageFile() throws IOException{

        /*
        File.createTempFile(String prefix, String suffix, File directory)
        : 지정된 접두사와 접미사 문자열을 사용하여
        지정 된 디렉토리에 새 빈 파일을 작성하여 이름을 생성한다
         */

        // 1. String prefix부분
        String time_Stamp
                = new SimpleDateFormat("yyyyMMdd_HHmmss")
                .format(new Date());
        String image_File_Nmae = "TEST_"+time_Stamp+"_";

        // 2. directory부분(file)
        File storage_Dir
                = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            /*
            File getExternalFilesDir(String type)
            : 매개변수에 원하는 디렉토리 유형을 전달하여,
            적절한 디렉토리를 얻을 수 있다
             */

        // 3. 임시파일 생성
        File image = File.createTempFile(
            image_File_Nmae,
            ".jpg",
            storage_Dir
        );

        return image;
    }

    /*
    이미지 회전하고 Bitmap반환
     */
    public Bitmap rotate_from(String image_file_path){

        // 파일경로를 비트맵으로 디코딩(파일형태로 저장 된 이미지 -> bitmap화)
        Bitmap image_bitmap = BitmapFactory.decodeFile(image_file_path);

        ExifInterface exif = null;
            /* ExifInterface
            = 이미지 파일 EXIF(데이터) 태그를 읽고 쓴다
             */
        try {
            // ExifInterface 생성
            exif = new ExifInterface(image_file_path);
                // 지정된 이미지 파일에서 Exif태그를 읽는다
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("실행", "ExifInterface생성오류->"+e.getMessage());
        }

        int exif_Orientation;
        int exif_Degree;

        if(exif != null){
            exif_Orientation
                    = exif.getAttributeInt
                    (
                            ExifInterface.TAG_ORIENTATION
                            ,ExifInterface.ORIENTATION_NORMAL
                    );
                /* ExifInterface.getAttributeInt(String tag, int defaultValue)
                = 지정된 태그의 정수 값을 반환한다
                 */
            exif_Degree
                    = exif_Orientation_To_Degress(exif_Orientation);
        }else{
            exif_Degree = 0;
        }

        // 임의로 만든 rotate함수에서 bitmap 리턴받기
        image_bitmap = rotate(image_bitmap,exif_Degree);

        return image_bitmap;
    } // end rotate

    /*
    화면 회전을 위해
    -> 매개변수로 주어지는 방향에 따라 회전을 분기하여 얼마큰 회전해야하는지
    리턴해준다
     */
    private int exif_Orientation_To_Degress(int exifOrientation){
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
            return 90;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
            return 180;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
            return 270;
        }

        Log.d("실행", "회전->"+exifOrientation);
        return 0;
    } // end exif_Orientation_To_Degress

    /*
    비트맵을 degree만큼 회전한 후의 비트맵을 리턴해준다
     */
    private Bitmap rotate(Bitmap bitmap,float degree){
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        return
                Bitmap
                        .createBitmap(
                                bitmap,
                                0,
                                0,
                                bitmap.getWidth(),
                                bitmap.getHeight(),
                                matrix,
                                true);
    }

    /*
    비밀번호 or 닉네임을 변경한다
    - 매개변수 : sort, change, login_value
        - sort : nickname / pw
        - change : 변경값
        - login_value : 누구를 바꿀지
    - 기능
        - 서버(member_info_change.php)로 이동
        - 값여부/sort에 따라 페이지 이동
     */
    public void Change_Member_Info(final String sort, final String change, final String login_value){
        // 웹페이지 실행하기
        String url = context.getString(R.string.server_url)+"member_info_change.php";

        StringRequest request = new StringRequest(
                Request.Method.POST,
                url,
                new com.android.volley.Response.Listener<String>() { // 정상 응답
                    @Override
                    public void onResponse(String response) {
                        Log.d("실행","response=>"+response);

                        /* 성공시
                        - sort = pw
                            -> toast(비밀번호 변경이 되었습니다), MainActivity로 페이지로 이동
                        - sort = nickname
                         */
                        if(!response.equals("success")){
                            Toast.makeText(context, "죄송합니다. 문제가 생겼습니다. 다시 시도해주세요"
                                    ,Toast.LENGTH_LONG).show();
                            return;
                        } // end if

                        // pw인 경우 nickname인 경우 분기
                        if(sort.equals("pw")){ // pw인 경우 -> MainActivity이동
                            Intent intent = new Intent(context, MainActivity.class);
                            context.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                            activity.finish();
                        }else if(sort.equals("nickname")){ // nickname인 경우

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
                params.put("sort", sort);
                params.put("change",change);
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

    /*
    회원정보 가져오기 + shared에 저장하기
     */
    public void bring_nick_profile(final String login_value, final String sort){
        /* 매개변수
        login_value = 회원 유일값
        sort = nickname or profile_url or pw
         */

        // 함수셋팅
        fshared = new Function_SharedPreference(context);

        // 웹페이지 실행하기
        String url = context.getString(R.string.server_url)+"get_member_info.php";

        StringRequest request = new StringRequest(
                Request.Method.POST,
                url,
                new com.android.volley.Response.Listener<String>() { // 정상 응답
                    @Override
                    public void onResponse(String response) {
                        Log.d("실행","response=>"+response);

                        fshared.setPreference("member",sort,response);

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
                params.put("sort", sort);
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
    } // bring_nick_profile





}
