package com.remon.books;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.remon.books.Function.Function_Set;
import com.remon.books.Function.Function_SharedPreference;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

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
import java.util.Random;

public class Activity_Change_Profile extends AppCompatActivity {

    Activity activity;
    Context context;
    ImageView img_profile;

    // 함수
    Function_SharedPreference fshared;
    Function_Set fs;

    /*
    startAcitivyForResult용 변수
     */
    int PICK_FROM_GALLERY = 123;
    int REQUEST_IMAGE_CAPTURE = 456;

    /*
    이미지 관련
     */
    String image_Uri;
    Bitmap image_bitmap;

    // 서버에 전송할 파일
    FileInputStream mFile_Input_Stream;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__change__profile);

        // 뷰연결
        activity = Activity_Change_Profile.this;
        context = getApplicationContext();
        img_profile = findViewById(R.id.img_profile);

        // 함수셋팅
        fs = new Function_Set(context,activity);
        fs.activity = activity;
        fs.context = context;
        fshared = new Function_SharedPreference(context);

        // 기존 이미지 셋팅
        Glide.with(context).load(fshared.getPreferenceString("member","profile_url")).into(img_profile);



    }

    // 카메라 버튼 클릭
    public void camera_for_profile(View view) {

        image_Uri = fs.camera_for_profile(REQUEST_IMAGE_CAPTURE);
        fs.log("image_Uri="+image_Uri);

    } // end camera_for_profile

    // 갤러리 버튼 클릭
    public void gallery_for_profile(View view) {
        // 갤러리에서 이미지를 가져온다

        fs.gallery_for_profile(PICK_FROM_GALLERY);

    } // end gallery_for_profile

    // 등록된 이미지 삭제
    public void delete_profile_picture(View view) {
        // 이미지에 기본 이미지 셋팅
        img_profile.setImageResource(R.drawable.basic_profile_img);

        // uri에 셋팅되어 있는 값 초기화
        image_Uri = null;
    }

    // 이미지 전송하기
    public void send_to_server(View view) {
        InsertData insertData = new InsertData();
        insertData.execute(fshared.getPreferenceString("member","login_value"));
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


        }

        /*
        카메라에서 이미지 가져오기 -> 이미지 회전 -> 크롭
         */
        if(requestCode==REQUEST_IMAGE_CAPTURE && resultCode==RESULT_OK){
            Log.d("실행", "카메라에서 사진 가져오기");

            Uri original_uri = Uri.parse(image_Uri);
            Uri return_uri = null;
            if(original_uri.getScheme()==null){
                return_uri = Uri.fromFile(new File(image_Uri));
            }else{
                return_uri = original_uri;
            }

            // 이미지 크롭하기
            CropImage.activity(return_uri).setGuidelines(CropImageView.Guidelines.ON).start(activity);
        }


        /*
        이미지 크롭
         */
        if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            Log.d("실행","requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE");

            if(resultCode==RESULT_OK){
                image_Uri = CropImage.getActivityResult(data).getUri().getPath();
                Log.d("실행", "image_Uri=>"+image_Uri);

                // 비트맵 방향 변경
                String temp = String.valueOf(image_Uri);
                image_bitmap = fs.rotate_from(temp);

                // 이미지 셋팅
                img_profile.setImageBitmap(image_bitmap);
            }

        }else if(resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            Exception error = result.getError();
            Log.d("실행", "이미지 크롭 에러=>"+error);
        }
    } // end onActivityResult

    class InsertData extends AsyncTask<String, Void, String>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {

            // 서버주소
            String server_Url = getString(R.string.server_url)+"change_profile_img.php";

            /*
            입력값
             */
            String login_value = strings[0];


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
                    pd.append("Content-Disposition: form-data; name=\"login_value\""+lineEnd);
                    pd.append(lineEnd);
                    pd.append(login_value+lineEnd);


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
            String get_profile_img = string_array[string_array.length-2];

            if(result.equals("success")){
                Toast.makeText(getApplicationContext()
                        , "프로필 이미지가 성공적으로 변경되었습니다.",Toast.LENGTH_LONG).show();

                //shared에 이미지 변경
                fshared.setPreference("member","profile_url",get_profile_img);

                // 로그인 페이지로 이동
                finish();
            }else{
                Toast.makeText(getApplicationContext()
                        , "죄송합니다. 문제가 생겼습니다. 다시 시도해주세요",Toast.LENGTH_LONG).show();
            }

        } // end onPostExecute
    }



}
