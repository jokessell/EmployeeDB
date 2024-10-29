package com.example.EmployeeDB.controller;

import com.example.EmployeeDB.dto.*;
import com.example.EmployeeDB.entity.Role;
import com.example.EmployeeDB.entity.User;
import com.example.EmployeeDB.repository.RoleRepository;
import com.example.EmployeeDB.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;

    // Create a new role
    @PostMapping("/roles")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createRole(@Valid @RequestBody RoleRequest roleRequest) {
        if (roleRepository.findByRoleName(roleRequest.getName()).isPresent()) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Role already exists."));
        }

        Role role = new Role();
        role.setRoleName(roleRequest.getName());
        roleRepository.save(role);

        return ResponseEntity.ok(new MessageResponse("Role created successfully."));
    }

    // Assign roles to a user
    @PostMapping("/assign-role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> assignRoleToUser(@Valid @RequestBody AssignRoleRequest assignRoleRequest) {
        Optional<User> userOpt = userRepository.findByUserName(assignRoleRequest.getUsername());
        if (userOpt.isEmpty()) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: User not found."));
        }

        User user = userOpt.get();
        Set<Role> rolesToAssign = new HashSet<>();

        for (String roleName : assignRoleRequest.getRoles()) {
            Role role = roleRepository.findByRoleName(roleName)
                    .orElseThrow(() -> new RuntimeException("Error: Role " + roleName + " not found."));
            rolesToAssign.add(role);
        }

        user.setRoles(rolesToAssign);
        userRepository.save(user);

        return ResponseEntity.ok(new MessageResponse("Roles assigned to user successfully."));
    }

    // Get all roles
    @GetMapping("/roles")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAllRoles() {
        List<Role> roles = roleRepository.findAll();
        return ResponseEntity.ok(roles);
    }

    // Get all users
    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAllUsers() {
        List<User> users = userRepository.findAll();
        // Exclude passwords from the response
        List<UserResponse> userResponses = users.stream().map(user -> new UserResponse(
                user.getUserId(),
                user.getUserName(),
                user.getRoles().stream().map(Role::getRoleName).collect(Collectors.toSet())
        )).collect(Collectors.toList());
        return ResponseEntity.ok(userResponses);
    }
}
