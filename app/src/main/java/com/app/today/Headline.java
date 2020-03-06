package com.app.today;

class Headline {
    private String title;
    //private String category;
    private String link;

    Headline(String title, String link) {//, String category
        this.title = title;
        //this.category = category;
        this.link = link;
    }

    String getTitle() {
        return title;
    }
    /*String getCategory() {
        return category;
    }*/
    String getLink() {
        return link;
    }
}
