package com.example.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;



@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInputDto {
    @NotBlank(message = "Topic is required.")
    private String topic;         // The user-provided topic (e.g., "Flight Schedules")

    @Min(value = 1, message = "Property count must be at least 1.")
    private int propertyCount;    // Number of properties (e.g., 3 to 10)

    @Min(value = 1, message = "Record count must be at least 1.")
    private int recordCount;      // Number of records (e.g., 5 to 50)
}
