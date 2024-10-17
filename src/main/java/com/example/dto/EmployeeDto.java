// src/main/java/com/example/dto/EmployeeDto.java
package com.example.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.Set;

@Data
public class EmployeeDto {
    private Long employeeId;
    private String name;
    private LocalDate dateOfBirth;
    private String avatarUrl;
    private String jobRole;
    private String gender;
    private String email;
    private Set<Long> skillIds;    // For input purposes
    private Set<Long> projectIds;  // For input purposes

    // Detailed objects for output purposes
    private Set<SkillDto> skills; // Populated in GET responses
}
