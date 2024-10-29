package com.example.EmployeeDB.configuration;

import com.example.EmployeeDB.entity.Role;
import com.example.EmployeeDB.entity.User;
import com.example.EmployeeDB.repository.RoleRepository;
import com.example.EmployeeDB.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class DataInitializer {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(RoleRepository roleRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Value("${admin.security.username}")
    private String adminUsername;

    @Value("${admin.security.password}")
    private String adminPassword;


    @PostConstruct
    public void init() {
        // Initialize roles only if they don't exist
        List<String> roles = Arrays.asList("BASIC", "ELEVATED", "ADMIN");
        for (String roleName : roles) {
            roleRepository.findByRoleName(roleName).orElseGet(() -> {
                Role role = new Role();
                role.setRoleName(roleName);
                return roleRepository.save(role);
            });
        }

        // Initialize admin user if not exists
        if (!userRepository.existsByUserName(adminUsername)) {
            User admin = new User();
            admin.setUserName(adminUsername);
            admin.setPassword(passwordEncoder.encode(adminPassword)); // Use a secure password
            Set<Role> adminRoles = new HashSet<>();
            Role adminRole = roleRepository.findByRoleName("ADMIN")
                    .orElseThrow(() -> new RuntimeException("ADMIN role not found."));
            adminRoles.add(adminRole);
            admin.setRoles(adminRoles);
            userRepository.save(admin);
            System.out.println("Admin user created with username '"+ adminUsername + "' and password '" + adminPassword + "'.");
        }
    }
}
