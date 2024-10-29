package com.example.EmployeeDB.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeDto {
    private Long employeeId;

    @NotBlank(message = "Name is required.")
    private String name;

    @NotNull(message = "Date of birth is required.")
    private LocalDate dateOfBirth;

    private String avatarUrl;

    @NotBlank(message = "Job role is required.")
    private String jobRole;

    @NotBlank(message = "Gender is required.")
    private String gender;

    private int age;

    private String email;

    private Set<SkillDto> skills;       // Detailed skills
    private Set<Long> skillIds;         // For input purposes

    private Set<ProjectDto> projects;   // Detailed projects with skills
    private Set<Long> projectIds;       // For input purposes
}
