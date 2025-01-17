package com.example.polochka;

public class ChatModel {
    private String bookTitle;       // Название книги
    private String userName;        // Имя пользователя
    private String lastMessage;     // Последнее сообщение

    // Конструктор
    public ChatModel(String bookTitle, String userName, String lastMessage) {
        this.bookTitle = bookTitle;
        this.userName = userName;
        this.lastMessage = lastMessage;
    }

    // Геттеры и сеттеры
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

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }


}
