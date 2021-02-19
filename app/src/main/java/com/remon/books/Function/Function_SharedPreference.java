package com.remon.books.Function;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

public class Function_SharedPreference {

    public Context context;
    public static final String DEFAULT_VALUE_STRING = "";
    public static final boolean DEFAULT_VALUE_BOOLEAN = false;
    public static final int DEFAULT_VALUE_INT = -1;
    public static final long DEFAULT_VALUE_LONG = -1L;
    public static final float DEFAULT_VALUE_FLOAT = -1F;

    // 생성자
    public Function_SharedPreference(Context context){
        this.context = context;
    }


    // 데이터 저장 함수
    public void setPreference(String PREFERENCE,String key, boolean value){
        SharedPreferences pref = context.getSharedPreferences(PREFERENCE, MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }
    public void setPreference(String PREFERENCE,String key, String value){
        SharedPreferences pref = context.getSharedPreferences(PREFERENCE, MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(key, value);
        editor.commit();
    }
    public void setPreference(String PREFERENCE,String key, int value){
        SharedPreferences pref = context.getSharedPreferences(PREFERENCE, MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(key, value);
        editor.commit();
    }
    public void setPreference(String PREFERENCE,String key, float value){
        SharedPreferences pref = context.getSharedPreferences(PREFERENCE, MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putFloat(key, value);
        editor.commit();
    }

    public void setPreference(String PREFERENCE,String key, long value){
        SharedPreferences pref = context.getSharedPreferences(PREFERENCE, MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putLong(key, value);
        editor.commit();
    }


    // 데이터 불러오기 함수
    public boolean getPreferenceBoolean(String PREFERENCE,String key){
        SharedPreferences pref = context.getSharedPreferences(PREFERENCE, MODE_PRIVATE);
        return pref.getBoolean(key, false);
    }
    public String getPreferenceString(String PREFERENCE,String key){
        SharedPreferences pref = context.getSharedPreferences(PREFERENCE, MODE_PRIVATE);
        return pref.getString(key, "");
    }
    public int getPreferenceInt(String PREFERENCE,String key){
        SharedPreferences pref = context.getSharedPreferences(PREFERENCE, MODE_PRIVATE);
        return pref.getInt(key, 0);
    }
    public float getPreferenceFloat(String PREFERENCE,String key){
        SharedPreferences pref = context.getSharedPreferences(PREFERENCE, MODE_PRIVATE);
        return pref.getFloat(key, 0f);
    }
    public long getPreferenceLong(String PREFERENCE,String key){
        SharedPreferences pref = context.getSharedPreferences(PREFERENCE, MODE_PRIVATE);
        return pref.getLong(key, 0l);
    }

    // 데이터 한개씩 삭제하는 함수
    public void setPreferenceRemove(String PREFERENCE,String key){
        SharedPreferences pref = context.getSharedPreferences(PREFERENCE, MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.remove(key);
        editor.commit();
    }

    // 모든 데이터 삭제
    public void setPreferenceClear(String PREFERENCE){
        SharedPreferences pref = context.getSharedPreferences(PREFERENCE, MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.clear();
        editor.commit();
    }
}
