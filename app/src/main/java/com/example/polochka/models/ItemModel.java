package com.example.polochka.models;

import java.io.Serializable;

public class ItemModel implements Serializable {

    private int id;                  // ID книги
    private String title;            // Название книги
    private String author;           // Автор книги
    private String description;      // Описание книги
    private String city;             // Город, где находится книга
    private String district;         // Район города
    private double latitude;         // Широта местоположения
    private double longitude;        // Долгота местоположения
    private String timestamp;        // Дата и время добавления
    private String phoneNumber;      // Номер телефона
    private String username;         // Имя пользователя
    private String imageId;         // URL изображения книги

    public ItemModel(int id, String title, String author, String description, String city, String district,
                     double latitude, double longitude, String phoneNumber, String username, String imageId) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.description = description;
        this.city = city;
        this.district = district;
        this.latitude = latitude;
        this.longitude = longitude;
        this.phoneNumber = phoneNumber;
        this.username = username;
        this.imageId = imageId;

    }

    // Геттеры и сеттеры, которые вы можете добавить по вашему усмотрению.

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getimageId() {
        return imageId;
    }

    public void setimageId(String imageId) {
        this.imageId = imageId;
    }
}
