package com.remon.books;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;

public class GetUri {

    // Uri -> 절대경로로 바꿔서 리턴시켜주는 메소드
    public String getPath(final Context context, final Uri uri){
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if(isKitKat && DocumentsContract.isDocumentUri(context, uri)){
            if(isExternalStorageDocument(uri)){

            }else if(isDownloadsDocument(uri)){

            }else if(isMediaDocument(uri)){

            }
        }else if("content".equalsIgnoreCase(uri.getScheme())){
            // equalsIgnoreCase : 문자열을 대소문자 구분없이 비교
            return getDataColumn(context, uri, null, null);
        }else if("file".equalsIgnoreCase(uri.getScheme())){
            return uri.getPath();
        }

        return null;
    } // end getPath

    /*
    getDataColumn
    : 이 Uri에 대한 데이터 열의 값을 가져온다
    -> MediaStore Uri 및 기타 파일 기반 ContentProviders
     */
    public static String getDataColumn
    (Context context, Uri uri, String selection, String[] selectionArgs){
        /*
        매개변수
        - context
        - uri :  쿼리할 uri
        - selection(선택) : 쿼리에 사용되는 필터
        - selectionArgs(선택) : 쿼리에 사용되는 선택 인수

         반환값
         : 일반적으로 파일 경로인 _data열의값
        */
//////////
        Cursor cursor = null;
        final String column = "_data";
        final String column2= "_data";

        final String[] projection = {
                column
        };

        try{
            cursor
                    = context
                    .getContentResolver()
                    .query(uri, projection, selection, selectionArgs, null);

        }finally{
            if(cursor != null){
                cursor.close();
            }
        }

        return null;

    }// end getDataColumn


    public static boolean isExternalStorageDocument(Uri uri){
            // 매개변수 : 체크할 uri
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
            // 리턴값 : Uri 기관이 ExternalStorageProvider인지 여부
    }

    public static boolean isDownloadsDocument(Uri uri) {
            // 매개변수 : 체크할 uri
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
            // 리턴값 : Uri 기관이 DownloadsProvider인지 여부
    }

    public static boolean isMediaDocument(Uri uri) {
            // 매개변수 : 체크할 uri
        return "com.android.providers.media.documents".equals(uri.getAuthority());
            // 리턴값 : Uri 기관이 MediaProvider인지 여부
    }
}
