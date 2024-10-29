package com.example.EmployeeDB.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssignRoleRequest {

    @NotBlank
    private String username;

    @NotEmpty
    private Set<String> roles; // Roles to assign
}
