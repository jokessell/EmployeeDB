package com.example.EmployeeDB.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInputDto {
    private String topic;         // The user-provided topic (e.g., "Flight Schedules")
    private int propertyCount;    // Number of properties (e.g., 3 to 10)
    private int recordCount;      // Number of records (e.g., 5 to 50)
}
