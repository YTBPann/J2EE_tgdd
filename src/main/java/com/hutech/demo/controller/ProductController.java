package com.hutech.demo.controller;

import com.hutech.demo.model.Product;
import com.hutech.demo.repository.ProductRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/products")
public class ProductController {

    private final ProductRepository productRepository;

    public ProductController(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    // Hiển thị danh sách sản phẩm
    @GetMapping
    public String list(Model model) {
        model.addAttribute("products", productRepository.findAll());
        return "product/list";
    }

    // Hiển thị form thêm sản phẩm
    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("product", new Product());
        return "product/add";
    }

    // Xử lý lưu sản phẩm mới
    @PostMapping("/add")
    public String save(@ModelAttribute Product product) {
        productRepository.save(product);
        return "redirect:/products";
    }

    // Hiển thị form sửa sản phẩm
    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        Product product = productRepository.findById(id).orElse(new Product());
        model.addAttribute("product", product);
        return "product/add";
    }

    // Xử lý xóa sản phẩm
    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        productRepository.deleteById(id);
        return "redirect:/products";
    }
}