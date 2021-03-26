package com.remon.books;

import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.remon.books.Adapter.Adapter_Book_Memo;
import com.remon.books.Adapter.Adapter_Img_Memo;
import com.remon.books.Data.Data_Book_Memo;
import com.remon.books.Data.Data_Img_Memo;
import com.remon.books.Function.Function_ImgClass;
import com.remon.books.Function.Function_Set;
import com.remon.books.Function.Function_SharedPreference;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Activity_Edit_Memo extends AppCompatActivity {

    int idx; // 메모 고유값

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
        setContentView(R.layout.activity__edit__memo);

        idx = getIntent().getIntExtra("idx",0);
        Log.d("실행","idx="+idx);



        /*
        뷰연결
         */
        context = getApplicationContext();
        activity = Activity_Edit_Memo.this;
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


        /*
        데이터 불러오기
         */
        Bring_Memo_Data();



    }



    // 메모 데이터 불러오기
    private void Bring_Memo_Data(){
        // 웹페이지 실행하기
        String url = getString(R.string.server_url)+"Data/Get_Memo_One.php";

        StringRequest request = new StringRequest(
                Request.Method.POST,
                url,
                new com.android.volley.Response.Listener<String>() { // 정상 응답
                    @Override
                    public void onResponse(String response) {
                        Log.d("실행","response=>"+response);

                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            Log.d("실행", "object="+jsonArray.get(0).toString());
                            Log.d("실행", "memo="+jsonArray.getJSONObject(0).getString("memo"));

                            /*
                            값셋팅
                             */
                            // 메모
                            edit_memo.setText(jsonArray.getJSONObject(0).getString("memo"));
                            // 페이지
                            edit_page.setText(jsonArray.getJSONObject(0).getString("page"));
                            // open
                            String open =jsonArray.getJSONObject(0).getString("open");
                            if(open.equals("all")){
                                spinner_select_open.setSelection(0);
                            }else if(open.equals("follow")){
                                spinner_select_open.setSelection(1);
                            }else if(open.equals("no")){
                                spinner_select_open.setSelection(2);
                            }
                            // 이미지
                            String[] string_array= jsonArray.getJSONObject(0).getString("img_urls").split("§");
                            for(int i=0; i<string_array.length; i++){
                                Data_Img_Memo DIM = new Data_Img_Memo(string_array[i]);
                                arrayList.add(DIM);
                            }
                            mainAdapter.notifyDataSetChanged();


                        } catch (JSONException e) {
                            e.printStackTrace();
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
                params.put("idx", String.valueOf(idx));
                return params;
            }
        };

        // 요청 객체를 만들었으니 이제 requestQueue 객체에 추가하면 됨.
        // Volley는 이전 결과를 캐싱하므로, 같은 결과가 있으면 그대로 보여줌
        // 하지만 아래 메소드를 false로 set하면 이전 결과가 있더라도 새로 요청해서 응답을 보여줌.
        request.setShouldCache(false);
        AppHelper.requestQueue.add(request);


    } // end Bring_Memo_Data()

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
        sdbm.execute(idx+"",edit_memo.getText().toString(),edit_page.getText().toString(),open,login_value);
            // unique_book_value,메모내용(memo), 페이지(page), 공개여부(open:all,follow,no),작성자
    } // end send_to_SERVER

    class Save_Data_Book_Memo extends AsyncTask<String,Void,String>{

        @Override
        protected String doInBackground(String... strings) {

            String urlString = getString(R.string.server_url)+"Edit_Data_Book_Memo.php";
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

                    // idx
                    wr.writeBytes("\r\n--" + boundary + "\r\n");
                    wr.writeBytes("Content-Disposition: form-data; name=\"idx\"\r\n\r\n");
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


                    /*
                    파일전송
                    - http로 시작하는 경우 => 주소, 순서 전달
                    - 그렇지 않은 경우 => 이미지를 서버에 저장, 순서 전달
                     */
                    ArrayList<String> imgorder = new ArrayList<>();
                    String fileName = "";
                    for(int i=0; i<arrayList.size(); i++){

                        dos = new DataOutputStream(conn.getOutputStream());

                        /*
                        분기 -> 사용자의 기기에서 가져온 이미지라면 => 서버에 저장해야 한다
                         */
                        if(!arrayList.get(i).getImg().substring(0,4).equals("http")){
                            String a = String.valueOf(i);
                            FileInputStream fileInputStream
                                    = new FileInputStream(arrayList.get(i).getImg());

                            Random generator = new Random();
                            int n = 1000000;
                            n = generator.nextInt(n);
                            fileName = "Img_Book_Memo-"+n+".jpg";

                            dos.writeBytes(lineEnd);
                            dos.writeBytes(twoHyphens + boundary + lineEnd);
                            dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file"+a+"\";filename=\""+ fileName + "\"" + lineEnd);
                            dos.writeBytes(lineEnd);

                            // create a buffer of maximum size
                            bytesAvailable = fileInputStream.available();
                            bufferSize = Math.min(bytesAvailable, maxBufferSize);
                            buffer = new byte[bufferSize];
                            bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                            while(bytesRead>0){
                                dos.write(buffer, 0, bufferSize);
                                bytesAvailable = fileInputStream.available();
                                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                            }
                            fileInputStream.close();
                            dos.writeBytes(lineEnd);
                            dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                            imgorder.add(getString(R.string.server_url)+"Img_Book_Memo/"+fileName);
                        }else{
                            fileName = arrayList.get(i).getImg();
                            imgorder.add(fileName);
                        }
                    } // end for

                    // 이미지 주소 뽑아내기 : 배열 -> string화
                    String img_order_string = "";
                    for(int i=0; i<imgorder.size(); i++){
                        img_order_string += imgorder.get(i);
                        if(i!=imgorder.size()-1){ // 마지막이 아니면
                            img_order_string += ",";
                        }
                    }
                    Log.d("실행","img_order_string="+img_order_string);

                    // 이미지 string 전송하기(이미지 순서)
                    dos = new DataOutputStream(conn.getOutputStream());
                    dos.writeBytes("\r\n--" + boundary + "\r\n");
                    dos.writeBytes("Content-Disposition: form-data; name=\"img_order_string\"\r\n\r\n" + img_order_string);
                    dos.writeBytes("\r\n--" + boundary + "\r\n");
                    dos.flush();



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

    public class RetrofitConnection{
        String URL = getString(R.string.server_url);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        JsonPlaceHolderApi server = retrofit.create(JsonPlaceHolderApi.class);
    }
}
