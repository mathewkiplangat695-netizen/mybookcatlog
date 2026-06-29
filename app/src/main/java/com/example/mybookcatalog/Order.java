package com.example.mybookcatalog;

import java.io.Serializable;
import java.util.List;

public class Order implements Serializable {
    private String orderId;
    private String date;
    private List<CartItem> items;

    public Order(String orderId, String date, List<CartItem> items) {
        this.orderId = orderId;
        this.date = date;
        this.items = items;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getDate() {
        return date;
    }

    public List<CartItem> getItems() {
        return items;
    }
}