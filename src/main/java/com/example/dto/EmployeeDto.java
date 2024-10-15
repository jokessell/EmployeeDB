// src/main/java/com/example/dto/EmployeeDto.java
package com.example.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.Set;

@Data
public class EmployeeDto {
    private String name;
    private LocalDate dateOfBirth;
    private String avatarUrl;
    private String jobRole;
    private String gender;
    private String email;
    private Set<Long> skillIds;
    private Set<Long> projectIds;
}
