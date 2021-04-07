package com.remon.books.Data;

public class Data_Comment_Memo {
    int idx_memo; // 메모 idx
    int idx; // 댓글 idx
    String login_value; // 작성자
    String nickname; // 닉네임
    String profile_url; // 프로필 사진
    String comment; // 댓글 내용
    String date_time; // 날짜
    int group_idx; // 부모 댓글
    int depth; // 깊이 (부모: 0, 자식 :1)
    String target; // 타겟 닉네임
    int visibility;

    // 대댓글
    public Data_Comment_Memo(int idx_memo, int idx, String login_value, String nickname, String profile_url, String comment, String date_time, int group_idx,int depth, String target, int visibility) {
        this.idx_memo = idx_memo;
        this.idx = idx;
        this.login_value = login_value;
        this.nickname = nickname;
        this.profile_url = profile_url;
        this.comment = comment;
        this.date_time = date_time;
        this.group_idx = group_idx;
        this.depth = depth;
        this.target = target;
        this.visibility = visibility;
    }

    // 댓글
    public Data_Comment_Memo(int idx_memo, int idx, String login_value, String nickname, String profile_url, String comment, String date_time, int group_idx,int depth, int visibility) {
        this.idx_memo = idx_memo;
        this.idx = idx;
        this.login_value = login_value;
        this.nickname = nickname;
        this.profile_url = profile_url;
        this.comment = comment;
        this.date_time = date_time;
        this.group_idx = group_idx;
        this.depth = depth;
        this.visibility = visibility;

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

    public int getGroup_idx() {
        return group_idx;
    }

    public void setGroup_idx(int group_idx) {
        this.group_idx = group_idx;
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public int getVisibility() {
        return visibility;
    }

    public void setVisibility(int visibility) {
        this.visibility = visibility;
    }
}
