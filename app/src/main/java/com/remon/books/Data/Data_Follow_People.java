package com.remon.books.Data;

public class Data_Follow_People {

    String login_value, nickname, profile_url;
    Boolean visible;

    public Data_Follow_People(String login_value, String nickname, String profile_url,Boolean visible) {
        this.login_value = login_value;
        this.nickname = nickname;
        this.profile_url = profile_url;
        this.visible = visible;
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

    public Boolean getVisible() {
        return visible;
    }

    public void setVisible(Boolean visible) {
        this.visible = visible;
    }
}
