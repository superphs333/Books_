package com.remon.books;

import com.remon.books.Data.Member_Info;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface JsonPlaceHolderApi {

    /*
    개인정보 가져오기(by post)
     */
    @FormUrlEncoded
    @POST("get_member_info.php")
    Call<ArrayList<Member_Info>> get_member_info_data(@Field("login_value") String login_value);
}
