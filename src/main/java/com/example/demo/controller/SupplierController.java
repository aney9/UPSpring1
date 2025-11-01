package com.example.demo.controller;

import com.example.demo.model.*;
import com.example.demo.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;

import java.beans.PropertyEditorSupport;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/supplier")
public class SupplierController {

    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private CategoryRepository categoryRepository;
    
    @Autowired
    private WarehouseRepository warehouseRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private SupplierRepository supplierRepository;
    
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(Category.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) {
                if (text != null && !text.isEmpty()) {
                    Optional<Category> category = categoryRepository.findById(Long.parseLong(text));
                    category.ifPresent(this::setValue);
                }
            }
        });
        
        binder.registerCustomEditor(Warehouse.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) {
                if (text != null && !text.isEmpty()) {
                    Optional<Warehouse> warehouse = warehouseRepository.findById(Long.parseLong(text));
                    warehouse.ifPresent(this::setValue);
                }
            }
        });
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByUsername(auth.getName()).orElse(null);
        
        if (user != null) {
            Optional<Supplier> supplier = supplierRepository.findByUser_Id(user.getId());
            if (supplier.isPresent()) {
                List<Product> products = productRepository.findAll().stream()
                    .filter(p -> p.getSupplier() != null && p.getSupplier().getId().equals(supplier.get().getId()))
                    .toList();
                model.addAttribute("productsCount", products.size());
                model.addAttribute("supplier", supplier.get());
            }
        }
        return "supplier/dashboard";
    }

    @GetMapping("/products")
    public String productsPage(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByUsername(auth.getName()).orElse(null);
        
        if (user != null) {
            Optional<Supplier> supplier = supplierRepository.findByUser_Id(user.getId());
            if (supplier.isPresent()) {
                List<Product> products = productRepository.findAll().stream()
                    .filter(p -> p.getSupplier() != null && p.getSupplier().getId().equals(supplier.get().getId()))
                    .toList();
                model.addAttribute("products", products);
            }
        }
        model.addAttribute("categories", categoryRepository.findAll());
        model.addAttribute("warehouses", warehouseRepository.findAll());
        return "supplier/products";
    }

    @GetMapping("/products/new")
    public String newProductForm(Model model) {
        model.addAttribute("product", new Product());
        model.addAttribute("categories", categoryRepository.findAll());
        model.addAttribute("warehouses", warehouseRepository.findAll());
        return "supplier/product-form";
    }

    @PostMapping("/products/new")
    public String createProduct(@Valid @ModelAttribute Product product, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("categories", categoryRepository.findAll());
            model.addAttribute("warehouses", warehouseRepository.findAll());
            return "supplier/product-form";
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByUsername(auth.getName()).orElse(null);
        
        if (user != null) {
            Optional<Supplier> supplier = supplierRepository.findByUser_Id(user.getId());
            supplier.ifPresent(product::setSupplier);
        }
        
        productRepository.save(product);
        return "redirect:/supplier/products";
    }

    @GetMapping("/products/edit/{id}")
    public String editProductForm(@PathVariable Long id, Model model) {
        Optional<Product> product = productRepository.findById(id);
        if (product.isPresent()) {
            model.addAttribute("product", product.get());
            model.addAttribute("categories", categoryRepository.findAll());
            model.addAttribute("warehouses", warehouseRepository.findAll());
            return "supplier/product-form";
        }
        return "redirect:/supplier/products";
    }

    @PostMapping("/products/edit/{id}")
    public String updateProduct(@PathVariable Long id, @Valid @ModelAttribute Product product, 
                               BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("categories", categoryRepository.findAll());
            model.addAttribute("warehouses", warehouseRepository.findAll());
            return "supplier/product-form";
        }

        Optional<Product> existingProduct = productRepository.findById(id);
        if (existingProduct.isPresent()) {
            Product prod = existingProduct.get();
            prod.setName(product.getName());
            prod.setDescription(product.getDescription());
            prod.setPrice(product.getPrice());
            prod.setQuantity(product.getQuantity());
            prod.setCategory(product.getCategory());
            prod.setWarehouse(product.getWarehouse());
            prod.setImageUrl(product.getImageUrl());
            productRepository.save(prod);
        }
        
        return "redirect:/supplier/products";
    }

    @PostMapping("/products/delete/{id}")
    public String deleteProduct(@PathVariable Long id) {
        productRepository.deleteById(id);
        return "redirect:/supplier/products";
    }
}

