package com.example.polochka.models;

import java.io.Serializable;

public class ItemModel  implements Serializable {

    private String bookTitle;       // Название книги
    private String userName;        // Имя пользователя

    public ItemModel(String bookTitle, String userName) {
        this.bookTitle = bookTitle;
        this.userName = userName;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
