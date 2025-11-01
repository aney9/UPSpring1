package com.example.demo.controller;

import com.example.demo.model.*;
import com.example.demo.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
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
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private CategoryRepository categoryRepository;
    
    @Autowired
    private WarehouseRepository warehouseRepository;
    
    @Autowired
    private SupplierRepository supplierRepository;
    
    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private ReviewRepository reviewRepository;
    
    @Autowired
    private RoleRepository roleRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
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
        
        binder.registerCustomEditor(Supplier.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) {
                if (text != null && !text.isEmpty()) {
                    Optional<Supplier> supplier = supplierRepository.findById(Long.parseLong(text));
                    supplier.ifPresent(this::setValue);
                }
            }
        });
        
        binder.registerCustomEditor(User.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) {
                if (text != null && !text.isEmpty()) {
                    Optional<User> user = userRepository.findById(Long.parseLong(text));
                    user.ifPresent(this::setValue);
                }
            }
        });
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("usersCount", userRepository.count());
        model.addAttribute("productsCount", productRepository.count());
        model.addAttribute("ordersCount", orderRepository.count());
        model.addAttribute("categoriesCount", categoryRepository.count());
        return "admin/dashboard";
    }

    // ==================== USERS ====================
    @GetMapping("/users")
    public String usersPage(Model model) {
        model.addAttribute("users", userRepository.findAll());
        return "admin/users";
    }

    @GetMapping("/users/new")
    public String newUserForm(Model model) {
        model.addAttribute("user", new User());
        return "admin/user-form";
    }

    @PostMapping("/users/new")
    public String createUser(@Valid @ModelAttribute User user, BindingResult result) {
        if (result.hasErrors()) {
            return "admin/user-form";
        }
        // Hash password
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return "redirect:/admin/users";
    }

    @GetMapping("/users/edit/{id}")
    public String editUserForm(@PathVariable Long id, Model model) {
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            model.addAttribute("user", user.get());
            return "admin/user-form";
        }
        return "redirect:/admin/users";
    }

    @PostMapping("/users/edit/{id}")
    public String updateUser(@PathVariable Long id, @Valid @ModelAttribute User user, BindingResult result) {
        if (result.hasErrors()) {
            return "admin/user-form";
        }
        Optional<User> existingUser = userRepository.findById(id);
        if (existingUser.isPresent()) {
            User userToUpdate = existingUser.get();
            userToUpdate.setUsername(user.getUsername());
            userToUpdate.setEmail(user.getEmail());
            // Only hash password if it was changed
            if (user.getPassword() != null && !user.getPassword().isEmpty()) {
                userToUpdate.setPassword(passwordEncoder.encode(user.getPassword()));
            }
            userToUpdate.setRole(user.getRole());
            userToUpdate.setFullName(user.getFullName());
            userToUpdate.setPhone(user.getPhone());
            userToUpdate.setEnabled(user.getEnabled());
            userRepository.save(userToUpdate);
        }
        return "redirect:/admin/users";
    }

    @PostMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable Long id) {
        userRepository.deleteById(id);
        return "redirect:/admin/users";
    }

    // ==================== PRODUCTS ====================
    @GetMapping("/products")
    public String productsPage(Model model) {
        model.addAttribute("products", productRepository.findAll());
        return "admin/products";
    }

    @GetMapping("/products/new")
    public String newProductForm(Model model) {
        model.addAttribute("product", new Product());
        model.addAttribute("categories", categoryRepository.findAll());
        model.addAttribute("warehouses", warehouseRepository.findAll());
        model.addAttribute("suppliers", supplierRepository.findAll());
        return "admin/product-form";
    }

    @PostMapping("/products/new")
    public String createProduct(@Valid @ModelAttribute Product product, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("categories", categoryRepository.findAll());
            model.addAttribute("warehouses", warehouseRepository.findAll());
            model.addAttribute("suppliers", supplierRepository.findAll());
            return "admin/product-form";
        }
        productRepository.save(product);
        return "redirect:/admin/products";
    }

    @GetMapping("/products/edit/{id}")
    public String editProductForm(@PathVariable Long id, Model model) {
        Optional<Product> product = productRepository.findById(id);
        if (product.isPresent()) {
            model.addAttribute("product", product.get());
            model.addAttribute("categories", categoryRepository.findAll());
            model.addAttribute("warehouses", warehouseRepository.findAll());
            model.addAttribute("suppliers", supplierRepository.findAll());
            return "admin/product-form";
        }
        return "redirect:/admin/products";
    }

    @PostMapping("/products/edit/{id}")
    public String updateProduct(@PathVariable Long id, @Valid @ModelAttribute Product product, 
                               BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("categories", categoryRepository.findAll());
            model.addAttribute("warehouses", warehouseRepository.findAll());
            model.addAttribute("suppliers", supplierRepository.findAll());
            return "admin/product-form";
        }
        productRepository.save(product);
        return "redirect:/admin/products";
    }

    @PostMapping("/products/delete/{id}")
    public String deleteProduct(@PathVariable Long id) {
        productRepository.deleteById(id);
        return "redirect:/admin/products";
    }

    // ==================== CATEGORIES ====================
    @GetMapping("/categories")
    public String categoriesPage(Model model) {
        model.addAttribute("categories", categoryRepository.findAll());
        return "admin/categories";
    }

    @GetMapping("/categories/new")
    public String newCategoryForm(Model model) {
        model.addAttribute("category", new Category());
        return "admin/category-form";
    }

    @PostMapping("/categories/new")
    public String createCategory(@Valid @ModelAttribute Category category, BindingResult result) {
        if (result.hasErrors()) {
            return "admin/category-form";
        }
        categoryRepository.save(category);
        return "redirect:/admin/categories";
    }

    @GetMapping("/categories/edit/{id}")
    public String editCategoryForm(@PathVariable Long id, Model model) {
        Optional<Category> category = categoryRepository.findById(id);
        if (category.isPresent()) {
            model.addAttribute("category", category.get());
            return "admin/category-form";
        }
        return "redirect:/admin/categories";
    }

    @PostMapping("/categories/edit/{id}")
    public String updateCategory(@PathVariable Long id, @Valid @ModelAttribute Category category, 
                                BindingResult result) {
        if (result.hasErrors()) {
            return "admin/category-form";
        }
        categoryRepository.save(category);
        return "redirect:/admin/categories";
    }

    @PostMapping("/categories/delete/{id}")
    public String deleteCategory(@PathVariable Long id) {
        categoryRepository.deleteById(id);
        return "redirect:/admin/categories";
    }

    // ==================== WAREHOUSES ====================
    @GetMapping("/warehouses")
    public String warehousesPage(Model model) {
        model.addAttribute("warehouses", warehouseRepository.findAll());
        return "admin/warehouses";
    }

    @GetMapping("/warehouses/new")
    public String newWarehouseForm(Model model) {
        model.addAttribute("warehouse", new Warehouse());
        return "admin/warehouse-form";
    }

    @PostMapping("/warehouses/new")
    public String createWarehouse(@Valid @ModelAttribute Warehouse warehouse, BindingResult result) {
        if (result.hasErrors()) {
            return "admin/warehouse-form";
        }
        warehouseRepository.save(warehouse);
        return "redirect:/admin/warehouses";
    }

    @GetMapping("/warehouses/edit/{id}")
    public String editWarehouseForm(@PathVariable Long id, Model model) {
        Optional<Warehouse> warehouse = warehouseRepository.findById(id);
        if (warehouse.isPresent()) {
            model.addAttribute("warehouse", warehouse.get());
            return "admin/warehouse-form";
        }
        return "redirect:/admin/warehouses";
    }

    @PostMapping("/warehouses/edit/{id}")
    public String updateWarehouse(@PathVariable Long id, @Valid @ModelAttribute Warehouse warehouse, 
                                 BindingResult result) {
        if (result.hasErrors()) {
            return "admin/warehouse-form";
        }
        warehouseRepository.save(warehouse);
        return "redirect:/admin/warehouses";
    }

    @PostMapping("/warehouses/delete/{id}")
    public String deleteWarehouse(@PathVariable Long id) {
        warehouseRepository.deleteById(id);
        return "redirect:/admin/warehouses";
    }

    // ==================== SUPPLIERS ====================
    @GetMapping("/suppliers")
    public String suppliersPage(Model model) {
        model.addAttribute("suppliers", supplierRepository.findAll());
        return "admin/suppliers";
    }

    @GetMapping("/suppliers/new")
    public String newSupplierForm(Model model) {
        model.addAttribute("supplier", new Supplier());
        model.addAttribute("users", userRepository.findAll());
        return "admin/supplier-form";
    }

    @PostMapping("/suppliers/new")
    public String createSupplier(@Valid @ModelAttribute Supplier supplier, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("users", userRepository.findAll());
            return "admin/supplier-form";
        }
        supplierRepository.save(supplier);
        return "redirect:/admin/suppliers";
    }

    @GetMapping("/suppliers/edit/{id}")
    public String editSupplierForm(@PathVariable Long id, Model model) {
        Optional<Supplier> supplier = supplierRepository.findById(id);
        if (supplier.isPresent()) {
            model.addAttribute("supplier", supplier.get());
            model.addAttribute("users", userRepository.findAll());
            return "admin/supplier-form";
        }
        return "redirect:/admin/suppliers";
    }

    @PostMapping("/suppliers/edit/{id}")
    public String updateSupplier(@PathVariable Long id, @Valid @ModelAttribute Supplier supplier, 
                                BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("users", userRepository.findAll());
            return "admin/supplier-form";
        }
        supplierRepository.save(supplier);
        return "redirect:/admin/suppliers";
    }

    @PostMapping("/suppliers/delete/{id}")
    public String deleteSupplier(@PathVariable Long id) {
        supplierRepository.deleteById(id);
        return "redirect:/admin/suppliers";
    }

    // ==================== ORDERS ====================
    @GetMapping("/orders")
    public String ordersPage(Model model) {
        model.addAttribute("orders", orderRepository.findAll());
        return "admin/orders";
    }

    @GetMapping("/orders/{id}")
    public String orderDetails(@PathVariable Long id, Model model) {
        Optional<Order> order = orderRepository.findById(id);
        if (order.isPresent()) {
            model.addAttribute("order", order.get());
            return "admin/order-details";
        }
        return "redirect:/admin/orders";
    }

    // ==================== REVIEWS ====================
    @GetMapping("/reviews")
    public String reviewsPage(Model model) {
        model.addAttribute("reviews", reviewRepository.findAll());
        return "admin/reviews";
    }

    @PostMapping("/reviews/delete/{id}")
    public String deleteReview(@PathVariable Long id) {
        reviewRepository.deleteById(id);
        return "redirect:/admin/reviews";
    }
    
    // ==================== ROLES ====================
    @GetMapping("/roles")
    public String rolesPage(Model model) {
        model.addAttribute("roles", roleRepository.findAll());
        return "admin/roles";
    }
    
    @GetMapping("/roles/new")
    public String newRoleForm(Model model) {
        model.addAttribute("role", new Role());
        return "admin/role-form";
    }
    
    @PostMapping("/roles/new")
    public String createRole(@Valid @ModelAttribute Role role, BindingResult result) {
        if (result.hasErrors()) {
            return "admin/role-form";
        }
        roleRepository.save(role);
        return "redirect:/admin/roles";
    }
    
    @GetMapping("/roles/edit/{id}")
    public String editRoleForm(@PathVariable Long id, Model model) {
        Optional<Role> role = roleRepository.findById(id);
        if (role.isPresent()) {
            model.addAttribute("role", role.get());
            return "admin/role-form";
        }
        return "redirect:/admin/roles";
    }
    
    @PostMapping("/roles/edit/{id}")
    public String updateRole(@PathVariable Long id, @Valid @ModelAttribute Role role, BindingResult result) {
        if (result.hasErrors()) {
            return "admin/role-form";
        }
        roleRepository.save(role);
        return "redirect:/admin/roles";
    }
    
    @PostMapping("/roles/delete/{id}")
    public String deleteRole(@PathVariable Long id) {
        roleRepository.deleteById(id);
        return "redirect:/admin/roles";
    }
}

