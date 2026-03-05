package com.hutech.demo.controller;

import com.hutech.demo.model.Product;
import com.hutech.demo.model.Category;
import com.hutech.demo.repository.CategoryRepository;
import com.hutech.demo.repository.ProductRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/products")
public class ProductController {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public ProductController(ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    // Hiển thị danh sách sản phẩm
    @GetMapping
    public String list(Model model) {
        List<Product> products = productRepository.findAll();
        Map<String, List<Product>> productsByCategory = new LinkedHashMap<>();

        categoryRepository.findAll().forEach(category -> {
            List<Product> productsInCategory = products.stream()
                    .filter(product -> product.getCategory() != null
                            && category.getId().equals(product.getCategory().getId()))
                    .toList();
            if (!productsInCategory.isEmpty()) {
                productsByCategory.put(category.getName(), productsInCategory);
            }
        });

        List<Product> uncategorizedProducts = products.stream()
                .filter(product -> product.getCategory() == null)
                .toList();
        if (!uncategorizedProducts.isEmpty()) {
            productsByCategory.put("Chưa phân loại", uncategorizedProducts);
        }

        model.addAttribute("products", products);
        model.addAttribute("productsByCategory", productsByCategory);
        return "product/list";
    }

    // Hiển thị form thêm sản phẩm
    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("product", new Product());
        model.addAttribute("categories", categoryRepository.findAll());
        return "product/add";
    }

    // Xử lý lưu sản phẩm mới
    @PostMapping("/add")
    public String save(@ModelAttribute Product product) {
        if (product.getCategory() != null) {
            Long categoryId = product.getCategory().getId();
            if (categoryId == null) {
                product.setCategory(null);
            } else {
                categoryRepository.findById(categoryId).ifPresent(product::setCategory);
            }
        }

        productRepository.save(product);
        return "redirect:/products";
    }

    // Hiển thị form sửa sản phẩm
    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        Product product = productRepository.findById(id).orElse(new Product());
        model.addAttribute("product", product);
        model.addAttribute("categories", categoryRepository.findAll());
        return "product/add";
    }

    // Xử lý xóa sản phẩm
    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        productRepository.deleteById(id);
        return "redirect:/products";
    }
}