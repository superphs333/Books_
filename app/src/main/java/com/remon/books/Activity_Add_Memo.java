package com.remon.books;

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
import com.remon.books.Function.Function_Set;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;

public class Activity_Add_Memo extends AppCompatActivity {

    String unique_book_value; // 책 고유값

    /*
    함수
     */
    Function_Set fs;

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
    }

    // 카메라(btn_camera) => 카메라에서 이미지 가져오기
    public void Pick_From_Camera(View view) {
        image_Uri_String = fs.camera_for_profile(REQUEST_IMAGE_CAPTURE_VALUE);
    }

    public void Pick_From_Gallery(View view) {
        fs.pick_from_gallery_imgs(PICK_FROM_GALLERY_VALUE);
    }
}
