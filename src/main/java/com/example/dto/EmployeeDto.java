// src/main/java/com/example/dto/EmployeeDto.java
package com.example.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeDto {
    private Long employeeId;

    @NotBlank(message = "Name is required.")
    private String name;

    @NotNull(message = "Date of Birth is required.")
    @Past(message = "Date of Birth must be in the past.")
    private LocalDate dateOfBirth;

    private String avatarUrl;

    @NotBlank(message = "Job role is required.")
    private String jobRole;

    @NotBlank(message = "Gender is required.")
    private String gender;

    private String email;

    private Set<Long> skillIds;    // For input purposes
    private Set<Long> projectIds;  // For input purposes

    // Detailed objects for output purposes
    private Set<SkillDto> skills; // Populated in GET responses
}