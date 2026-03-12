package com.hutech.demo.controller;

import com.hutech.demo.model.CartItem;
import com.hutech.demo.model.Product;
import com.hutech.demo.repository.ProductRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class CartController {

    private static final String CART_SESSION_KEY = "CART";

    private final ProductRepository productRepository;

    public CartController(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @PostMapping("/cart/add/{productId}")
    public String addToCart(@PathVariable Long productId, HttpSession session) {
        Product product = productRepository.findById(productId).orElse(null);
        if (product == null || product.getPromotionQuantity() <= 0) {
            return "redirect:/products";
        }

        Map<Long, Integer> cart = getCart(session);
        cart.merge(productId, 1, Integer::sum);
        product.setPromotionQuantity(product.getPromotionQuantity() - 1);
        productRepository.save(product);
        session.setAttribute(CART_SESSION_KEY, cart);
        return "redirect:/products";
    }

    @GetMapping("/cart")
    public String viewCart(Model model, HttpSession session) {
        Map<Long, Integer> cart = getCart(session);
        List<CartItem> cartItems = new ArrayList<>();
        double grandTotal = 0;

        for (Map.Entry<Long, Integer> entry : cart.entrySet()) {
            Product product = productRepository.findById(entry.getKey()).orElse(null);
            if (product == null) {
                continue;
            }

            int quantity = Math.max(entry.getValue(), 1);
            double lineTotal = product.getPrice() * quantity;
            grandTotal += lineTotal;
            cartItems.add(new CartItem(product, quantity, lineTotal));
        }

        model.addAttribute("cartItems", cartItems);
        model.addAttribute("grandTotal", grandTotal);
        model.addAttribute("cartItemCount", cart.values().stream().mapToInt(Integer::intValue).sum());
        return "cart/view";
    }

    @PostMapping("/cart/increase/{productId}")
    public String increaseQuantity(@PathVariable Long productId, HttpSession session) {
        Map<Long, Integer> cart = getCart(session);
        if (cart.containsKey(productId)) {
            Product product = productRepository.findById(productId).orElse(null);
            if (product != null && product.getPromotionQuantity() > 0) {
                cart.merge(productId, 1, Integer::sum);
                product.setPromotionQuantity(product.getPromotionQuantity() - 1);
                productRepository.save(product);
                session.setAttribute(CART_SESSION_KEY, cart);
            }
        }
        return "redirect:/cart";
    }

    @PostMapping("/cart/decrease/{productId}")
    public String decreaseQuantity(@PathVariable Long productId, HttpSession session) {
        Map<Long, Integer> cart = getCart(session);
        if (cart.containsKey(productId)) {
            int updated = cart.get(productId) - 1;
            if (updated <= 0) {
                cart.remove(productId);
            } else {
                cart.put(productId, updated);
            }
            restorePromotionQuantity(productId, 1);            
            session.setAttribute(CART_SESSION_KEY, cart);
        }
        return "redirect:/cart";
    }

    @PostMapping("/cart/remove/{productId}")
    public String removeItem(@PathVariable Long productId, HttpSession session) {
        Map<Long, Integer> cart = getCart(session);
        int removedQuantity = cart.getOrDefault(productId, 0);
        cart.remove(productId);
        if (removedQuantity > 0) {
            restorePromotionQuantity(productId, removedQuantity);
        }
        session.setAttribute(CART_SESSION_KEY, cart);
        return "redirect:/cart";
    }

    @PostMapping("/cart/clear")
    public String clearCart(HttpSession session) {
        Map<Long, Integer> cart = getCart(session);
        for (Map.Entry<Long, Integer> entry : cart.entrySet()) {
            restorePromotionQuantity(entry.getKey(), entry.getValue());
        }
        session.setAttribute(CART_SESSION_KEY, new HashMap<Long, Integer>());
        return "redirect:/cart";
    }
        session.setAttribute(CART_SESSION_KEY, new HashMap<Long, Integer>());
        return "redirect:/cart";
    }

    private void restorePromotionQuantity(Long productId, int quantityToRestore) {
        if (quantityToRestore <= 0) {
            return;
        }

        productRepository.findById(productId).ifPresent(product -> {
            product.setPromotionQuantity(product.getPromotionQuantity() + quantityToRestore);
            productRepository.save(product);
        });
    }

    @SuppressWarnings("unchecked")
    private Map<Long, Integer> getCart(HttpSession session) {
        Object cartObj = session.getAttribute(CART_SESSION_KEY);
        if (cartObj instanceof Map<?, ?>) {
            return (Map<Long, Integer>) cartObj;
        }
        return new HashMap<>();
    }
}