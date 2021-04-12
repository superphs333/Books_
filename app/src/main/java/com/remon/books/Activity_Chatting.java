package com.remon.books;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.icu.util.Output;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.EditText;

import com.remon.books.Adapter.Adapter_Chatting;
import com.remon.books.Data.Data_Chatting;
import com.remon.books.Function.Function_SharedPreference;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Activity_Chatting extends AppCompatActivity implements View.OnClickListener{

    // 방 idx
    int room_idx;

    // 회원정보
    String login_value;

    // 함수
    Function_SharedPreference fshared;

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

    /*
    리사이클러뷰
     */
    ArrayList<Data_Chatting> arrayList;
    Adapter_Chatting mainAdapter;
    LinearLayoutManager linearLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__chatting);

        room_idx = getIntent().getIntExtra("room_idx",0);
        Log.d("실행", "room_idx="+room_idx);

        // 뷰연결
        edit_chat = findViewById(R.id.edit_chat);
        rv_chatting = findViewById(R.id.rv_chatting);


        // 함수연결
        fshared = new Function_SharedPreference(getApplicationContext());

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
                //gallery_imgs_send();
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



                } // end while
            }catch (Exception e){
                e.printStackTrace();
                Log.d("실행", "에러=>"+e.toString());

            }
        }// end run()
    } // end MessageThread




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
