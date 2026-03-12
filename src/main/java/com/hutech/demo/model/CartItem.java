package com.hutech.demo.model;

public class CartItem {

    private final Product product;
    private final int quantity;
    private final double lineTotal;

    public CartItem(Product product, int quantity, double lineTotal) {
        this.product = product;
        this.quantity = quantity;
        this.lineTotal = lineTotal;
    }

    public Product getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getLineTotal() {
        return lineTotal;
    }
}