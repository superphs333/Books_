package com.remon.books.Data;

public class Data_Comment_Memo {
    int idx_memo; // 메모 idx
    int idx; // 댓글 idx
    String login_value; // 작성자
    String nickname; // 닉네임
    String profile_url; // 프로필 사진
    String comment; // 댓글 내용
    String date_time; // 날짜
    String parent; // 부모 댓글

    // 대댓글
    public Data_Comment_Memo(int idx_memo, int idx, String login_value, String nickname, String profile_url, String comment, String date_time, String parent) {
        this.idx_memo = idx_memo;
        this.idx = idx;
        this.login_value = login_value;
        this.nickname = nickname;
        this.profile_url = profile_url;
        this.comment = comment;
        this.date_time = date_time;
        this.parent = parent;
    }

    // 댓글
    public Data_Comment_Memo(int idx_memo, int idx, String login_value, String nickname, String profile_url, String comment, String date_time) {
        this.idx_memo = idx_memo;
        this.idx = idx;
        this.login_value = login_value;
        this.nickname = nickname;
        this.profile_url = profile_url;
        this.comment = comment;
        this.date_time = date_time;
    }

    public int getIdx_memo() {
        return idx_memo;
    }

    public void setIdx_memo(int idx_memo) {
        this.idx_memo = idx_memo;
    }

    public int getIdx() {
        return idx;
    }

    public void setIdx(int idx) {
        this.idx = idx;
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

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getDate_time() {
        return date_time;
    }

    public void setDate_time(String date_time) {
        this.date_time = date_time;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }
}
