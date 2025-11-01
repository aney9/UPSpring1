package com.example.demo.controller;

import com.example.demo.model.*;
import com.example.demo.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/client")
public class ClientController {

    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private CategoryRepository categoryRepository;
    
    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private OrderItemRepository orderItemRepository;
    
    @Autowired
    private ReviewRepository reviewRepository;

    @GetMapping("/products")
    public String productsPage(@RequestParam(required = false) Long categoryId, Model model) {
        List<Product> products;
        if (categoryId != null && categoryId > 0) {
            Optional<Category> category = categoryRepository.findById(categoryId);
            if (category.isPresent()) {
                products = productRepository.findAll().stream()
                    .filter(p -> p.getCategory() != null && p.getCategory().getId().equals(categoryId))
                    .toList();
                model.addAttribute("selectedCategory", category.get());
            } else {
                products = productRepository.findAll();
            }
        } else {
            products = productRepository.findAll();
        }
        
        model.addAttribute("products", products);
        model.addAttribute("categories", categoryRepository.findAll());
        return "client/products";
    }

    @GetMapping("/products/{id}")
    public String productDetails(@PathVariable Long id, Model model) {
        Optional<Product> product = productRepository.findById(id);
        if (product.isPresent()) {
            model.addAttribute("product", product.get());
            model.addAttribute("reviews", reviewRepository.findByProduct_Id(id));
            model.addAttribute("newReview", new Review());
            return "client/product-details";
        }
        return "redirect:/client/products";
    }

    @PostMapping("/products/{id}/add-to-cart")
    @ResponseBody
    public String addToCart(@PathVariable Long id, @RequestParam Integer quantity) {
        // В будущем можно реализовать корзину через сессию
        return "redirect:/client/products";
    }

    @GetMapping("/orders")
    public String ordersPage(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByUsername(auth.getName()).orElse(null);
        
        if (user != null) {
            List<Order> orders = orderRepository.findByUser_Id(user.getId());
            model.addAttribute("orders", orders);
        }
        return "client/orders";
    }

    @GetMapping("/orders/{id}")
    public String orderDetails(@PathVariable Long id, Model model) {
        Optional<Order> order = orderRepository.findById(id);
        if (order.isPresent()) {
            model.addAttribute("order", order.get());
            model.addAttribute("orderItems", orderItemRepository.findByOrder_Id(id));
            return "client/order-details";
        }
        return "redirect:/client/orders";
    }

    @PostMapping("/reviews")
    public String createReview(@ModelAttribute Review review, @RequestParam Long productId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByUsername(auth.getName()).orElse(null);
        
        if (user != null) {
            Optional<Product> product = productRepository.findById(productId);
            if (product.isPresent()) {
                review.setProduct(product.get());
                review.setUser(user);
                reviewRepository.save(review);
            }
        }
        return "redirect:/client/products/" + productId;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByUsername(auth.getName()).orElse(null);
        
        if (user != null) {
            List<Order> orders = orderRepository.findByUser_Id(user.getId());
            model.addAttribute("ordersCount", orders.size());
            model.addAttribute("user", user);
        }
        return "client/dashboard";
    }
}


