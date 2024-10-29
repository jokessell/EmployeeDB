package com.example.EmployeeDB.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SkillDto {
    private Long skillId;

    @NotBlank(message = "Skill name is required.")
    private String name;
}