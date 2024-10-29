package com.example.EmployeeDB.entity;

import com.example.EmployeeDB.entity.Employee;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

public class EmployeeTest {

    @Test
    void testEmployeeGettersAndSetters() {
        Employee employee = new Employee();
        employee.setEmployeeId(1L);
        employee.setName("Jane Doe");
        employee.setDateOfBirth(LocalDate.of(1985, 5, 15));
        employee.setAvatarUrl("http://example.com/avatar.png");
        employee.setJobRole("Manager");
        employee.setGender("Female");
        employee.setAge(35);
        employee.setEmail("jane.doe@example.com");

        assertEquals(1L, employee.getEmployeeId());
        assertEquals("Jane Doe", employee.getName());
        assertEquals(LocalDate.of(1985, 5, 15), employee.getDateOfBirth());
        assertEquals("http://example.com/avatar.png", employee.getAvatarUrl());
        assertEquals("Manager", employee.getJobRole());
        assertEquals("Female", employee.getGender());
        assertEquals(35, employee.getAge());
        assertEquals("jane.doe@example.com", employee.getEmail());
    }
}