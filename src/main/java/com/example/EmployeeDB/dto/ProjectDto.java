package com.example.EmployeeDB.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    private Set<Long> employeeIds; // For input purposes (e.g., PATCH requests)
    private Set<Long> skillIds;    // For input purposes (e.g., PATCH requests)

    private Set<EmployeeDto> employees; // Populated in GET responses
    private Set<SkillDto> skills;       // Populated in GET responses
}
