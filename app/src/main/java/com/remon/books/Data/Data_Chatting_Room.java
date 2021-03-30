package com.remon.books.Data;

public class Data_Chatting_Room {

    String title, room_explain,leader;
    int total_count, join_count, idx;

    public Data_Chatting_Room(String title, String room_explain, int total_count, int join_count, int idx, String leader) {
        this.title = title;
        this.room_explain = room_explain;
        this.total_count = total_count;
        this.join_count = join_count;
        this.idx = idx;
        this.leader = leader;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getRoom_explain() {
        return room_explain;
    }

    public void setRoom_explain(String room_explain) {
        this.room_explain = room_explain;
    }

    public int getTotal_count() {
        return total_count;
    }

    public void setTotal_count(int total_count) {
        this.total_count = total_count;
    }

    public int getJoin_count() {
        return join_count;
    }

    public void setJoin_count(int join_count) {
        this.join_count = join_count;
    }

    public int getIdx() {
        return idx;
    }

    public void setIdx(int idx) {
        this.idx = idx;
    }

    public String getLeader() {
        return leader;
    }

    public void setLeader(String leader) {
        this.leader = leader;
    }
}
