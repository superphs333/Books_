package com.remon.books;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.remon.books.Function.Function_Set;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import static java.lang.Thread.sleep;

public class MyCanvas extends View {

    int startX= -1;
    int startY= -1;
    int stopX= -1;
    int stopY= -1;
    boolean mode_eraser;
        // true -> 지우개 false -> 펜
    Bitmap bitmap; // 서버에서 온 이미지 -> 비트맵화
    Canvas mcanvas;
    Paint mpaint = new Paint();
    Bitmap image_Bitmap;
        // image_Bitmpa = 이미지 셋팅용 비트맵
        // paint_Bitmap = 페인트를 그릴 비트맵
    Context context;
    Activity activity;
    Path path = new Path();
    public String m_filename;
        // 셋팅할 이미지 파일 주소
    public String send_filepath;
        // 보낼 파일 주소


    public MyCanvas(Context context) {
        super(context);
        Log.d("실행", "Context");
        this.context = context;
        this.setLayerType(LAYER_TYPE_SOFTWARE,null);
        //this.m_filename = m_filename;
            // 이걸했더니 에러가 났다
    }

    public MyCanvas(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        Log.d("실행", "Context context, @Nullable AttributeSet attrs");
        this.setLayerType(LAYER_TYPE_SOFTWARE,null);
        //this.m_filename = m_filename;
            // 이걸했더니 에러가 났다
    }

//    public MyCanvas(Context context,Bitmap bitmap) {
//        super(context);
//        Log.d("실행", "Context context,Bitmap bitmap");
//        this.setLayerType(LAYER_TYPE_SOFTWARE,null);
//        this.bitmap = bitmap;
//    }
//
//    public MyCanvas(Context context, @Nullable AttributeSet attrs,Bitmap bitmap) {
//        super(context, attrs);
//        Log.d("실행", "Context context, @Nullable AttributeSet attrs,Bitmap bitmap");
//        this.setLayerType(LAYER_TYPE_SOFTWARE,null);
//        //this.m_filename = m_filename;
//        // 이걸했더니 에러가 났다
//        this.bitmap = bitmap;
//    }





    // 뷰의 크기가 변경되었을 때 호출한다
    @Override
    protected void onSizeChanged(int width, int height, int oldw, int oldh) {
        super.onSizeChanged(width, height, oldw, oldh);

        Log.d("실행", "m_filename="+m_filename);


        // Paint 설정
        mpaint.setStrokeWidth(10f);
        mpaint.setAntiAlias(true);
        mpaint.setAntiAlias(true);

        image_Bitmap = Bitmap.createBitmap(getWidth(),getHeight(), Bitmap.Config.ARGB_8888);
        //paint_Bitmap = Bitmap.createBitmap(getWidth(),getHeight(), Bitmap.Config.ARGB_8888);

        // bitmap에 직접 그림을 그리거나 다른 이미지를 그릴려고 하면, 새로운 canvas를 만들어내야 한다
        mcanvas = new Canvas();

        // 비트맵과 캔버스 연결
        mcanvas.setBitmap(image_Bitmap);
        // 앞으로 canvas에 그리는 모든 작업은 bitmap에 반영이 된다
        mcanvas.drawColor(Color.WHITE);
        Log.d("실행","width="+getWidth()+", height="+getHeight());

        /*
        캔버스에 비트맵 셋팅
        : 이미지를 로드할 때는, 필요한 만큼만 이미지를 sampling하여 로드할 필요가 있다
         */
        final int w = getWidth(); // 뷰너비
        final int h = getHeight(); // 뷰높이
        Log.d("실행", "w="+getWidth());
        Log.d("실행", "h="+getHeight());

        // 여기에서 서버에서 온 경우/아닌 경우 분기
        if(m_filename.substring(0,4).equals("http")){ // 서버에서
            Log.d("실행", "서버o");

            Glide.with(context)
                    .asBitmap()
                    .load(m_filename)
                    .into(new CustomTarget<Bitmap>(){
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            bitmap = resource;

                            // 비트맵 리사이즈
                            Resize_Bitmap(bitmap,w,h);
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {

                        }
                    });
        }else{ // 이용자의 기기에서
            Log.d("실행", "서버x");

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.RGB_565;
            options.inJustDecodeBounds = true;
                // 이미지 크기를 구할 때는 이 속성을 true로 지정해 주고,
                // 이 옵션을 이용해서 BitmapFactory의 decode메소드를 사용함
            BitmapFactory.decodeFile(m_filename,options);

            // 화면 크기에 가장 근접하는 이미지의 리스케일 사이즈
            bitmap = BitmapFactory.decodeFile(m_filename);

            Resize_Bitmap(bitmap, w, h);
        }

    }

