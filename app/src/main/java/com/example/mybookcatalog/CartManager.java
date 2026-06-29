package com.example.mybookcatalog;

import java.util.ArrayList;
import java.util.List;

public class CartManager {
    private static List<CartItem> cartItems = new ArrayList<>();

    public static void addBook(Book book) {
        for (CartItem item : cartItems) {
            if (item.getBook().getTitle().equals(book.getTitle())) {
                item.setQuantity(item.getQuantity() + 1);
                return;
            }
        }
        cartItems.add(new CartItem(book, 1));
    }

    public static void removeBook(Book book) {
        cartItems.removeIf(item -> item.getBook().getTitle().equals(book.getTitle()));
    }

    public static void updateQuantity(Book book, int quantity) {
        for (CartItem item : cartItems) {
            if (item.getBook().getTitle().equals(book.getTitle())) {
                if (quantity <= 0) {
                    removeBook(book);
                } else {
                    item.setQuantity(quantity);
                }
                return;
            }
        }
    }

    public static List<CartItem> getCartItems() {
        return cartItems;
    }

    public static void clearCart() {
        cartItems.clear();
    }

    public static int getItemCount() {
        int count = 0;
        for (CartItem item : cartItems) {
            count += item.getQuantity();
        }
        return count;
    }
}