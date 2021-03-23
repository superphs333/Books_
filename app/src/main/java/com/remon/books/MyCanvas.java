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
import android.net.Uri;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.io.FileNotFoundException;
import java.io.OutputStream;

public class MyCanvas extends View {

    int startX= -1;
    int startY= -1;
    int stopX= -1;
    int stopY= -1;
    boolean mode_eraser;
        // true -> 지우개 false -> 펜
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


    public MyCanvas(Context context) {
        super(context);
        this.setLayerType(LAYER_TYPE_SOFTWARE,null);
        //this.m_filename = m_filename;
            // 이걸했더니 에러가 났다
    }

    public MyCanvas(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.setLayerType(LAYER_TYPE_SOFTWARE,null);
        //this.m_filename = m_filename;
            // 이걸했더니 에러가 났다

    }

    // 뷰의 크기가 변경되었을 때 호출한다
    @Override
    protected void onSizeChanged(int width, int height, int oldw, int oldh) {
        super.onSizeChanged(width, height, oldw, oldh);

        Log.d("실행", "m_filename="+m_filename);


        image_Bitmap = Bitmap.createBitmap(getWidth(),getHeight(), Bitmap.Config.ARGB_8888);
        //paint_Bitmap = Bitmap.createBitmap(getWidth(),getHeight(), Bitmap.Config.ARGB_8888);


        // Paint 설정
        mpaint.setStrokeWidth(10f);
        mpaint.setAntiAlias(true);
            // 선분을 매끄럽게 그리기 위해서
        // 블러효과
//        BlurMaskFilter blur = new BlurMaskFilter(10, BlurMaskFilter.Blur.INNER);
//        mpaint.setColor(Color.BLUE);
//        mpaint.setMaskFilter(blur);
//        mpaint.setStrokeJoin(Paint.Join.ROUND);
            // 선끝 모양
        // 투명도
        mpaint.setAntiAlias(true);
//        mpaint.setAlpha(150);

        // bitmap에 직접 그림을 그리거나 다른 이미지를 그릴려고 하면, 새로운 canvas를 만들어내야 한다
        mcanvas = new Canvas();

        // 비트맵과 캔버스 연결
        mcanvas.setBitmap(image_Bitmap);
            // 앞으로 canvas에 그리는 모든 작업은 bitmap에 반영이 된다
        mcanvas.drawColor(Color.WHITE);

        /*
        캔버스에 비트맵 셋팅
         */
        // 이미지를 로드할 때는, 필요한 만큼만 이미지를 sampling하여 로드할 필요가 있다
        Log.d("실행","width="+getWidth()+", height="+getHeight());
        // 리사이즈할 이미지 크기
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        options.inJustDecodeBounds = true;
            // 이미지 크기를 구할 때는 이 속성을 true로 지정해 주고,
            // 이 옵션을 이용해서 BitmapFactory의 decode메소드를 사용함
        BitmapFactory.decodeFile(m_filename,options);
        // 화면 크기에 가장 근접하는 이미지의 리스케일 사이즈
        int w = getWidth(); // 뷰너비
        int h = getHeight(); // 뷰높이
        Log.d("실행", "w="+getWidth());
        Log.d("실행", "h="+getHeight());
        Log.d("실행", "options.outWidth="+options.outWidth);
        Log.d("실행", "options.getHeight="+options.outHeight);
        Bitmap bitmap = BitmapFactory.decodeFile(m_filename);
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

        ContentValues contentValues = new ContentValues(3);
        contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, "Draw On Me");
        Uri imageFileUri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,contentValues);
        try {
            OutputStream imageFileOS = context.getContentResolver().openOutputStream(imageFileUri);
            image_Bitmap.compress(Bitmap.CompressFormat.JPEG,90,imageFileOS);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.d("실행","FileNotFoundException:"+e.getMessage());
        }

        Log.d("실행", "imageFileUri="+imageFileUri);

        return String.valueOf(imageFileUri);
    }
}
