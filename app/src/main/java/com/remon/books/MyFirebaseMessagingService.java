package com.remon.books;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;


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

    // 새 토큰이 생성될 때마다 onNewToken콜백이 호출된다
    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        Log.d("실행", "onNewToken함수 호출");
        Log.d("실행", "Refreshed token: " + s);
    }

    // 메세지를 받았을 때 동작하는 메서드
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.d("실행", "onMessageReceived함수 호출");

        // 데이터 메세지를 받는다
            // 데이터 받을 때 => remoteMessage.getData().get("key")
        if(remoteMessage.getData().size()>0){
            Log.d("실행", "remoteMessage.getData().size()>0");


        }

        // 알림메세지를 받는다
            // 데이터 받을 때 => remoteMessage.getNotification().getBody()
        if(remoteMessage.getNotification() != null){
            Log.d("실행", "remoteMessage.getNotification() != null");

        }
    }
}
