package com.example.client.ui.news;

import android.widget.ImageView;

public class NewsItem {
    private String title;
    private String publishTime;
    private int coverID;

    public NewsItem(String title, String publishTime, int first_pic) {
        this.title = title;
        this.publishTime = publishTime;
        this.coverID = first_pic;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(String publishTime) {
        this.publishTime = publishTime;
    }

    public NewsItem(String title, String publishTime) {
        this.title = title;
        this.publishTime = publishTime;
    }

    public int getCoverID() {
        return coverID;
    }

    public void setCoverID(int coverID) {
        this.coverID = coverID;
    }
}
