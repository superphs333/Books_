package com.remon.books;

import com.remon.books.Data.Data_My_Book;
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

    /*
    My_Books 데이터 가져오기
     */
    @FormUrlEncoded
    @POST("Data/Get_My_Books.php")
    Call<ArrayList<Data_My_Book>> Get_My_Books(@Field("login_value") String login_value, @Field("status") int status, @Field("search") String search);

    /*
    My_Books 데이터 1개 가져오기
     */
    @FormUrlEncoded
    @POST("Data/Get_My_Book.php")
    Call<ArrayList<Data_My_Book>> Get_My_Book(@Field("unique_book_value") String unique_book_value);
}
