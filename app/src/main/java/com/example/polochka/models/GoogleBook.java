package com.example.polochka.models;

public class GoogleBook {

    private String thumbnail;
    private String author;
    private String description;

    private String title;

    public String getThumbnail() {
        return thumbnail;
    }

    public GoogleBook(String thumbnail, String author, String description, String title) {
        this.title = title;
        this.thumbnail = thumbnail;
        this.author = author;
        this.description = description;
    }

    public String getAuthor() {
        return author;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }
}
