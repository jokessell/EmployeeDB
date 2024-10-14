// src/main/java/com/example/dto/EmployeeDto.java
package com.example.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeDto {
    private String name;
    private LocalDate dateOfBirth;
    private String avatarUrl;
    private String jobRole;
    private String gender;
    private Set<ProjectDto> projects;
}
