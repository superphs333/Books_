package com.remon.books;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;

public class GetUri {

    // Uri -> 절대경로로 바꿔서 리턴시켜주는 메소드
    public String getPath(final Context context, final Uri uri){
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if(isKitKat && DocumentsContract.isDocumentUri(context, uri)){

             Log.d("실행", "isKitKat && DocumentsContract.isDocumentUri(context, uri)");


            if(isExternalStorageDocument(uri)){
                Log.d("실행", "isExternalStorageDocument(uri)");

                final String docID
                        = DocumentsContract.getDocumentId(uri);
                final String[] split = docID.split(":");
                final String type = split[0];

                if("primary".equalsIgnoreCase(type)){
                    // 주장소이면
                    return Environment.getExternalStorageDirectory()+"/"+split[1];
                        // 내부저장소 ex/storage/0/
                }else{
                    return "/storage/" + type + "/" + split[1];
                        //외부저장소 ex)/storage/1/, /storage/sdcard/
                }
            }else if(isDownloadsDocument(uri)){
                Log.d("실행", "isDownloadsDocument(uri)");

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                return getDataColumn(context, contentUri, null, null);
            }else if(isMediaDocument(uri)){
                Log.d("실행", "isMediaDocument(uri)");


                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }else if("content".equalsIgnoreCase(uri.getScheme())){
            Log.d("실행", "\"content\".equalsIgnoreCase(uri.getScheme())");

            // equalsIgnoreCase : 문자열을 대소문자 구분없이 비교
            return getDataColumn(context, uri, null, null);
        }else if("file".equalsIgnoreCase(uri.getScheme())){
            Log.d("실행", "\"file\".equalsIgnoreCase(uri.getScheme())");

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
            if(cursor != null && cursor.moveToFirst()){
                // Cursor.moveToFirst = Cursor을 제일 첫번재 행(Row)로 이동 시킨다

                final int column_index = cursor.getColumnIndexOrThrow(column);
                    /* public abstract int getColumnIndexOrThrow(String columnName)
                    : 주어진 열 이름에 대해 0부터 시작하는 인덱스를 반환
                    , 컬럼이 존재하지 않는 경우에는 IllegalArgumentExcpetion 예외
                     */
                return cursor.getString(column_index);
                    // Cursor.getString(int columnIndex)
                    // = 요청 된 열의 값을 문자열로 반환한다

            }

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
