package com.example.boticaapp.utils;

import com.example.boticaapp.models.CartItem;
import java.util.ArrayList;
import java.util.List;

public class CartManager {
    private static CartManager instance;
    private final List<CartItem> items;

    private CartManager() {
        items = new ArrayList<>();
    }

    public static synchronized CartManager getInstance() {
        if (instance == null) {
            instance = new CartManager();
        }
        return instance;
    }

    // Devuelve la lista actual (no una copia)
    public List<CartItem> getItems() {
        return items;
    }

    public void addItem(CartItem item) {
        // si ya existe, solo incrementa cantidad
        for (CartItem ci : items) {
            if (ci.getMedicine().getId() == item.getMedicine().getId()) {
                ci.setQuantity(ci.getQuantity() + item.getQuantity());
                return;
            }
        }
        items.add(item);
    }

    public void clear() {
        items.clear();
    }

    public double getTotal() {
        double sum = 0;
        for (CartItem ci : items) sum += ci.getSubtotal();
        return sum;
    }
}
