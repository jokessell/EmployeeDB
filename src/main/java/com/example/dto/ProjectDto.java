// src/main/java/com/example/dto/ProjectDto.java
package com.example.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProjectDto {
    private Long projectId;

    @NotBlank(message = "Project name is required.")
    @Size(max = 255, message = "Project name cannot exceed 255 characters.")
    private String projectName;

    @Size(max = 1024, message = "Description cannot exceed 1024 characters.")
    private String description;

    @NotNull(message = "At least one Employee ID is required.")
    private Set<Long> employeeIds; // To associate with Employees

    private Set<Long> skillIds; // To associate with Skills
}
