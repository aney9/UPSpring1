package com.example.demo.config;

import com.example.demo.model.Role;
import com.example.demo.model.RoleEnum;
import com.example.demo.model.User;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private RoleRepository roleRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Создаем роли если их еще нет
        initializeRoles();
        
        // Создаем админа если его еще нет
        initializeAdmin();
    }

    private void initializeRoles() {
        if (roleRepository.count() == 0) {
            Role clientRole = new Role();
            clientRole.setName("CLIENT");
            clientRole.setDescription("Обычный клиент зоомагазина");
            roleRepository.save(clientRole);
            
            Role supplierRole = new Role();
            supplierRole.setName("SUPPLIER");
            supplierRole.setDescription("Поставщик товаров");
            roleRepository.save(supplierRole);
            
            Role adminRole = new Role();
            adminRole.setName("ADMIN");
            adminRole.setDescription("Администратор системы");
            roleRepository.save(adminRole);
            
            System.out.println("Роли инициализированы");
        }
    }

    private void initializeAdmin() {
        if (!userRepository.existsByUsername("admin")) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setEmail("admin@zoo-shop.ru");
            admin.setPassword(passwordEncoder.encode("admin"));
            admin.setRole(RoleEnum.ADMIN);
            admin.setFullName("Администратор Системы");
            admin.setEnabled(true);
            
            userRepository.save(admin);
            
            System.out.println("Админ создан: username=admin, password=admin");
        }
    }
}


