package com.remon.books;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.remon.books.Function.Function_Set;
import com.remon.books.Function.Function_SharedPreference;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;


/*
기기 등록 토큰 액세스
: FCM SDK는 앱을 처음 시작할 때 클라이언트 앱 인스턴스용 등록 토큰을 생성한다
=> 단일 기기를 타겟팅하거나 기기 그룹을 만드려면
=> FirebaseMessagingService를 확장하고, onNewToken을 재정의하여
이 토큰에 액세스해야 한다.
    - 토큰은 최초 시작 후에 순환될 수 있으므로, 마지막으로 업데이트 된 등록토큰을 가져오는 것이 좋다
        - 등록 토큰이 변경도리 수 있는 경우
            - 앱에서 인스턴스 id 삭제
            - 새 기기에서 앱 복원
            - 사용자가 앱 삭제/재설치
            - 사용자가 앱 데이터 소거
 */

// 토큰이 확보되었다면, 앱서버로 전송하고 원하는 방법으로 저장 할 수 있다
public class MyFirebaseMessagingService extends FirebaseMessagingService {

    // 이메일
    Function_SharedPreference fshared;



    // 새 토큰이 생성될 때마다 onNewToken콜백이 호출된다
    @Override
    public void onNewToken(@NonNull final String s) {
        super.onNewToken(s);
        Log.d("실행", "onNewToken함수 호출");
        Log.d("실행", "Refreshed token: " + s);

        fshared = new Function_SharedPreference(getApplicationContext());

        final String login_value = fshared.get_login_value();

        // 웹페이지 실행하기
        String url = getString(R.string.server_url)+"Update_sender_id.php";

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
                params.put("login_value",login_value);
                params.put("sender_id",s);
                return params;
            }
        };

        // 요청 객체를 만들었으니 이제 requestQueue 객체에 추가하면 됨.
        // Volley는 이전 결과를 캐싱하므로, 같은 결과가 있으면 그대로 보여줌
        // 하지만 아래 메소드를 false로 set하면 이전 결과가 있더라도 새로 요청해서 응답을 보여줌.
        request.setShouldCache(false);
        AppHelper.requestQueue.add(request);


    }

    // 메세지를 받았을 때 동작하는 메서드
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.d("실행", "onMessageReceived함수 호출");

        fshared = new Function_SharedPreference(getApplicationContext());

        final String login_value = fshared.get_login_value();

        // 데이터 메세지를 받는다
            // 데이터 받을 때 => remoteMessage.getData().get("key")
        if(remoteMessage.getData().size()>0){
            Log.d("실행", "remoteMessage.getData().size()>0");

            String input = remoteMessage.getData().toString();
            Log.d("실행", "input="+input);

            String sort = remoteMessage.getData().get("sort");
            Log.d("실행", "sort="+sort);

            String title = remoteMessage.getData().get("title");
            String message = remoteMessage.getData().get("message");


            if(sort.equals("For_chatting_room_waiting_list")){
                showNotification("For_chatting_room_waiting_list",remoteMessage.getData().get("idx"),title,message);
            }else if(sort.equals("For_Follow")){
                showNotification("For_Follow","login_value",title,message);
            }else if(sort.equals("For_memo_like")){
                showNotification("For_memo_like","3",title,message);
            }else if(sort.equals("For_Comment")){
                Log.d("실행", "idx_memo="+remoteMessage.getData().get("idx_memo"));
                showNotification("For_Comment",remoteMessage.getData().get("idx_memo"),title,message);
            }else if(sort.equals("For_Chatting")){
                // 만약 자기 자신이면 아무반응 x
                if(!remoteMessage.getData().get("my").equals(fshared.get_login_value())){ // 자기자신
                    showNotification_for_Chatting("For_Chattinge",remoteMessage.getData().get("room_idx"),title,message,remoteMessage.getData().get("content"),remoteMessage.getData().get("category"),remoteMessage.getData().get("room_title"), remoteMessage.getData().get("nickname"));
                }
            }


        } // end remoteMessage.getData().size()>0



        // 알림메세지를 받는다
            // 데이터 받을 때 => remoteMessage.getNotification().getBody()
        if(remoteMessage.getNotification() != null){
            Log.d("실행", "remoteMessage.getNotification() != null");




        } // end remoteMessage.getNotification() != null
    }


    public void showNotification(String sort,String putextra, String title,String message){
        Intent intent = null;

        // putExtra에 보낼 내용 분기
        if(sort.equals("For_chatting_room_waiting_list")){
            intent = new Intent(this, Activity_Chatting_Room.class);
            intent.putExtra("idx",putextra);

        }else if(sort.equals("For_Follow")){
            intent = new Intent(this, Activity_Management_Follow.class);
            intent.putExtra("login_value",putextra);
        }else if(sort.equals("For_memo_like")){
            intent = new Intent(this,Activity_Management_Follow.class);
            intent.putExtra("login_value",putextra);
        }else if(sort.equals("For_Comment")){
            intent = new Intent(this,Activity_Add_Comment.class);
            intent.putExtra("idx_memo",Integer.parseInt(putextra));
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        // 호출하려는 액티비티의 인스턴스가 이미 존재하는 경우 -> 새로운 인스턴스 생성x,존재하는 액티비티
        // 를 포그라운드에 가져온다 + 액티비티 스택의 최상단 액티비티부터 포그라운드로 가져온 액티비티
        // 까지의 모든 액티비티를 삭제한다

        /*
        PendingIntent(Context context, int requestCode, Intent intent, int flags)
        - context = 어떤 PendingIntent에 의해 서비스르 시작하는 Context
        - requestCode = 보낸 사람에 대한 비공개 요청 코드
        - intent = 시작된 서비스를 나타내는 intent
        - flags = 실제 전달될 때 제공되는 intent의 어떤 불특정 부분을 제어 할 수 있다
         */
        PendingIntent pendingIntent
                = PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_ONE_SHOT);
        // FLAG_ONE_SHOT = 한번만 사용할 수 있는 PendingIntent

        /*
        알림 생성하기
         */
        NotificationCompat.Builder builder
                /*
                Compat사용 이유
                - 하위 호환 가능
                - 버전분기 없이 사용 가능하게 해줌(물론, 알람채널 등록은 버전분기를 해줘야 함)
                 */
                = new NotificationCompat.Builder(getApplicationContext(),getString(R.string.Channel_ID_Chatting_Room))
                // 채널 아이디를 제공해야 한다
                .setSmallIcon(R.mipmap.ic_launcher)
                // 작은 아이콘(사용자가 볼 수 있는 유일한 필수 콘텐츠)
                .setAutoCancel(true)
                // 알람 터치시 자동으로 삭제할 것인지 설정
                .setVibrate(new long[] {1000, 1000, 1000, 1000, 1000})
                .setOnlyAlertOnce(true)
                // 한번만 울리기(중복된 알림은 발생해도 알리지 않음
                .setContentIntent(pendingIntent);

        // 버전별로 분기
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN){
            builder = builder.setContent(getCustomDesign(title, message));
        }else{
            builder = builder.setContentTitle(title)
                    .setContentText(message)
                    .setSmallIcon(R.mipmap.ic_launcher);
        }

        // 알람채널을 시스템에 등록한다
        NotificationManager notificationManager
                = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            // 채널생성
            // Notification Channel = notification들을 groupping하는 데 사용됨
            // Notification을 여러가지 용도로 나누어서 관리할 수 있게 만들어 줌
            NotificationChannel notificationChannel
                    = new NotificationChannel(getString(R.string.Channel_ID_Chatting_Room),"채팅방 참여",NotificationManager.IMPORTANCE_HIGH);
                /*
                - channel id = 고유한 id값을 설정한다
                - channel name = 채널의 이름을 설정한다
                - channel importance - 중요도를 설정한다(DEFAULT, HIGH)
                 */
            notificationManager.createNotificationChannel(notificationChannel);
        }
        notificationManager.notify(0,builder.build());
        // id = 고유한 알림 식별자
        // 사용자에게 표시 할 내용을 설명하는 개체
    }

    private void showNotification_for_Chatting(String sort, String room_idx, String title,String message ,String content, String category, String room_title, String nickname){

        // 알림 클릭 시 이동 할 액티비티
        Intent intent = new Intent(this, Activity_Chatting.class);
        String channel_id = sort;
        int put = Integer.parseInt(room_idx);
        intent.putExtra("room_idx",put);
        intent.putExtra("title",room_title);
        PendingIntent pendingIntent
                = PendingIntent.getActivity(
                this,
                0,
                intent,
                PendingIntent.FLAG_ONE_SHOT
                // FLAG_ONE_SHOT = 한번만 사용 할 수 있는 PendingIntent
        );
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                /*
        알림 생성하기
         */
        NotificationCompat.Builder builder
                /*
                Compat사용 이유
                - 하위 호환 가능
                - 버전분기 없이 사용 가능하게 해줌(물론, 알람채널 등록은 버전분기를 해줘야 함)
                 */
                = new NotificationCompat.Builder(getApplicationContext(),getString(R.string.Channel_ID_Chatting_Room))
                // 채널 아이디를 제공해야 한다
                .setSmallIcon(R.mipmap.ic_launcher)
                // 작은 아이콘(사용자가 볼 수 있는 유일한 필수 콘텐츠)
                .setAutoCancel(true)
                // 알람 터치시 자동으로 삭제할 것인지 설정
                .setVibrate(new long[] {1000, 1000, 1000, 1000, 1000})
                .setOnlyAlertOnce(true)
                // 한번만 울리기(중복된 알림은 발생해도 알리지 않음
                .setContentIntent(pendingIntent);

        // 버전별로 분기
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN){
            builder = builder.setContent(getCustomDesign_for_Chatting(category,title, message,content, nickname));
        }else{
            builder = builder.setContentTitle(title)
                    .setContentText(message)
                    .setSmallIcon(R.mipmap.ic_launcher);
        }

        // 알람채널을 시스템에 등록한다
        NotificationManager notificationManager
                = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            // 채널생성
            // Notification Channel = notification들을 groupping하는 데 사용됨
            // Notification을 여러가지 용도로 나누어서 관리할 수 있게 만들어 줌
            NotificationChannel notificationChannel
                    = new NotificationChannel(getString(R.string.Channel_ID_Chatting_Room),"채팅방 참여",NotificationManager.IMPORTANCE_HIGH);
                /*
                - channel id = 고유한 id값을 설정한다
                - channel name = 채널의 이름을 설정한다
                - channel importance - 중요도를 설정한다(DEFAULT, HIGH)
                 */
            notificationManager.createNotificationChannel(notificationChannel);
        }
        notificationManager.notify(0,builder.build());
        // id = 고유한 알림 식별자
        // 사용자에게 표시 할 내용을 설명하는 개체


    } // end showNotification_for_Chatting




    /*
    알람 커스텀
     */
    // 기본
    private RemoteViews getCustomDesign(String title,String message){
        RemoteViews remoteViews
                = new RemoteViews(getApplicationContext().getPackageName(),R.layout.notification);
        remoteViews.setTextViewText(R.id.noti_title,title);
        remoteViews.setTextViewText(R.id.noti_message,message);
        remoteViews.setImageViewResource(R.id.noti_icon,R.mipmap.ic_launcher);
        return remoteViews;
    } // end getCustomDesign
    // 채팅용 : sort,title, message,content
    private RemoteViews getCustomDesign_for_Chatting(String category,String title,String message, String content, String nickname){
        // 제목(noti_title) =>
        // 분류(sort)용도 => 내용을 보일 것인지 or 이미지를 보일 것인지
        // 내용(noti_message) => message

        /*
        내용셋팅
         */
        final RemoteViews remoteViews
                = new RemoteViews(getApplicationContext().getPackageName(),
                R.layout.notification_for_chatting);
        remoteViews.setTextViewText(R.id.noti_title,title);
        if(category.equals("message")){
            remoteViews.setViewVisibility(R.id.noti_message, View.VISIBLE); // 텍스트뷰 보이기
            remoteViews.setViewVisibility(R.id.for_chatting_img, View.INVISIBLE); // 이미지뷰 안보이기
            remoteViews.setTextViewText(R.id.noti_message,message); // 텍스트 셋팅
        }else if(category.equals("file") || category.equals("files")){
            remoteViews.setViewVisibility(R.id.for_chatting_img, View.VISIBLE); // 이미지뷰 보이기
            //remoteViews.setViewVisibility(R.id.noti_message, View.INVISIBLE); // 텍스트뷰 안보이기
            remoteViews.setTextViewText(R.id.noti_message,nickname+":"); // 텍스트 셋팅
            // url로 부터 비트맵을 받아와서 셋팅한다
            HttpURLConnection connection = null;
            try {
                URL url = new URL(content);
                try {
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setDoInput(true);
                    connection.connect();
                    InputStream input = connection.getInputStream();
                    Bitmap myBitmap = BitmapFactory.decodeStream(input);

                    remoteViews.setImageViewBitmap(R.id.for_chatting_img,myBitmap);

                } catch (IOException e) {
                    e.printStackTrace();
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            }finally{
                if(connection!=null)
                    connection.disconnect();
            }

        }
        return remoteViews;
    } // end getCustomDesign

}
