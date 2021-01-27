package com.remon.books;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
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


    // 비트맵 -> 저장, uri리턴
    public Uri SaveImage(Bitmap bitmap){
        Log.d("실행","SaveImage함수 실행");

        //String root = getExternalCacheDir().toString();
        String root = Environment.getExternalStorageDirectory().toString();
        Log.d("실행","root =>"+root);
        //String root = getFilesDir().toString();
            /*
            File getExternalCacheDir()
            = File[]을 반환 함
            : 응용 프로그램이 소유한 캐시 파일을 배치 할 수 있는 모든
            공유/외부 저장 장치의 응용 프로그램 별 디렉토리에 대한
            절대 경로를 반환
             */
        File myDir = new File(root+"/saved_image2");
        // File(String pathname) : pathname에 해당하는
        // File 객체를 생성
        Log.d("실행","myDir=>"+myDir);
        myDir.mkdirs();
        // 존재하지 않는 부모 폴더까지 포함하여 해당 경로에 폴더를
        // 만든다



        // 랜덤값을 생서아호 그 값을 포함하여 이미지 파일 이름을 정한다.
        Random generator = new Random();
        int n = 1000000;
        n = generator.nextInt(n);
        //String fname = "Image-"+n+".jpg";
        fname = "Image-"+n+".jpg";
        File file = new File(myDir,fname);
        // File(File dir, String name)
        // = dir객체 폴더에 name이라는 파일에 대한 file객체 생성

        if(file.exists()){file.delete();}
        // 팡리이 존재한다면, 파일을 삭제한다.

        FileOutputStream out = null;
        try {
            out = new FileOutputStream(file);
            Log.d("실행","저장성공");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.d("실행","FileNotFoundException에러=>"+e.getMessage());
        }


        finalBitmap.compress(Bitmap.CompressFormat.JPEG,90,out);
            /*
            Bitmap
        		.compress(Bitmap.CompressFormat format
        		, int quality, OutpuStream stream)
        	비트맵의 압축 버전을 지정된 출력 스트림에 쓴다.
             */

        try {
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 버퍼의 내용을 강제로 출력하여 비운다
        // = 버퍼에 남은 outputstream출력
        try {
            out.close(); // outputstream을 닫는다
        } catch (IOException e) {
            e.printStackTrace();
        }




        realPath = file.getAbsolutePath();
        // File.getAbsolutePath() = 외부 저장소 절대 경로 찾기
        Log.d("실행",realPath);

        File f = new File(realPath);
        // File.getAbsolutePath() = 외부 저장소 절대 경로 찾기
        Uri uri = Uri.fromFile(f);
            /*
            Uri.fromFile() = File을 URI로 변경한다
            ()
             */
        Log.d("실행","in SaveImage uri="+uri);
        Log.d("실행","in SaveImage realPath="+realPath);

        /*
        임시적
        FileInputStream mFileInputStream
                        = new FileInputStream(fileName);
         */
        try {
            FileInputStream mFileInputStream
                    = new FileInputStream(file);
            Log.d("실행","inSaveImage 성공");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.d("실행","inSaveImage - FileNotFoundException - 오류남=>"+e.getMessage());
        }

        return uri; // 파일의 절대경로 리턴
    } // end SaveBitmap

}
