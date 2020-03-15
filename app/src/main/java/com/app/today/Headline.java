package com.app.today;

class Headline {
    //Headline class to store the article title and URL so we can
    //open it in the browser

    private String title;
    private String url;

    Headline(String title, String url) {
        this.title = title;
        this.url = url;
    }

    String getTitle() {
        return title;
    }
    String getUrl() {
        return url;
    }
}
