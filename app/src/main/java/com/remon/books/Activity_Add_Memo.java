package com.remon.books;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.remon.books.Adapter.Adapter_Img_Memo;
import com.remon.books.Data.Data_Img_Memo;
import com.remon.books.Function.Function_ImgClass;
import com.remon.books.Function.Function_Set;
import com.remon.books.Function.Function_SharedPreference;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;

public class Activity_Add_Memo extends AppCompatActivity {

    String unique_book_value; // 책 고유값

    /*
    함수
     */
    Function_Set fs;
    Function_ImgClass fi;
    Function_SharedPreference fshared;
    /*
    뷰연결
     */
    Context context;
    Activity activity;
    RecyclerView rv_memo_img;
    TextView txt_title;
    Button btn_camera, btn_gallery;
    EditText edit_memo,edit_page;
    Spinner spinner_select_open;

    /*
    리사이클러뷰
    */
    private Adapter_Img_Memo mainAdapter;
    private LinearLayoutManager linearLayoutManager;
    private ArrayList<Data_Img_Memo> arrayList;
    ItemTouchHelper helper; // 스와이프, 이동

    /*
    이미지용 변수
     */
    String image_Uri_String;
    Bitmap image_bitmap;
    int REQUEST_IMAGE_CAPTURE_VALUE = 356; // 카메라에서
    int PICK_FROM_GALLERY_VALUE = 123; // 갤러리에서


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__add__memo);

        unique_book_value = getIntent().getStringExtra("unique_book_value");
        Log.d("실행","unique_book_value="+unique_book_value);



        /*
        뷰연결
         */
        context = getApplicationContext();
        activity = Activity_Add_Memo.this;
        rv_memo_img = findViewById(R.id.rv_memo_imgs);
        txt_title = findViewById(R.id.txt_title);
        btn_camera = findViewById(R.id.btn_camera);
        btn_gallery = findViewById(R.id.btn_gallery);
        edit_memo = findViewById(R.id.edit_memo);
        edit_page = findViewById(R.id.edit_page);
        spinner_select_open = findViewById(R.id.spinner_select_open);

        // 함수셋팅
        fs = new Function_Set(context);
        fs.context = context;
        fs.activity = activity;
        fi = new Function_ImgClass();
        fshared = new Function_SharedPreference(context);

        // 제목셋팅
        txt_title.setText(getIntent().getStringExtra("title"));

        /*
        spinner셋팅
         */
        final String[] data = getResources().getStringArray(R.array.select_open);
        ArrayAdapter<String> adapter
                = new ArrayAdapter<String>(context,android.R.layout.simple_dropdown_item_1line,data);
        spinner_select_open.setAdapter(adapter);

        /*
        리사이클러뷰 셋팅
         */
        arrayList = new ArrayList<>();
        mainAdapter = new Adapter_Img_Memo(arrayList,context,activity);
        rv_memo_img.setAdapter(mainAdapter);
        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rv_memo_img.setLayoutManager(linearLayoutManager);
        helper = new ItemTouchHelper(new ItemTouchHelperCallback(mainAdapter));
        helper.attachToRecyclerView(rv_memo_img);





    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // 카메라에서 이미지 가져오기
        if(requestCode == REQUEST_IMAGE_CAPTURE_VALUE && resultCode == RESULT_OK){
            Uri original_url = Uri.parse(image_Uri_String);
            Uri return_uri = null;
            if(original_url.getScheme()==null){
                return_uri = Uri.fromFile(new File(image_Uri_String));
            }else{
                return_uri = original_url;
            }

            // 이미지 크롭하기
            CropImage.activity(return_uri).setGuidelines(CropImageView.Guidelines.ON).start(activity);
        }

        // 갤러리에서 이미지 가져오기
        if(requestCode==PICK_FROM_GALLERY_VALUE
                && resultCode==RESULT_OK)
        {
            if(data==null){
                // data==null일때는 => 앨범에서 뒤로가기 눌렀을 때
                // data가 없기 때문에 생기는 오류를 잡아주기 위함
            }else{
                if(data.getClipData()==null){
                    // 이미지 한 장
                    CropImage.activity(data.getData()).setGuidelines(CropImageView.Guidelines.ON).start(activity);
                }else{
                    // 이미지 여러장
                    ClipData clipData = data.getClipData();
                    Log.d("실행","이미지 갯수="+String.valueOf(clipData.getItemCount()));

                    for(int i=0; i<clipData.getItemCount(); i++){
                        CropImage.activity(clipData.getItemAt(i).getUri()).start(activity);
                    } // end for
                }
            }
        } // end requestCode==PICK_FROM_GALLERY_VALUE

        // 이미지 크롭
        if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode==RESULT_OK){
            image_Uri_String = CropImage.getActivityResult(data).getUri().getPath();

            // 비트맵 방향 변경
            String temp = String.valueOf(image_Uri_String);
            image_bitmap = fs.rotate_from(temp);

            // 이미지를 arraylist에 추가
            Data_Img_Memo dim = new Data_Img_Memo(image_Uri_String);
            arrayList.add(dim);
            mainAdapter.notifyDataSetChanged();
        }

        // 편집된 이미지 가져오기
        if(requestCode==999 && resultCode==RESULT_OK){
            Log.d("실행", "(in onActivityResult)result="+data.getStringExtra("result"));
            Log.d("실행", "(in onActivityResult)result="+data.getStringExtra("position"));

            // 절대경로로 변경해야 함
            String url = data.getStringExtra("result");
            Log.d("실행", "url="+url);

            // 해당 이미지 변경하기
            arrayList.get(Integer.parseInt(data.getStringExtra("position"))).setImg(url);
            mainAdapter.notifyDataSetChanged();
        }
    }

    // 카메라(btn_camera) => 카메라에서 이미지 가져오기
    public void Pick_From_Camera(View view) {
        image_Uri_String = fs.camera_for_profile(REQUEST_IMAGE_CAPTURE_VALUE);
    }

    public void Pick_From_Gallery(View view) {
        fs.pick_from_gallery_imgs(PICK_FROM_GALLERY_VALUE);
    }

    // 메모 내용 서버로 전송하기
    public void send_to_SERVER(View view) {
        // 선택된 스피너값
        String select_spinner = spinner_select_open.getSelectedItem().toString();
        String open;
        if(select_spinner.equals("전체")){
            open = "all";
        }else if(select_spinner.equals("팔로잉")){
            open = "follow";
        }else{ // 전체
            open = "no";
        }

        // 작성자
        String login_value = fshared.getPreferenceString(getString(R.string.member),getString(R.string.login_value));

        Save_Data_Book_Memo sdbm = new Save_Data_Book_Memo();
        sdbm.execute(unique_book_value,edit_memo.getText().toString(),edit_page.getText().toString(),open,login_value);
            // unique_book_value,메모내용(memo), 페이지(page), 공개여부(open:all,follow,no),작성자
    } // end send_to_SERVER

    class Save_Data_Book_Memo extends AsyncTask<String,Void,String>{

        @Override
        protected String doInBackground(String... strings) {

            String urlString = getString(R.string.server_url)+"Add_Data_Book_Memo.php";
            HttpURLConnection conn = null;
            DataOutputStream dos = null;
            String lineEnd = "\r\n";
            String twoHyphens = "--";
            String boundary = "*****";
            int bytesRead, bytesAvailable, bufferSize;
            byte[] buffer;
            int maxBufferSize = 1 * 1024 * 1024;
            try {
                URL url = new URL(urlString);

                try {
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setDoInput(true); // Allow Inputs
                    conn.setDoOutput(true); // Allow Outputs
                    conn.setUseCaches(false); // Don't use a Cached Copy
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Accept-Charset", "utf-8");
                    conn.setRequestProperty("Connection", "Keep-Alive");
                    conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                    conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);

                    DataOutputStream wr = new DataOutputStream(conn.getOutputStream());

                    /*
                    텍스트 데이터
                    : unique_book_value,메모내용(memo), 페이지(page), 공개여부(open:all,follow,no)
                     */
                    // arraylist 크기(얼마나 for을 돌릴건지 알기 위해)
                    wr.writeBytes("\r\n--" + boundary + "\r\n");
                    wr.writeBytes("Content-Disposition: form-data; name=\"size\"\r\n\r\n" + arrayList.size());
                    wr.writeBytes("\r\n--" + boundary + "\r\n");

                    // unique_book_value
                    wr.writeBytes("\r\n--" + boundary + "\r\n");
                    wr.writeBytes("Content-Disposition: form-data; name=\"unique_book_value\"\r\n\r\n");
                    wr.write(strings[0].getBytes("utf-8"));
                    wr.writeBytes("\r\n--" + boundary + "\r\n");

                    // 메모내용(memo)
                    wr.writeBytes("\r\n--" + boundary + "\r\n");
                    wr.writeBytes("Content-Disposition: form-data; name=\"memo\"\r\n\r\n");
                    wr.write(strings[1].getBytes("utf-8"));
                    wr.writeBytes("\r\n--" + boundary + "\r\n");

                    // 페이지(page)
                    wr.writeBytes("\r\n--" + boundary + "\r\n");
                    wr.writeBytes("Content-Disposition: form-data; name=\"page\"\r\n\r\n");
                    wr.write(strings[2].getBytes("utf-8"));
                    wr.writeBytes("\r\n--" + boundary + "\r\n");

                    // 공개여부(open)
                    wr.writeBytes("\r\n--" + boundary + "\r\n");
                    wr.writeBytes("Content-Disposition: form-data; name=\"open\"\r\n\r\n");
                    wr.write(strings[3].getBytes("utf-8"));
                    wr.writeBytes("\r\n--" + boundary + "\r\n");

                    // 작성자
                    wr.writeBytes("\r\n--" + boundary + "\r\n");
                    wr.writeBytes("Content-Disposition: form-data; name=\"login_value\"\r\n\r\n");
                    wr.write(strings[4].getBytes("utf-8"));
                    wr.writeBytes("\r\n--" + boundary + "\r\n");

                    // 파일전송
                    for(int i=0; i<arrayList.size(); i++){

                        FileInputStream fileInputStream = null;
                        
                        try{
                            fileInputStream
                                    = new FileInputStream(arrayList.get(i).getImg());
                            Log.d("실행", "FileInputStream성공->"+i);
                        }catch (Exception e){
                            Log.d("실행", "FileInputStream에러=>"+arrayList.get(i).getImg()+","+e.getMessage());
                        }
                        /*
                        여러파일을 전송할 때 주의사항
                        -> uploaded_file은 키값으로 들어갈 것이기 때문에,
                        중복되면 마지막 데이터만 전송이 된다
                            => i값을 string으로 변환하여 구분해준다(php단에서 구분지어 받는다)
                         */
                        dos = new DataOutputStream(conn.getOutputStream());
                        dos.writeBytes(lineEnd);
                        dos.writeBytes(twoHyphens + boundary + lineEnd);

                        // 파일이름
                        Random generator = new Random();
                        int n = 1000000;
                        n = generator.nextInt(n);
                        String fileName = "Img_Book_Memo-"+n+".jpg";

                        // 키값 전송
                        String a= String.valueOf(i);
                        dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file"+a+"\";filename=\""+ fileName + "\"" + lineEnd);
                        dos.writeBytes(lineEnd);

                        // create a buffer of maximum size
                        bytesAvailable = fileInputStream.available();
                        bufferSize = Math.min(bytesAvailable, maxBufferSize);
                        buffer = new byte[bufferSize];
                        bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                        while (bytesRead > 0) {
                            dos.write(buffer, 0, bufferSize);
                            bytesAvailable = fileInputStream.available();
                            bufferSize = Math.min(bytesAvailable, maxBufferSize);
                            bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                        }
                        fileInputStream.close();
                        dos.writeBytes(lineEnd);
                        dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
                        dos.flush();
                    } // end for


                    /*
                    서버에서 전송받기
                     */
                    int responseStatusCode
                            = conn.getResponseCode();
                    Log.d("실행", "POST response code - " + responseStatusCode);

                    // inputStream을 통해 데이터를 받아온다
                    InputStream inputStream;
                    if(responseStatusCode==HttpURLConnection.HTTP_OK){
                        // 정상적인 응답 데이터 인 경우
                        inputStream = conn.getInputStream();
                    }else{
                        // 에러 발생
                        inputStream = conn.getErrorStream();
                    }

                    /*
                    StringBuilder를 사용하여 수신되는 데이터를 저장한다
                     */
                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                    StringBuilder sb = new StringBuilder();
                    String line = null;
                    while((line = bufferedReader.readLine()) != null){
                        sb.append(line);
                    }
                    bufferedReader.close();

                    // 저장 된 데이터를 스트링으로 변환하여 리턴한다
                    return sb.toString();
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.d("실행", "conn=>"+e.getMessage());
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
                Log.d("실행", "URL에러=>"+e.getMessage());
            }


            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.d("실행", "POST response  - " + s);

            if(s.equals("success")){ // 성공시
                finish();
            }else{ // 실패시
                Toast.makeText(getApplicationContext(), "죄송합니다. 오류가 발생하였습니다. 다시 시도해주세요",Toast.LENGTH_LONG).show();
            }
        }
    } // end Save_Data_Book_Memo
}
