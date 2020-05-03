package com.app.today;

class Headline {
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

    String getTitle() {
        return title;
    }
    String getUrl() {
        return url;
    }
    String getBmp() {
        return bmp;
    }
}
