package com.remon.books;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.remon.books.Data.Data_My_Book;
import com.remon.books.Function.Function_Set;
import com.remon.books.Function.Function_SharedPreference;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Activity_Book_Edit extends AppCompatActivity {

    String unique_book_value;

    Context context;
    EditText edit_title, edit_authors, edit_publisher, edit_isbn, edit_content;
    Spinner category_read_status;
    RatingBar rating;
    Button btn_edit;
    ImageView img_book;

    // 객체
    Data_My_Book dmb;

    /*
    사진 관련
     */
    int PICK_FROM_GALLERY=412;
    String image_Uri;
    Bitmap image_bitmap;
    // 이미지 변경 여부(원래 이미지로 선택했을 경우 -> false)
    boolean img_change;

    /*
    함수
    */
    Function_Set fs;
    Function_SharedPreference fshared;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__book__edit);

        //  unique_book_value 가져오기(by intent)
        unique_book_value = getIntent().getStringExtra("unique_book_value");
        Log.d("실행", "unique_book_value="+unique_book_value);

        /*
        뷰연결
         */
        context = getApplicationContext();
        edit_title = findViewById(R.id.edit_title);
        edit_authors = findViewById(R.id.edit_authors);
        edit_publisher = findViewById(R.id.edit_publisher);
        edit_isbn = findViewById(R.id.edit_isbn);
        edit_content = findViewById(R.id.edit_content);
        btn_edit = findViewById(R.id.btn_edit);
        img_book = findViewById(R.id.img_book);
        category_read_status = findViewById(R.id.category_read_status);
        rating = findViewById(R.id.rating);

        /*
        함수연결
         */
        fs = new Function_Set(context,Activity_Book_Edit.this);
        fs.context =context;
        fs.activity = Activity_Book_Edit.this;
        fshared = new Function_SharedPreference(context);

        // Spinner셋팅
        final String[] data = getResources().getStringArray(R.array.read_status);
        ArrayAdapter<String> adapter
                = new ArrayAdapter<String>(context,android.R.layout.simple_dropdown_item_1line,data);
        category_read_status.setAdapter(adapter);

        /*
        unique_book_value에 해당하는 데이터 불러오기
         */
        RetrofitConnection retrofitConnection
                = new RetrofitConnection();
        Call<ArrayList<Data_My_Book>> call
                = retrofitConnection.server
                .Get_My_Book(unique_book_value,fshared.get_login_value());
        call.enqueue(new Callback<ArrayList<Data_My_Book>>() {
            @Override
            public void onResponse(Call<ArrayList<Data_My_Book>> call, Response<ArrayList<Data_My_Book>> response) {
                if(response.isSuccessful()){
                    Log.d("실행", "resonse=>"+response.message());

                    /*
                    값셋팅
                     */
                    dmb = response.body().get(0);

                    // 값셋팅
                    edit_title.setText(dmb.getTitle());
                    edit_authors.setText(dmb.getAuthors());
                    edit_publisher.setText(dmb.getPublisher());
                    edit_isbn.setText(dmb.getIsbn());
                    edit_content.setText(dmb.getContents());
                    category_read_status.setSelection(dmb.getStatus());
                    Log.d("실행","status="+dmb.getStatus());
                    Log.d("실행","rating="+dmb.getRating());
                    rating.setRating(dmb.getRating());
                    Glide.with(context).load(dmb.getThumbnail()).into(img_book);

                }else{
                    Log.d("실행","서버에 연결은 되었으나 오류발생 ");
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Data_My_Book>> call, Throwable t) {
                Log.d("실행", "onFailure: " + t.toString()); //서버와 연결 실패

            }
        });


    }



    /* 선택
    새 이미지 선택
    이미지 제거
    기존 이미지로 변경
     */
    public void get_Img(View view) {
        AlertDialog.Builder builder
                = new AlertDialog.Builder(Activity_Book_Edit.this);
        final String str[] = {"새 이미지 선택","이미지 제거","기존 이미지로 변경"};
        builder.setTitle("선택하세요")
                .setNegativeButton("취소",null)
                .setItems(str,// 리스트 목록에 사용할 배열
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.d("실행","선택된것="+str[which]);

                                if(str[which].equals("새 이미지 선택")){
                                    fs.gallery_for_profile(PICK_FROM_GALLERY);
                                    img_change = true;
                                }else if(str[which].equals("이미지 제거")){
                                    // 이미지 기본 이미지로 변경
                                    img_book.setImageResource(R.drawable.basic_book_cover);
                                    // 이미지 변수 null
                                    image_bitmap = null;
                                    image_Uri = "";
                                    img_change = true;
                                }else if(str[which].equals("기존 이미지로 변경")){
                                    Glide.with(context).load(dmb.getThumbnail()).into(img_book);
                                    image_bitmap = null;
                                    image_Uri = "";
                                    img_change = false;
                                }
                            }
                        }
                );
        AlertDialog dialog = builder.create();
        dialog.show();
    } // end get_Img

    // 책 정보 전달하기
    public void Book_Edit(View view) {
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

        // isbn
        if(edit_isbn.getText().toString().equals("")){
            Toast.makeText(getApplicationContext(), "isbn를 입력해주세요",Toast.LENGTH_LONG).show();
            return;
        }

        // status -> 읽고싶은:0, 읽는중:1, 읽음:2
        int status;
        if(category_read_status.getSelectedItem().toString().equals(getString(R.string.read_bucket))){
            // 읽고싶은
            status = 0;
        }else if(category_read_status.getSelectedItem().toString().equals(getString(R.string.read_reading))){
            // 읽는중
            status = 1;
        }else{
            // 읽음
            status = 2;
        }

        InsertData task = new InsertData();
        task.execute(edit_title.getText().toString(), edit_authors.getText().toString()
                , edit_publisher.getText().toString(), edit_isbn.getText().toString()
                ,"0", edit_content.getText().toString()
                , fshared.getPreferenceString("member","login_value")
                ,rating.getRating()+""
                ,status+""
                ,String.valueOf(img_change)
                ,dmb.getUnique_book_value()
                ,dmb.getThumbnail()
        );
        // 타이틀, 작가, 출판사, isbn, 페이지수, 요약정보, 회원, rating, status, img_change, unique_book_value, 기존 썸네일 이밎 주소
    }

    class InsertData extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {

            // 서버주소
            String server_Url = getString(R.string.server_url)+"Book_Edit.php";

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

                    // 텍스트값 : 제목, 지은이, 출판사, isbn, 페이지수, 요약정보, 로그인 정보
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

                    pd.append(twoHyphens+boundary+lineEnd);
                    pd.append("Content-Disposition: form-data; name=\"login_value\""+lineEnd);
                    pd.append(lineEnd);
                    pd.append(strings[6]+lineEnd);

                    pd.append(twoHyphens+boundary+lineEnd);
                    pd.append("Content-Disposition: form-data; name=\"rating\""+lineEnd);
                    pd.append(lineEnd);
                    pd.append(strings[7]+lineEnd);

                    pd.append(twoHyphens+boundary+lineEnd);
                    pd.append("Content-Disposition: form-data; name=\"status\""+lineEnd);
                    pd.append(lineEnd);
                    pd.append(strings[8]+lineEnd);

                    // img_change(이미지 변경여부)
                    pd.append(twoHyphens+boundary+lineEnd);
                    pd.append("Content-Disposition: form-data; name=\"img_change\""+lineEnd);
                    pd.append(lineEnd);
                    pd.append(strings[9]+lineEnd);

                    // unique_book_value
                    pd.append(twoHyphens+boundary+lineEnd);
                    pd.append("Content-Disposition: form-data; name=\"unique_book_value\""+lineEnd);
                    pd.append(lineEnd);
                    pd.append(strings[10]+lineEnd);

                    // 썸네일
                    pd.append(twoHyphens+boundary+lineEnd);
                    pd.append("Content-Disposition: form-data; name=\"thumbnail\""+lineEnd);
                    pd.append(lineEnd);
                    pd.append(strings[11]+lineEnd);


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

            String[] string_array= s.split(getString(R.string.seperator));
            String result = string_array[string_array.length-1];
            Log.d("실행", "result = "+result);

            if(result.equals("success")){
                Toast.makeText(getApplicationContext()
                        , "책수정을 마쳤습니다",Toast.LENGTH_LONG).show();

                // 로그인 페이지로 이동
                finish();
            }else{
                Toast.makeText(getApplicationContext()
                        , "죄송합니다. 문제가 생겼습니다. 다시 시도해주세요",Toast.LENGTH_LONG).show();
            }
        }
    } // InsertData

    public class RetrofitConnection{
        String URL = getString(R.string.server_url);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        JsonPlaceHolderApi server = retrofit.create(JsonPlaceHolderApi.class);
    }

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
                    .start(Activity_Book_Edit.this);
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



    } // end onAcitivtyResult
}
