package com.example.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeDto {
    @NotBlank(message = "Name is required.")
    private String name;

    @Past(message = "Date of birth must be in the past.")
    private LocalDate dateOfBirth;

    private String avatarUrl;

    @NotBlank(message = "Job role is required.")
    private String jobRole;

    @NotBlank(message = "Gender is required.")
    private String gender;
}