    private void Resize_Bitmap(Bitmap bitmap, int w, int h) {
        float bitmap_width = bitmap.getWidth();
        float bitmap_height = bitmap.getHeight();
        if(bitmap_height>h){
            float percente = (float)(bitmap_height / 100);
            float scale = (float)(h / percente);
            bitmap_width *=(scale/100);
            bitmap_height *= (scale/100);
        } // end if
        bitmap = Bitmap.createScaledBitmap(bitmap,(int)bitmap_width,(int)bitmap_height,true);

        // 비트맵 크기
        float left = (w-bitmap.getWidth())/2;
        float top = (h-bitmap.getHeight())/2;
        Log.d("실행", "left="+left);
        Log.d("실행", "top="+top);
        // 비트맵 그리기
        mcanvas.drawBitmap(bitmap,left,top,mpaint);
            /* void drawBitmap(Bitmap bitmap, float left, float top, Paint paint)
            : 왼쪽 모서리 좌표를 (x,y)로 가지는 bitmap을 그림
             */
        invalidate();
    }


    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    /* onDraw(Canvas canvas)
        개별적인 뷰를 그릴때 호출된다
         */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(image_Bitmap != null){
            canvas.drawBitmap(image_Bitmap,0,0,null);
        }
    } // end onDraw

    /* onTouchEvent(MotionEvent event)
     : 터치화면 이벤트 핸들러의 콜백 메서드(화면에 터치가 발생했을 때 호출되는 콜백 메서드)
     */

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        int X = (int)event.getX();
        int Y = (int)event.getY();
        if(event.getAction() == MotionEvent.ACTION_DOWN)
        {
            startX = X; startY = Y;
            path.moveTo(X,Y);
        }
        else if (event.getAction() == MotionEvent.ACTION_MOVE)
        {
            if(startX != -1)
            {
                mcanvas.drawLine(startX,startY,X,Y,mpaint);
                invalidate();
                startX = X; startY = Y;
            }
        }
        else if (event.getAction() == MotionEvent.ACTION_UP)
        {
            if(startX != -1)
            {
                mcanvas.drawLine(startX,startY,X,Y,mpaint);
            }
            invalidate();
            startX = -1; startY = -1;
        }
        return true;
    }

    // 해당 이미지를 저장하고 이전엑티비티로 전송
    public String Save_Send(){

        // 다른방법으로 이미지를 저장해야 하나?

        // 임의 경로에 파일 만들기
//        File photoFile = null;
//        try {
//            photoFile = createImgageFile();
//        } catch (IOException e) {
//            e.printStackTrace();
//            Log.d("실행", "photoFile에러=>"+e.getMessage());
//        }
//
//        // FileProvider를 통해서 파일의 uri값을 만든다
//        if(photoFile !=null){
//            send_filepath = String.valueOf(FileProvider.getUriForFile(context,context.getPackageName(),photoFile));
//                // 주어진 파일에 대한 uri반환
//            Log.d("실행","send_filepath="+send_filepath);
//            image_Bitmap.compress(Bitmap.CompressFormat.JPEG,90,out)
//        }else{
//            Log.d("실행", "photoFile=null");
//        }
//
//        // 이미지 저장하기
//        String root = context.getExternalCacheDir().toString();

//        try {
////
////            File file = new File(String.valueOf(createImgageFile()));
////            OutputStream os = null;
////
////            file.createNewFile();
////            os = new FileOutputStream(file);
////
////            image_Bitmap.compress(Bitmap.CompressFormat.PNG,100,os);
////            os.close();
////
////        } catch (IOException e) {
////            e.printStackTrace();
////        }

//        ContentValues contentValues = new ContentValues(3);
//        contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, "Draw_On_Me");
//        Uri imageFileUri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,contentValues);
//
//        try {
//            OutputStream imageFileOS = context.getContentResolver().openOutputStream(imageFileUri);
//            image_Bitmap.compress(Bitmap.CompressFormat.JPEG,90,imageFileOS);
//
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//            Log.d("실행","FileNotFoundException:"+e.getMessage());
//        }

        //Log.d("실행", "imageFileUri="+imageFileUri);

        // 내부저장소 캐시 경로를 받아온다
        File storage = context.getCacheDir();

        // 저장할 파일 이름
        String timeStamp
                = new SimpleDateFormat("yyyyMMdd_HHmmss")
                .format(new Date());
        String fileName = "TEST_"+timeStamp+".jpg";
        // storeae에 파일 인스턴스를 생성
        File tempFile = new File(storage,fileName);

        try {
            // 자동으로 빈 파일을 생성
            tempFile.createNewFile();

            // 파일을 쓸 수 있는 스트림을 준비
            FileOutputStream out = new FileOutputStream(tempFile);

            // compress함수를 사용해 스틀미에 비트맵을 저장
            image_Bitmap.compress(Bitmap.CompressFormat.JPEG,100,out);

            // 스트림 사용후 닫아준자
            out.close();

            send_filepath = tempFile.getPath();
            Log.d("실행", "send_filepath="+send_filepath);
        } catch (FileNotFoundException e) {
            Log.e("실행","FileNotFoundException : " + e.getMessage());
        } catch (IOException e) {
            Log.e("실행","IOException : " + e.getMessage());
        }


        return String.valueOf(send_filepath);
    }

    // 좋은 화질의 사진을 가져오기 위해 -> 임시파일을 생성한다
    private File createImgageFile() throws IOException{
        String timeStamp
                = new SimpleDateFormat("yyyyMMdd_HHmmss")
                .format(new Date());
        m_filename = "TEST_"+timeStamp+"_";

        /*
        createTempFile에 들어갈 매개변수 중 directory부분(File)
         */
        File storageDir
                = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            /*
            File getExternalFilesDir(String type)
            : 매개변수에 원하는 디렉토리 유형을 전달하여,
            적절한 디렉토리를 얻을 수 있다.
            (여기서  Environment.DIRECTORY_PICTURES는 그림 파일 저장)
             */

        /*
        createTempFile = 임시 파일을 생성 할 수 있다
        createTempFile(String prefix, String suffix, File directory)
        : 지정된 접두사와 접미사 문자열을 사용하여
        지정 된 디렉토리에 새 빈 파일을 작성하여 이름을 생성한다
         */
        File image = File.createTempFile(
                m_filename,
                ".jpg",
                storageDir
        );

        send_filepath = image.getAbsolutePath();
        Log.d("실행","send_filepath="+send_filepath);

        return image;
    }

    public void Eraser(){
        image_Bitmap.eraseColor(Color.WHITE);
        invalidate();

        /*
        캔버스에 비트맵 셋팅
        : 이미지를 로드할 때는, 필요한 만큼만 이미지를 sampling하여 로드할 필요가 있다
         */
        final int w = getWidth(); // 뷰너비
        final int h = getHeight(); // 뷰높이
        Log.d("실행", "w="+getWidth());
        Log.d("실행", "h="+getHeight());

        // 여기에서 서버에서 온 경우/아닌 경우 분기
        if(m_filename.substring(0,4).equals("http")){ // 서버에서
            Log.d("실행", "서버o");

            Glide.with(context)
                    .asBitmap()
                    .load(m_filename)
                    .into(new CustomTarget<Bitmap>(){
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            bitmap = resource;

                            // 비트맵 리사이즈
                            Resize_Bitmap(bitmap,w,h);
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {

                        }
                    });
        }else{ // 이용자의 기기에서
            Log.d("실행", "서버x");

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.RGB_565;
            options.inJustDecodeBounds = true;
            // 이미지 크기를 구할 때는 이 속성을 true로 지정해 주고,
            // 이 옵션을 이용해서 BitmapFactory의 decode메소드를 사용함
            BitmapFactory.decodeFile(m_filename,options);

            // 화면 크기에 가장 근접하는 이미지의 리스케일 사이즈
            bitmap = BitmapFactory.decodeFile(m_filename);

            Resize_Bitmap(bitmap, w, h);
        }

    } // end Eraser()
}
