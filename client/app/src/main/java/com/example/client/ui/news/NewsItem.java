package com.example.client.ui.news;

import android.widget.ImageView;

public class NewsItem {
    private String title;
    private String publishTime;
    private int coverID;
    private String coverURL;
    private String url;

    public NewsItem(String title, String publishTime, String coverURL, String url) {
        this.title = title;
        this.publishTime = publishTime;
        this.coverURL = coverURL;
        this.url = url;
    }

    public NewsItem(String title, String publishTime, int coverID, String url) {
        this.title = title;
        this.publishTime = publishTime;
        this.coverID = coverID;
        this.url = url;
    }

    public String getCoverURL() {
        return coverURL;
    }

    public void setCoverURL(String coverURL) {
        this.coverURL = coverURL;
    }

    public NewsItem(String title, String publishTime, String url) {
        this.title = title;
        this.publishTime = publishTime;
        this.url = url;
    }
    

    public NewsItem(String title, String publishTime, int coverID) {
        this.title = title;
        this.publishTime = publishTime;
        this.coverID = coverID;
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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
