package com.remon.books.Data;

public class Data_Join_People {
    boolean follow; // 내가 상대를 팔로우 하고 있는지 (팔로우 하고 있다면 : 1, 아니라면 : 0)
    String nickname, profile_url,login_value;

    public Data_Join_People(boolean follow, String nickname, String profile_url, String login_value) {
        this.follow = follow;
        this.nickname = nickname;
        this.profile_url = profile_url;
        this.login_value = login_value;
    }

    public boolean isFollow() {
        return follow;
    }

    public void setFollow(boolean follow) {
        this.follow = follow;
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

    public String getLogin_value() {
        return login_value;
    }

    public void setLogin_value(String login_value) {
        this.login_value = login_value;
    }
}
