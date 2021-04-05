package com.remon.books.Data;

public class Data_Book_Memo {

    int idx,page,count_heart,count_comment;
    String login_value,nickname, profile_url, unique_book_value, title,date_time,
            img_urls,memo,open,thumbnail;
    Boolean check_heart;
    public boolean follow; // 내가 상대를 팔로우 하고 있는지 (팔로우 하고 있다면 : 1, 아니라면 : 0)


    public Data_Book_Memo(int idx, int page, int count_heart, int count_comment, String login_value, String nickname, String profile_url, String unique_book_value, String title, String date_time, String img_urls, String memo, String open, String thumbnail, Boolean check_heart) {
        this.idx = idx;
        this.page = page;
        this.count_heart = count_heart;
        this.count_comment = count_comment;
        this.login_value = login_value;
        this.nickname = nickname;
        this.profile_url = profile_url;
        this.unique_book_value = unique_book_value;
        this.title = title;
        this.date_time = date_time;
        this.img_urls = img_urls;
        this.memo = memo;
        this.open = open;
        this.thumbnail = thumbnail;
        this.check_heart = check_heart;
    }

    public Data_Book_Memo(int idx, int page, int count_heart, int count_comment, String login_value,
                          String nickname, String profile_url, String unique_book_value,
                          String title, String date_time, String img_urls, String memo, String open,
                          String thumbnail, Boolean check_heart, boolean follow) {
        this.idx = idx;
        this.page = page;
        this.count_heart = count_heart;
        this.count_comment = count_comment;
        this.login_value = login_value;
        this.nickname = nickname;
        this.profile_url = profile_url;
        this.unique_book_value = unique_book_value;
        this.title = title;
        this.date_time = date_time;
        this.img_urls = img_urls;
        this.memo = memo;
        this.open = open;
        this.thumbnail = thumbnail;
        this.check_heart = check_heart;
        this.follow = follow;
    }

    public int getIdx() {
        return idx;
    }

    public void setIdx(int idx) {
        this.idx = idx;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
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

    public String getUnique_book_value() {
        return unique_book_value;
    }

    public void setUnique_book_value(String unique_book_value) {
        this.unique_book_value = unique_book_value;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate_time() {
        return date_time;
    }

    public void setDate_time(String date_time) {
        this.date_time = date_time;
    }

    public String getImg_urls() {
        return img_urls;
    }

    public void setImg_urls(String img_urls) {
        this.img_urls = img_urls;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public String getOpen() {
        return open;
    }

    public void setOpen(String open) {
        this.open = open;
    }

    public int getCount_heart() {
        return count_heart;
    }

    public void setCount_heart(int count_heart) {
        this.count_heart = count_heart;
    }

    public int getCount_comment() {
        return count_comment;
    }

    public void setCount_comment(int count_comment) {
        this.count_comment = count_comment;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public Boolean getCheck_heart() {
        return check_heart;
    }

    public void setCheck_heart(Boolean check_heart) {
        this.check_heart = check_heart;
    }

    public boolean isFollow() {
        return follow;
    }

    public void setFollow(boolean follow) {
        this.follow = follow;
    }
}
