package com.hutech.demo.controller;

import com.hutech.demo.model.Category;
import com.hutech.demo.model.Product;
import com.hutech.demo.repository.CategoryRepository;
import com.hutech.demo.repository.ProductRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    public CategoryController(CategoryRepository categoryRepository, ProductRepository productRepository) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("categories", categoryRepository.findAll());
        model.addAttribute("category", new Category());
        return "category/manage";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute Category category) {
        categoryRepository.save(category);
        return "redirect:/categories";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id, Model model) {
        model.addAttribute("categories", categoryRepository.findAll());
        model.addAttribute("category", categoryRepository.findById(id).orElse(new Category()));
        return "category/manage";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        List<Product> products = productRepository.findByCategoryId(id);
        products.forEach(product -> product.setCategory(null));
        productRepository.saveAll(products);

        categoryRepository.deleteById(id);
        return "redirect:/categories";
    }
}