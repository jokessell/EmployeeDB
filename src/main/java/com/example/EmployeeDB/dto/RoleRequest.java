package com.example.EmployeeDB.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoleRequest {

    @NotBlank
    private String name; // e.g., BASIC, ELEVATED, ADMIN
}
