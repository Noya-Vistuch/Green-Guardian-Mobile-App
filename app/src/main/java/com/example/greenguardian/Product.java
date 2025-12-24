package com.example.greenguardian;

public class Product {
    private int id;
    private byte[] image;
    private String price;
    private String comment;

    private int userId;

    public Product(int id, byte[] image, String price, String comment, int userId) {
        this.id = id;
        this.image = image;
        this.price = price;
        this.comment = comment;
        this.userId = userId;
    }

    public int getUserId() {
        return userId;
    }

    public int getId() {
        return id;
    }

    public byte[] getImage() {
        return image;
    }

    public String getPrice() {
        return price;
    }

    public String getComment() {
        return comment;
    }
}
