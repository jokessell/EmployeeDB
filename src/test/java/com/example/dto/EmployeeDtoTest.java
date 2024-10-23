// src/test/java/com/example/dto/EmployeeDtoTest.java
package com.example.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.Set;

public class EmployeeDtoTest {

    @Test
    void testEmployeeDtoGettersAndSetters() {
        EmployeeDto employeeDto = new EmployeeDto();
        employeeDto.setEmployeeId(1L);
        employeeDto.setName("John Doe");
        employeeDto.setDateOfBirth(LocalDate.of(1990, 1, 1));
        employeeDto.setAvatarUrl("http://example.com/avatar.png");
        employeeDto.setJobRole("Developer");
        employeeDto.setGender("Male");
        employeeDto.setEmail("john.doe@example.com");
        employeeDto.setSkillIds(Set.of(1L, 2L));
        employeeDto.setProjectIds(Set.of(3L, 4L));

        assertEquals(1L, employeeDto.getEmployeeId());
        assertEquals("John Doe", employeeDto.getName());
        assertEquals(LocalDate.of(1990, 1, 1), employeeDto.getDateOfBirth());
        assertEquals("http://example.com/avatar.png", employeeDto.getAvatarUrl());
        assertEquals("Developer", employeeDto.getJobRole());
        assertEquals("Male", employeeDto.getGender());
        assertEquals("john.doe@example.com", employeeDto.getEmail());
        assertEquals(Set.of(1L, 2L), employeeDto.getSkillIds());
        assertEquals(Set.of(3L, 4L), employeeDto.getProjectIds());
    }
}