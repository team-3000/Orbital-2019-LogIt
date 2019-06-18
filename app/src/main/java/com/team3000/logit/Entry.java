package com.team3000.logit;

public class Entry {
    private String id;
    private String title;
    private String date;
    private String time;
    private String desc;

    public Entry(String id, String title, String date, String time, String desc) {
        this.id = id;
        this.title = title;
        this.date = date;
        this.time = time;
        this.desc = desc;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getDesc() {
        return desc;
    }
}
