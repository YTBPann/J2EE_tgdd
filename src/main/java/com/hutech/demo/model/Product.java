package com.hutech.demo.model;

import com.hutech.demo.model.Category;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "products")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private double price;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "LONGTEXT")
    private String imageUrl;

    @Column(nullable = false)
    private int promotionQuantity = 0;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;
}