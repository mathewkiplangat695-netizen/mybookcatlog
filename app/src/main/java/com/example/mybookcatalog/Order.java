package com.example.mybookcatalog;

import java.io.Serializable;
import java.util.List;

public class Order implements Serializable {
    private String orderId;
    private String date;
    private double totalAmount;
    private List<CartItem> items;

    public Order(String orderId, String date, double totalAmount, List<CartItem> items) {
        this.orderId = orderId;
        this.date = date;
        this.totalAmount = totalAmount;
        this.items = items;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getDate() {
        return date;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public List<CartItem> getItems() {
        return items;
    }
}