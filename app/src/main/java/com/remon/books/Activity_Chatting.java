package com.remon.books;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ClipData;
import android.content.Intent;
import android.icu.util.Output;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.remon.books.Adapter.Adapter_Chatting;
import com.remon.books.Data.Data_Chatting;
import com.remon.books.Function.Function_SharedPreference;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Activity_Chatting extends AppCompatActivity implements View.OnClickListener{

    // 방 idx
    int room_idx;

    // 방 타이틀
    String title;

    // 회원정보
    String login_value;

    // 함수
    Function_SharedPreference fshared;
    GetUri getUri;
        // 절대경로 받아오기

    // 이미지 주소
    String image_Uri_String;


    /*
    소켓관련
     */
    // 어플 종료시 스레드 중지 위해
    boolean isRunning = false;
    // 서버와 연결되어 있는 소켓 객체
    Socket member_socket;

    // 뷰변수
    RecyclerView rv_chatting;
    EditText edit_chat;
    TextView txt_title;

    /*
    리사이클러뷰
     */
    ArrayList<Data_Chatting> arrayList;
    Adapter_Chatting mainAdapter;
    LinearLayoutManager linearLayoutManager;

    int PICK_FROM_GALLERY_MULTI = 124;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__chatting);

        room_idx = getIntent().getIntExtra("room_idx",0);
        Log.d("실행", "room_idx="+room_idx);
        title = getIntent().getStringExtra("title");

        // 뷰연결
        txt_title = findViewById(R.id.txt_title);
        edit_chat = findViewById(R.id.edit_chat);
        rv_chatting = findViewById(R.id.rv_chatting);

        // 뷰셋팅
        txt_title.setText(title);


        // 함수연결
        fshared = new Function_SharedPreference(getApplicationContext());
        getUri = new GetUri();

        // 변수셋팅
        login_value = fshared.get_login_value();

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);



        // 서버와 연결(접속 스레드 가동)
        ConnectionThread thread = new ConnectionThread();
        thread.start();

    }

    @Override
    protected void onResume() {
        super.onResume();

        // 채팅 데이터 불러오기
        RetrofitConnection retrofitConnection
                = new RetrofitConnection();
        Call<ArrayList<Data_Chatting>> call
                = retrofitConnection.server.Get_Chatting(room_idx);
        call.enqueue(new Callback<ArrayList<Data_Chatting>>() {
            @Override
            public void onResponse(Call<ArrayList<Data_Chatting>> call, Response<ArrayList<Data_Chatting>> response) {
                if(response.isSuccessful()){
                    Log.d("실행", String.valueOf(response.code()));

                    arrayList = response.body();

                    /*
                    리사이클러뷰 셋팅
                    */
                    mainAdapter = new Adapter_Chatting(arrayList,getApplicationContext(),login_value); // 어댑터
                    rv_chatting.setAdapter(mainAdapter);
                    linearLayoutManager = new LinearLayoutManager(getApplicationContext());
                    linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                    rv_chatting.setLayoutManager(linearLayoutManager);
                    rv_chatting.scrollToPosition(mainAdapter.getItemCount()-1);


                }else{
                    Log.d("실행","서버에 연결은 되었으나 오류발생");
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Data_Chatting>> call, Throwable t) {
                Log.d("실행", "onFailure: " + t.toString()); //서버와 연결 실패
            }
        });
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.btn_send : // 메세지 전송
                Log.d("실행", "메세지 전송");

                // 사용자가 작성한 값
                String msg = edit_chat.getText().toString();

                // 서버에 데이터를 전달하는 스레드
                Send_To_Server_Thread thread
                        = new Send_To_Server_Thread(msg);
                thread.start();
                break;
            case R.id.btn_plus : // 이미지 전송
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                //startActivityForResult(intent.createChooser(intent,""), PICK_FROM_GALLERY_MULTI);
                startActivityForResult(intent,PICK_FROM_GALLERY_MULTI);
        }
    } // end onClick


    /*
    서버접속 처리하는 스레드 클래스
    -> 서버와 연결하고, 서버와 자신 클라이언트 사이에 스트림을 구성해 놓기
     */
    class ConnectionThread extends Thread{
        public void run(){
            try {
                Socket socket = new Socket("3.36.33.242",2345);
                member_socket = socket; // 클라이언트 소켓 셋팅
                Log.d("실행","Socket성공");

                /*
                스트림을 추출한다(데이터를 서버에 보내기 위해)
                 */
                OutputStream os = socket.getOutputStream();
                    // 출력스트림을  socket.getOutputStream()으로 부터 얻어옴
                DataOutputStream dos = new DataOutputStream(os);
                    /* DataOutputStream
                    = outputstream을 인자로 받아들이고,
                    자바의 기본형 데이터(int, float..)의 정보를 입력하고
                    출력하는 데 알맞는 클래스
                     */

                // 방 idx 송신하기
                dos.writeUTF(room_idx+"");
                    /*  DataOutput.writeUTF(String s)
                    = UTF-8인코딩을 사용해서 문자열을 출력한다다
                    */
                dos.flush();

                // 이메일 송신하기
                dos.writeUTF(login_value);
                dos.flush();

                // 닉네임 송신하기
                dos.writeUTF(fshared.get_nickname());
                dos.flush();

                // 메세지를 받아들이는 스레드 시작
                isRunning = true;
                MessageThread thread
                        = new MessageThread(socket);
                thread.start();


            } catch (IOException e) {
                e.printStackTrace();
                Log.d("실행","Socket에러-"+e.getMessage());
            }
        } // end run
    } // end ConnectionThread

    /*
    메세지 작업을 처리하는 스레드
    : 전달받은 메세지를 계속(while) 불러들인다
     */
    class MessageThread extends Thread{
        Socket socket;
        DataInputStream dis;

        // 생성자
        public MessageThread(Socket socket){
            // socket에서 InputStream받아오기
            try {
                InputStream is = socket.getInputStream();
                dis = new DataInputStream(is);
                Log.d("실행","InputStream성공");
            } catch (IOException e) {
                e.printStackTrace();
                Log.d("실행","InputStream에러-"+e.getMessage());
            }
        }// end 생성자

        // 실행코드
        @Override
        public void run() {
            try{
                Log.d("실행","MessageThread run!!");

                /*
                메세지가 들어올 때마다 실행되어야 하므로
                -> while무한루프를 통해 계속 대기상태로 둔다
                 */
                while(isRunning){
                    // 서버로부터 계속 데이터를 수신받는다

                    // 서버로 부터 받은 메세지
                    final String msg = dis.readUTF();
                    Log.d("실행","서버로부터 받은 메세지->"+msg);

                    /*
                    메세지 분기
                     */
                    String[] string_array= msg.split("§");
                    //Log.d("실행", "string_array.size="+string_array.length);

                    String sort = string_array[0]; // 분류
                    int idx = 0; // 값이 없는 경우 (분기 -> 값이 없을 때 parseInt시 오류
                    if(!string_array[1].equals("")){
                        idx = Integer.parseInt(string_array[1]); // idx
                    }
                    String login_value = string_array[2]; // login_value
                    String nickname = string_array[3]; // nickname
                    String profile_url = string_array[4]; // profile_url
                    String content = string_array[5]; // content
                    String time = string_array[6]; // time

                    Data_Chatting dc = null;
                    if(sort.equals("notice")){ // notice(날짜)
                        Log.d("실행", "notice");
                        dc = new Data_Chatting(sort, content);
                    }else if(sort.equals("message")){
                        dc = new Data_Chatting(idx,room_idx,login_value,nickname,profile_url,sort,content,time,null);
                    }else if(sort.equals("file")){
                        dc = new Data_Chatting(idx,room_idx,login_value,nickname,profile_url,sort,content,time,null);
                    }else if(sort.equals("files")){
                        dc = new Data_Chatting(idx,room_idx,login_value,nickname,profile_url,sort,content,time,string_array[7]);
                    }

                    arrayList.add(dc);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.d("실행", "어댑터 반영");

                            rv_chatting.scrollToPosition(mainAdapter.getItemCount()-1);
                            mainAdapter.notifyDataSetChanged();
                        }
                    }); // end runOnUiThread

                    Log.d("실행", "arraylist_size="+arrayList.size());


                    // 알람보내기
                    if(string_array.length==7){
                        alarm_for_joiner(sort,nickname,content,login_value);
                    }else if(string_array.length==8){
                        if(string_array[7].equals("end")){
                            alarm_for_joiner(sort,nickname,content,login_value);
                        }
                    }




                } // end while
            }catch (Exception e){
                e.printStackTrace();
                Log.d("실행", "에러=>"+e.toString());

            }
        }// end run()
    } // end MessageThread


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK && requestCode==PICK_FROM_GALLERY_MULTI){
            if(data==null){
                // data가 null일 때는 앨범에서 뒤로가기 눌렀을 때
                // data가 없기 때문에 생기는 오류를 잡아주기 위함
            }else{
                if(data.getClipData()==null){ // 이미지 한 장
                    Log.d("실행", "이미지 한장");

                    image_Uri_String = getUri.getPath(getApplicationContext(),data.getData());
                    Log.d("실행", "image_Uri_String="+image_Uri_String);


                    // 이미지를 서버에 전송하기
                    File_Sender fileSender = new File_Sender(image_Uri_String);
                    fileSender.start();

                }else{ // 이미지 여러장
                    Log.d("실행", "이미지 여러장");

                    ClipData clipData = data.getClipData();

                    // 이미지 경로 저장 list
                    List<String> img_list = new ArrayList<>();

                    for(int i=0; i<clipData.getItemCount(); i++){
                        img_list.add(getUri.getPath(getApplicationContext(),clipData.getItemAt(i).getUri()));
                    } // end for

                    // 이미지들을 서버에 전송하기
                    Files_Sender sender = new Files_Sender(img_list);
                    sender.start();
                }
            }
        }
    } // end onActivityResult

    /*
    서버에 데이터를 전송하는 스레드
     */
    class Send_To_Server_Thread extends Thread{
        String msg; // 보낼 메세지
        DataOutputStream dos; // 데이터를 보낼 stream

        public Send_To_Server_Thread(String msg){
            this.msg = msg;

            try {
                OutputStream os = member_socket.getOutputStream();
                dos = new DataOutputStream(os);
            } catch (IOException e) {
                Log.d("실행","OutputStream 오류-"+e.getMessage());
                e.printStackTrace();
            }
        } // end 생성자

        @Override
        public void run() {
            try{
                /*
                서버를 데이터로 보낸다
                : 전달받은 메세지를 dos.writeUTF(msg)를 통해 서버로
                데이터를 보내주기만 하면 된다
                 */
                dos.writeUTF("message");
                dos.flush();
                dos.writeUTF(msg);
                dos.flush();

                // edit부분을 비워준다
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("실행", "setText 비움");

                        edit_chat.setText("");
                    }
                }); // end runOnUiThread

            }catch (Exception e){
                Log.d("실행","OutputStream 오류-"+e.getMessage());
                e.printStackTrace();
            }
        } // end run
    }// end Send_To_Server_Thread

    /*
    서버에 파일을 전송하는 스레드
     */
    class File_Sender extends Thread{

        String file_Path; // 파일경로
        String file_Nm; // 파일 이름
        DataOutputStream dos;
        FileInputStream fis;
        BufferedInputStream bis;

        // 생성자
        public File_Sender(String file_Path){
            this.file_Path = file_Path;

            // 파일명
            Random generator = new Random();
            int n = 1000000;
            n = generator.nextInt(n);
            file_Nm = "Chat_image-"+n+".jpg";

            try{
                // 데이터 전송용 스트림 생성
                dos = new DataOutputStream(member_socket.getOutputStream());
            }catch (IOException e) {
                Log.d("실행","DataOutputStream에러-"+e.getMessage());
                e.printStackTrace();
            }
        } // end 생성자

        @Override
        public void run() {
            try {
                // 파일 전송을 할 것이라는 것을 서버에 알린다
                dos.writeUTF("file");
                dos.flush();

                // 전송 할 파일을 읽어서 Socket Server에 전송
                String result  = fileRead();
                Log.d("실행","result:"+result);
            } catch (IOException e) {
                Log.d("실행","dos.writeUTF에러-"+e.getMessage());
                e.printStackTrace();
            }
        }
//
        // 파일을 전송하는 함수
        private String fileRead(){
            String result="";

            try {

                // 파일명
                Random generator = new Random();
                int n = 1000000;
                n = generator.nextInt(n);
                String file_Nm = "Image-"+n+".jpg";


                dos.writeUTF(file_Nm);
                Log.d("실행","파일 이름(" + file_Nm + ")을 전송하였습니다.");

                /*
                파일을 읽어서 서버에 전송
                 */
                File file = new File(image_Uri_String);

                // 파일 사이즈 보내기(얼만큼 보낼 것인지 알려주기)
                dos.writeUTF(file.length()+"");
                dos.flush();

                fis = new FileInputStream(file); // 파일에서 데이터를 읽기
                bis = new BufferedInputStream(fis); //FileInputStream보다 더 효율적으로 입출력 위해

                int len;
                int size = 4096;
                byte[] data = new byte[size];
                while((len=bis.read(data)) != -1){
                    dos.write(data,0,len);
                    dos.flush();
                }

                // 서버에 전송(서버로 보내기 위해서 flush를 사용)
                dos.flush();
                result = "SUCCESS";

            } catch (IOException e) {
                Log.d("실행","dos.writeUTF에러-"+e.getMessage());
                e.printStackTrace();
                result = "ERROR";
            }finally {
                try { bis.close(); } catch (IOException e) { e.printStackTrace(); }
            }


            return result;
        } // end fileRead()
    } // end File_Sender


    // 여러 파일 전송
    class Files_Sender extends Thread{

        List<String> imgs_list;
        DataOutputStream dos;
        FileInputStream fis;
        BufferedInputStream bis;

        // 생성자
        public Files_Sender(List<String> imgs_list){
            this.imgs_list = imgs_list;

            try {
                // 데이터 전송용 스트림 생성
                dos = new DataOutputStream(member_socket.getOutputStream());
            } catch (IOException e) {
                Log.d("실행","DataOutputStream에러-"+e.getMessage());
                e.printStackTrace();
            }
        }// end 생성자

        // 실행코드
        @Override
        public void run() {
            for(int i=0; i<imgs_list.size(); i++){
                try {
                    sleep(300);
                } catch (InterruptedException e) {e.printStackTrace();}

                Log.d("실행", "=====i="+i+"시작=====");

                try{
                  // 파일 전송을 할 것이라는 것을 서버에 알린다
                    dos.writeUTF("files");
                    dos.flush();

                    /*
                    order_tag를 보낸다
                    - 맨 첫번째 => first
                    - 중간 => center
                    - 맨 마지막 => end
                     */
                    if(i==0){ // 맨 처음
                        dos.writeUTF("first");
                    }else if(i==imgs_list.size()-1){  // 맨 마지막
                        dos.writeUTF("end");
                    }else{  // 나머지 = 센터
                        dos.writeUTF("center");
                    }
                    dos.flush();

                    // 전송할 파일을 읽어서 Server에 전송
                    String result = fileRead(dos,imgs_list.get(i));
                }catch(Exception e){
                  e.printStackTrace();
                  Log.d("실행","dos.writeUTF에러-"+e.getMessage());
                }finally {
                    //  리소스 초기화
                    try {
                        fis.close();
                    } catch (IOException e) {
                        Log.d("실행","bis.close()에러-"+e.getMessage());
                        e.printStackTrace();
                    }
                }

            } // end for
        } // end run

        private String fileRead(DataOutputStream dos,String file_path){
            String result="";

            try {
                // 파일명
                Random generator = new Random();
                int n = 1000000;
                n = generator.nextInt(n);
                String file_Nm = "Image-"+n+".jpg";

                dos.writeUTF(file_Nm);
                Log.d("실행","파일 이름(" + file_Nm + ")을 전송하였습니다.");

                /*
                파일을 읽어서 서버에 전송
                 */
                File file = new File(file_path);

                // 파일 사이즈 보내기(얼만큼 보낼 것인지 알려주기)
                dos.writeUTF(file.length()+"");
                dos.flush();

                fis = new FileInputStream(file); // 파일에서 데이터를 읽기
                bis = new BufferedInputStream(fis); //FileInputStream보다 더 효율적으로 입출력 위해

                int len;
                int size = 4096;
                byte[] data = new byte[size];
                while((len=bis.read(data)) != -1){
                    dos.write(data,0,len);
                    dos.flush();
                }

                // 서버에 전송(서버로 보내기 위해서 flush를 사용)
                dos.flush();
                result = "SUCCESS";

            } catch (IOException e) {
                Log.d("실행","dos.writeUTF에러-"+e.getMessage());
                e.printStackTrace();
                result = "ERROR";
            }finally {
                Log.d("실행", "bis, fis close");
                try { bis.close(); } catch (IOException e) { e.printStackTrace(); }
                try {
                    fis.close();
                } catch (IOException e) {
                    Log.d("실행","bis.close()에러-"+e.getMessage());
                    e.printStackTrace();
                }
            }

            return result;
        } // end fileRead()
    } // end Files_Sender

    private String fileRead(DataOutputStream dos,FileInputStream fis, BufferedInputStream bis){
        String result="";

        try {

            // 파일명
            Random generator = new Random();
            int n = 1000000;
            n = generator.nextInt(n);
            String file_Nm = "Image-"+n+".jpg";

            dos.writeUTF(file_Nm);
            Log.d("실행","파일 이름(" + file_Nm + ")을 전송하였습니다.");

            /*
            파일을 읽어서 서버에 전송
             */
            File file = new File(image_Uri_String);

            // 파일 사이즈 보내기(얼만큼 보낼 것인지 알려주기)
            dos.writeUTF(file.length()+"");
            dos.flush();

            fis = new FileInputStream(file); // 파일에서 데이터를 읽기
            bis = new BufferedInputStream(fis); //FileInputStream보다 더 효율적으로 입출력 위해

            int len;
            int size = 4096;
            byte[] data = new byte[size];
            while((len=bis.read(data)) != -1){
                dos.write(data,0,len);
                dos.flush();
            }

            // 서버에 전송(서버로 보내기 위해서 flush를 사용)
            dos.flush();
            result = "SUCCESS";

        } catch (IOException e) {
            Log.d("실행","dos.writeUTF에러-"+e.getMessage());
            e.printStackTrace();
            result = "ERROR";
        }

        return result;
    } // end fileRead()

    // 알람전송
        // sort, login_value,nickname,content
    private void alarm_for_joiner(final String sort, final String nickname, final String content, final String writer){
        // 실행할 웹페이지
        String url = getString(R.string.server_url)+"alarm_for_chatting.php";

        StringRequest request = new StringRequest(
                Request.Method.POST,
                url,
                new com.android.volley.Response.Listener<String>() { // 정상 응답
                    @Override
                    public void onResponse(String response) {
                        Log.d("실행","response=>"+response);

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
                params.put("room_idx",room_idx+""); // 모임 idx
                params.put("nickname", nickname); // 닉네임
                params.put("sort", sort); // 분류
                params.put("content", content); // 내용
                params.put("title",title);
                params.put("writer",writer); // 메세지 작성자
                return params;
            }
        };

        // 요청 객체를 만들었으니 이제 requestQueue 객체에 추가하면 됨.
        // Volley는 이전 결과를 캐싱하므로, 같은 결과가 있으면 그대로 보여줌
        // 하지만 아래 메소드를 false로 set하면 이전 결과가 있더라도 새로 요청해서 응답을 보여줌.
        request.setShouldCache(false);
        AppHelper.requestQueue.add(request);

    }


    // 화면에 꺼지면 -> 채팅방 나가기(socket.close)
    @Override
    protected void onDestroy() {
        super.onDestroy();

        try{
            member_socket.close();
            isRunning=false;

        }catch (Exception e){
            e.printStackTrace();
        }
    } // end onDestroy

    public class RetrofitConnection{
        String URL = getString(R.string.server_url);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        JsonPlaceHolderApi server = retrofit.create(JsonPlaceHolderApi.class);
    }
}
