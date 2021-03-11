package com.remon.books;
import android.os.AsyncTask;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.remon.books.Function.Function_Set;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Random;

public class Activity_Book_Add extends AppCompatActivity {

    /*
    함수
     */
    Function_Set fs;

    /*
    뷰변수
     */
    Context context;
    EditText edit_title, edit_authors, edit_publisher, edit_isbn, edit_content;
    Button btn_add;
    ImageView img_book;

    /*
    사진 관련
     */
    int PICK_FROM_GALLERY;
    String image_Uri;
    Bitmap image_bitmap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__book__add);

        /*
        뷰연결
         */
        context = getApplicationContext();
        edit_title = findViewById(R.id.edit_title);
        edit_authors = findViewById(R.id.edit_authors);
        edit_publisher = findViewById(R.id.edit_publisher);
        edit_isbn = findViewById(R.id.edit_isbn);
        edit_content = findViewById(R.id.edit_content);
        btn_add = findViewById(R.id.btn_add);
        img_book = findViewById(R.id.img_book);

        /*
        함수연결
         */
        fs = new Function_Set();
        fs.context =context;
        fs.activity = Activity_Book_Add.this;


    }

    // img_book클릭 => 갤러리에서 이미지 가져오고 셋팅하기
    public void get_Img(View view) {
        fs.gallery_for_profile(PICK_FROM_GALLERY);
    }

    // btn_add : 필수정보(제목, 지은이)확인 -> 책정보 보내기 -> 서버에서 저장 -> 액티비티 이동
    public void Book_Add(View view) {

        /*
        필수정보 확인
         */
        // 제목
        if(edit_title.getText().toString().equals("")){
            Toast.makeText(getApplicationContext(), "책 제목을 입력해주세요",Toast.LENGTH_LONG).show();
            return;
        }

        // 글쓴이
        if(edit_authors.getText().toString().equals("")){
            Toast.makeText(getApplicationContext(), "작가를 입력해주세요",Toast.LENGTH_LONG).show();
            return;
        }



        InsertData task = new InsertData();
        task.execute(edit_title.getText().toString(), edit_authors.getText().toString()
        , edit_publisher.getText().toString(), edit_isbn.getText().toString()
                ,"0", edit_content.getText().toString());
                // 타이틀, 작가, 출판사, isbn, 페이지수, 요약정보
    }

    class InsertData extends AsyncTask<String, Void, String>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {

            // 서버주소
            String server_Url = getString(R.string.server_url)+"Book_Add.php";

            /*
            HttpURLConnection을 위한 변수들
             */
            String params = "";
            String lineEnd = "\r\n";
            String twoHyphens = "--";
            String boundary = "*****";
            // 넘겨지는 각 인자를 구분하기 위한 구분자

            // 이미지용 inputStream
            FileInputStream mFile_Input_Stream;



            /*
            HttpURLConnection클래스를 사용하여 post방식으로 데이터를 전송한다
             */
            try {
                URL connectUrl = new URL(server_Url);
                Log.d("실행","URL 에러X");

                try {
                    // HttpURLConnection 생성하기
                    HttpURLConnection httpURLConnection
                            = (HttpURLConnection)connectUrl.openConnection();
                    httpURLConnection.setReadTimeout(5000);
                    httpURLConnection.setConnectTimeout(5000);
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection
                            .setRequestProperty(
                                    "Content-Type"
                                    , "multipart/form-data;charset=utf-8;boundary=" + boundary);
                    httpURLConnection.setRequestProperty("Connection", "Keep-Alive");
                    httpURLConnection.setRequestProperty("Cache-Control", "no-cache");
                    httpURLConnection.setDoInput(true);
                    httpURLConnection.setDoOutput(true);
                    httpURLConnection.setUseCaches(false);

                    /*
                    데이터 쓰기
                     */
                    DataOutputStream dos
                            = new DataOutputStream(httpURLConnection.getOutputStream());

                    // 텍스트값 : 제목, 지은이, 출판사, isbn, 페이지수, 요약정보
                    StringBuffer pd = new StringBuffer();
                    pd.append(lineEnd);
                    pd.append(twoHyphens+boundary+lineEnd);
                    pd.append("Content-Disposition: form-data; name=\"title\""+lineEnd);
                    pd.append(lineEnd);
                    pd.append(strings[0]+lineEnd);

                    pd.append(twoHyphens+boundary+lineEnd);
                    pd.append("Content-Disposition: form-data; name=\"authors\""+lineEnd);
                    pd.append(lineEnd);
                    pd.append(strings[1]+lineEnd);

                    pd.append(twoHyphens+boundary+lineEnd);
                    pd.append("Content-Disposition: form-data; name=\"publisher\""+lineEnd);
                    pd.append(lineEnd);
                    pd.append(strings[2]+lineEnd);

                    pd.append(twoHyphens+boundary+lineEnd);
                    pd.append("Content-Disposition: form-data; name=\"isbn\""+lineEnd);
                    pd.append(lineEnd);
                    pd.append(strings[3]+lineEnd);

                    pd.append(twoHyphens+boundary+lineEnd);
                    pd.append("Content-Disposition: form-data; name=\"total_page\""+lineEnd);
                    pd.append(lineEnd);
                    pd.append(strings[4]+lineEnd);

                    pd.append(twoHyphens+boundary+lineEnd);
                    pd.append("Content-Disposition: form-data; name=\"contents\""+lineEnd);
                    pd.append(lineEnd);
                    pd.append(strings[5]+lineEnd);


                    dos.writeUTF(pd.toString());

                    // 책 이미지가 있는 경우에만
                    if(image_Uri!=null){
                        try {
                            mFile_Input_Stream = new FileInputStream(image_Uri);
                            Log.d("실행","FileInputStream성공");

                            // 이미지 파일
                            String fileName = "";
                            Random generator = new Random();
                            int n = 1000000;
                            n = generator.nextInt(n);
                            fileName = "Image_Book_Cover-"+n+".jpg";

                            pd.append(twoHyphens+boundary+lineEnd);
                            pd.append("Content-Disposition: form-data; name=\"uploadedfile\"; filename=\""+fileName+"\"\r\n");
                            pd.append(lineEnd);
                            dos.writeUTF(pd.toString());

                            // 입력 스트림으로 읽을 수 있는 데이터의 바이트 수를 반환
                            int byte_Available = mFile_Input_Stream.available();
                            int maxBufferSize = 1024;
                            int bufferSize = Math.min(byte_Available,maxBufferSize);
                            byte[] buffer = new byte[bufferSize];
                            int bytesRead
                                    = mFile_Input_Stream.read(buffer,0,bufferSize);
                            // read image
                            while(bytesRead>0){
                                dos.write(buffer,0,bufferSize);
                                byte_Available  = mFile_Input_Stream.available();
                                bufferSize = Math.min(byte_Available , maxBufferSize);
                                bytesRead = mFile_Input_Stream.read(buffer,0,bufferSize);
                            } // end while
                            dos.writeBytes(lineEnd);
                            dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
                            Log.e("실행" , "File is written");
                            mFile_Input_Stream.close();
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                            Log.d("실행","FileNotFoundException에러=>"+e.getMessage());
                        }
                    } // end image_Uri!=null

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
                    BufferedReader bufferedReader
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
                    Log.d("실행", "URL에러 =>"+e.getMessage());
                } // end HttpURLConnection try

            } catch (MalformedURLException e) {
                e.printStackTrace();
                Log.d("실행", "URL에러 =>"+e.getMessage());

            } // end URL try

            return null;
        } // end doInBackground

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            Log.d("실행", "POST response  - " + s);
        }
    } // InsertData

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // 갤러리에서 이미지 가져오기
        if(requestCode==PICK_FROM_GALLERY && resultCode==RESULT_OK
                && data!=null && data.getData()!=null){
            // 이미지 크롭
            CropImage
                    .activity(data.getData())
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .start(Activity_Book_Add.this);


        }

        // 이미지 크롭
        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            Log.d("실행","requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE");
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if(resultCode == RESULT_OK){

                image_Uri = CropImage.getActivityResult(data).getUri().getPath();
                Log.d("실행", "image_Uri=>"+image_Uri);

                // 비트맵 방향변경
                String temp = String.valueOf(image_Uri);
                image_bitmap = fs.rotate_from(temp);

                // 이미지 셋팅
                img_book.setImageBitmap(image_bitmap);

            }else if(resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE){
                Exception error = result.getError();
                Log.d("실행", "이미지 크롭 에러=>"+error);
            }
        } // end 이미지 크롭
    } // end onActivityResult


}
