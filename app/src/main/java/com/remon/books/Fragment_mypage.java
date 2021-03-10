package com.remon.books;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.databinding.adapters.ImageViewBindingAdapter;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.remon.books.Function.Function_Set;
import com.remon.books.Function.Function_SharedPreference;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;


public class Fragment_mypage extends Fragment {

    /*
    뷰변수
     */
    View v;
    Activity activity;
    Context context;
    ImageView img_profile,img_setting;
    TextView txt_nickname;

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


    /*
    권한이 허가되거나 거부당했을 때,
    결과를 리턴해주는 리스너
     */
    PermissionListener permissionlistener = new PermissionListener() {

        /*
        권한이 모두 허용 되거나서 실행됨
         */
        @Override
        public void onPermissionGranted() {
            Log.d("실행", "권한허가");
        }

        /*
        요청한 권한중에서 거부당한 권한목록을 리턴해줌
        ex) 3개의 권한을 요청했는데 사용자가 1개만 허용하고 2개는 거부한 경우
        -> onPermissionDenied()에 2개의 거부당한 권한목록이 전달됨
         */
        @Override
        public void onPermissionDenied(ArrayList<String> deniedPermissions) {
            Log.d("실행", "거부 된 권한->"+deniedPermissions.toString());
        }


    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.d("실행", "==========Fragment_mypage onCreateView==========");


        /*
        뷰연결
         */
        LayoutInflater inflater1 = inflater;
        v = inflater.inflate(R.layout.fragment_mypage,container,false);
        activity = getActivity();
        context = v.getContext();
        img_profile = v.findViewById(R.id.img_profile);
        img_setting = v.findViewById(R.id.img_setting);
        txt_nickname = v.findViewById(R.id.txt_nickname);

        // 함수셋팅
        fshared = new Function_SharedPreference(context);
        fs = new Function_Set();
        fs.context = context;




        // 세팅 버튼 => Acitvity_Setting으로 이동
        img_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,Activity_Setting.class);
                startActivity(intent);
            }
        });

        // img_profile(이미지) => 프로필 사진 변경 액티비티(Activity_Change_Profile)
        img_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context,Activity_Change_Profile.class);
                startActivity(intent);

//                // 카메라 or 갤러리 선택
//                AlertDialog.Builder builder
//                        = new AlertDialog.Builder(activity);
//                final String str[] = {"카메라","갤러리"};
//                builder.setTitle("선택하세요")
//                        .setNegativeButton("취소",null)
//                        .setItems(str,// 리스트 목록에 사용할 배열
//                                new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
//                                        Log.d("실행","선택된것="+str[which]);
//
//                                        // 분기 -> 카메라, 갤러리
//                                        if(str[which].equals("카메라")){
//
//                                        }else if(str[which].equals("갤러리")){
//                                            Intent intent = new Intent();
//                                            intent.setType("image/*");
//                                            // 이미지르 열 수 있는 앱을 호출
//                                            intent.setAction(Intent.ACTION_GET_CONTENT);
//                                            startActivityForResult(intent,PICK_FROM_GALLERY);
//                                        }
//
//                                    } // end onClick
//                                }
//                        );
//                AlertDialog dialog = builder.create();
//                dialog.show();



            } // end onClick
        });

        // txt_nickname(닉네임) => 닉네임 변경
        txt_nickname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,Change_nickname.class);
                startActivity(intent);
            }
        });







        // Inflate the layout for this fragment
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();

        /*
        프로필, 닉네임 셋팅 (서버에서 가져온다)
        by member에 저장된(sharedpreference)
         */
        Log.d("실행", "nickname="+fshared.getPreferenceString("member","nickname"));
        Log.d("실행", "profile_url="+fshared.getPreferenceString("member","profile_url"));
        Glide.with(context).load(fshared.getPreferenceString("member","profile_url")).into(img_profile);
        txt_nickname.setText(fshared.getPreferenceString("member","nickname"));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        /*
        갤러리 이미지 가져오기
         */
        if(requestCode==PICK_FROM_GALLERY&&resultCode==RESULT_OK && data!=null && data.getData()!=null){
            fs.log("onAcitivtyResult - 갤러리에서 이미지 가져오기");
            // 이미지 크롭
            CropImage
                    .activity(data.getData())
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .start(activity);

        }// end requestCode==PICK_FROM_GALLERY

        /*
        이미지 크롭
         */
        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            fs.log("onAcitivtyResult -  이미지 크롭");

            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if(resultCode == RESULT_OK){
                image_Uri = CropImage.getActivityResult(data).getUri().getPath();

                Log.d("실행", "image_Uri="+image_Uri);


                // 비트맵 방향 변경
                String temp = String.valueOf(image_Uri);


                image_bitmap = fs.rotate_from(temp);


            }
        }else if(resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            Exception error = result.getError();
            Log.d("실행", "이미지 크롭 에러=>"+error);
        }
    } // end onActivityResult
}
