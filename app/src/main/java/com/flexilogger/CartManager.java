package com.flexilogger;

import java.util.ArrayList;
import java.util.List;

public class CartManager {
    private static final List<Product> cartItems = new ArrayList<>();

    public static void addToCart(Product product) {
        cartItems.add(product);
    }

    public static List<Product> getCartItems() {
        return new ArrayList<>(cartItems);
    }

    public static void clearCart() {
        cartItems.clear();
    }

    public static boolean isEmpty() {
        return cartItems.isEmpty();
    }
    public static void removeFromCart(Product product) {
        cartItems.remove(product);
    }

}
