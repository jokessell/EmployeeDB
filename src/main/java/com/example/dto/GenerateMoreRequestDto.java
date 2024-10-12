package com.example.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GenerateMoreRequestDto {
    @NotBlank(message = "Topic is required.")
    private String topic;

    @Min(value = 1, message = "Record count must be at least 1.")
    private int recordCount;

    @NotEmpty(message = "Properties list cannot be empty.")
    private List<String> properties;
}
