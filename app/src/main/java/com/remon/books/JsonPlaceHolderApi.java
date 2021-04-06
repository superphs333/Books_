package com.remon.books;

import com.remon.books.Data.Data_Book_Memo;
import com.remon.books.Data.Data_Chatting_Room;
import com.remon.books.Data.Data_Comment_Memo;
import com.remon.books.Data.Data_Follow_People;
import com.remon.books.Data.Data_Join_People;
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
    Call<ArrayList<Data_My_Book>> Get_My_Book(@Field("unique_book_value") String unique_book_value, @Field("login_value") String login_value);

    /*
    Data_Book_Memo 가져오기
     */
    @FormUrlEncoded
    @POST("Data/Get_Book_Memo.php")
    Call<ArrayList<Data_Book_Memo>> Get_Book_Memo(@Field("login_value") String login_value, @Field("unique_book_value") String unique_book_value);

    /*
    Data_Book_Memo 가져오기
     */
    @FormUrlEncoded
    @POST("Data/Get_Book_Memo_in_SNS.php")
    Call<ArrayList<Data_Book_Memo>> Get_Book_Memo_in_SNS(@Field("requester") String requester, @Field("unique_book_value") String unique_book_value,@Field("view") String view,@Field("chk_heart") boolean chk_heart);

    /*
    Data_Book_Memo 가져오기
     */
//    @FormUrlEncoded
//    @POST("Data/Get_Book_Memo_in_SNS.php")
//    Call<ArrayList<Data_Book_Memo>> Get_Book_Memo_in_Feed(@Field("requester") String requester
//            , @Field("unique_book_value") String unique_book_value,@Field("view") String view
//            ,@Field("chk_heart") boolean chk_heart,@Field("") boolean chk_heart);

    /*
    Data_Follow_People 가져오기
     */
    @FormUrlEncoded
    @POST("Data/Get_Follow_People.php")
    Call<ArrayList<Data_Follow_People>> Get_Follow(@Field("login_value") String login_value, @Field("sort") String sort);

    /*
    Data_Chatting_Room 가져오기1 - 전체
     */
    @POST("Data/Get_Chatting_Room_Data.php")
    Call<ArrayList<Data_Chatting_Room>> Get_Chatting_Room_Data_Whole();

    /*
    Data_Chatting_Room 가져오기1 - 참여중 or 대기중
     */
    @FormUrlEncoded
    @POST("Data/Get_Chatting_Room_Data.php")
    Call<ArrayList<Data_Chatting_Room>> Get_Chatting_Room_Data_By_Sort(@Field("login_value") String login_value, @Field("state") int state);

    /*
    Data_Join_People 가져오기
     */
    @FormUrlEncoded
    @POST("Data/Get_Join_Peoples.php")
    Call<ArrayList<Data_Join_People>> Get_Join_Peoples(@Field("idx") int idx, @Field("login_value") String login_value);


    /*
    댓글 가져오기 - 메모에 대한 댓글
     */
    @FormUrlEncoded
    @POST("Data/Get_Comments.php")
    Call<ArrayList<Data_Comment_Memo>> Get_Comment_Memos(@Field("idx_memo") int idx_memo);


    /*
    댓글 가져오기 - 내가 쓴 댓글
     */
    @FormUrlEncoded
    @POST("Data/Get_Comments.php")
    Call<ArrayList<Data_Comment_Memo>> Get_Comment_Memos_I(@Field("login_value") String login_value);

}
