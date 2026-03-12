package com.hutech.demo.model;

public record CartItem(Product product, int quantity, double lineTotal) {
}