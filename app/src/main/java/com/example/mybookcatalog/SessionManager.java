package com.example.mybookcatalog;

import java.util.ArrayList;
import java.util.List;

public class SessionManager {
    private static User currentUser = new User("mathew koech", "mathewkiplangat695@gmail.com");
    private static List<Order> orderHistory = new ArrayList<>();
    private static List<User> registeredUsers = new ArrayList<>();

    static {
        // Pre-register the requested user
        registeredUsers.add(currentUser);
    }

    public static boolean register(String name, String email) {
        for (User u : registeredUsers) {
            if (u.getEmail().equalsIgnoreCase(email)) return false;
        }
        registeredUsers.add(new User(name, email));
        return true;
    }

    public static boolean login(String email) {
        for (User u : registeredUsers) {
            if (u.getEmail().equalsIgnoreCase(email)) {
                currentUser = u;
                return true;
            }
        }
        return false;
    }

    public static void login(User user) {
        currentUser = user;
    }

    public static void logout() {
        currentUser = null;
        orderHistory.clear();
    }

    public static boolean isLoggedIn() {
        return currentUser != null;
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static void addOrder(Order order) {
        orderHistory.add(0, order);
    }

    public static List<Order> getOrderHistory() {
        return orderHistory;
    }
}