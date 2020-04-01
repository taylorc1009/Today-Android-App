package com.app.today.BusinessLayer;

public class Headline {
    //Headline class to store the article title, bitmap and URL so we can
    //open it in the browser

    private String title;
    private String url;
    private String bmp;

    Headline(String title, String url, String bmp) {
        this.title = title;
        this.url = url;
        this.bmp = bmp;
    }

    public String getTitle() {
        return title;
    }
    public String getUrl() {
        return url;
    }
    public String getBmp() {
        return bmp;
    }
}
