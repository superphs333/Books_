package com.remon.books.Data;

public class Data_Chatting {
    int idx; // 채팅 idx
    int room_idx; // 룸 idx
    String login_value, nickname, profile_url; // 작성자 login_value, nickname, profile_url
    String sort; // 구분
    String content; // 내용
    String date; // 날짜
    String order_tag; // 순서태그


    public Data_Chatting(int idx, int room_idx, String login_value, String nickname, String profile_url, String sort, String content, String date, String order_tag) {
        this.idx = idx;
        this.room_idx = room_idx;
        this.login_value = login_value;
        this.nickname = nickname;
        this.profile_url = profile_url;
        this.sort = sort;
        this.content = content;
        this.date = date;
        this.order_tag = order_tag;
    }


    public int getIdx() {
        return idx;
    }

    public void setIdx(int idx) {
        this.idx = idx;
    }

    public int getRoom_idx() {
        return room_idx;
    }

    public void setRoom_idx(int room_idx) {
        this.room_idx = room_idx;
    }

    public String getLogin_value() {
        return login_value;
    }

    public void setLogin_value(String login_value) {
        this.login_value = login_value;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getProfile_url() {
        return profile_url;
    }

    public void setProfile_url(String profile_url) {
        this.profile_url = profile_url;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getOrder_tag() {
        return order_tag;
    }

    public void setOrder_tag(String order_tag) {
        this.order_tag = order_tag;
    }
}
