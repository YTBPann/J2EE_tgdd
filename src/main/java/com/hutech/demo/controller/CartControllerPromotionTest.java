package com.hutech.demo.controller;

import com.hutech.demo.model.Product;
import com.hutech.demo.repository.ProductRepository;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpSession;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CartControllerPromotionTest {

    private ProductRepository productRepository;
    private CartController cartController;
    private HttpSession session;

    @BeforeEach
    void setUp() {
        productRepository = mock(ProductRepository.class);
        cartController = new CartController(productRepository);
        session = new MockHttpSession();
    }

    @Test
    void addToCart_shouldNotExceedPromotionQuantity() {
        Product product = new Product();
        product.setId(1L);
        product.setPromotionQuantity(1);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenAnswer(inv -> inv.getArgument(0));

        cartController.addToCart(1L, session);
        cartController.addToCart(1L, session);

        @SuppressWarnings("unchecked")
        Map<Long, Integer> cart = (Map<Long, Integer>) session.getAttribute("CART");
        assertEquals(1, cart.get(1L));
        assertEquals(0, product.getPromotionQuantity());
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void decreaseQuantity_shouldRestorePromotionQuantity() {
        Product product = new Product();
        product.setId(2L);
        product.setPromotionQuantity(2);

        when(productRepository.findById(2L)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenAnswer(inv -> inv.getArgument(0));

        cartController.addToCart(2L, session);
        cartController.decreaseQuantity(2L, session);

        assertEquals(2, product.getPromotionQuantity());
        verify(productRepository, times(2)).save(any(Product.class));
    }
}