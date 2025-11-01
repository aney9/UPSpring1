package com.example.demo;

import com.example.demo.model.Product;
import com.example.demo.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class ProductControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ProductRepository productRepository;
    
    @Test
    public void testCreateProduct() throws Exception {
        String productJson = "{\"name\":\"Test Product\",\"description\":\"Test Description\",\"price\":99.99,\"quantity\":10,\"categoryId\":1}";
        
        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(productJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Test Product"))
                .andExpect(jsonPath("$.price").value(99.99));
    }
    
    @Test
    public void testGetAllProducts() throws Exception {
        // Создаем тестовый продукт
        Product product = new Product();
        product.setName("Test Product");
        product.setDescription("Test Description");
        product.setPrice(new BigDecimal("99.99"));
        product.setQuantity(10);
        product.setCategoryId(1L);
        productRepository.save(product);
        
        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }
    
    @Test
    public void testGetProductById() throws Exception {
        // Создаем тестовый продукт
        Product product = new Product();
        product.setName("Test Product");
        product.setDescription("Test Description");
        product.setPrice(new BigDecimal("99.99"));
        product.setQuantity(10);
        product.setCategoryId(1L);
        Product savedProduct = productRepository.save(product);
        
        mockMvc.perform(get("/api/products/" + savedProduct.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedProduct.getId()))
                .andExpect(jsonPath("$.name").value("Test Product"));
    }
    
    @Test
    public void testUpdateProduct() throws Exception {
        // Создаем тестовый продукт
        Product product = new Product();
        product.setName("Original Name");
        product.setDescription("Original Description");
        product.setPrice(new BigDecimal("50.00"));
        product.setQuantity(5);
        product.setCategoryId(1L);
        Product savedProduct = productRepository.save(product);
        
        String updatedJson = "{\"name\":\"Updated Name\",\"description\":\"Updated Description\",\"price\":75.50,\"quantity\":15,\"categoryId\":2}";
        
        mockMvc.perform(put("/api/products/" + savedProduct.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(updatedJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Name"))
                .andExpect(jsonPath("$.price").value(75.50));
    }
    
    @Test
    public void testDeleteProduct() throws Exception {
        // Создаем тестовый продукт
        Product product = new Product();
        product.setName("Product to Delete");
        product.setDescription("Description");
        product.setPrice(new BigDecimal("10.00"));
        product.setQuantity(1);
        product.setCategoryId(1L);
        Product savedProduct = productRepository.save(product);
        
        mockMvc.perform(delete("/api/products/" + savedProduct.getId()))
                .andExpect(status().isNoContent());
        
        // Проверяем, что продукт удален
        mockMvc.perform(get("/api/products/" + savedProduct.getId()))
                .andExpect(status().isNotFound());
    }
    
    @Test
    public void testGetProductByIdNotFound() throws Exception {
        mockMvc.perform(get("/api/products/99999"))
                .andExpect(status().isNotFound());
    }
}
