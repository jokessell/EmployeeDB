// src/test/java/com/example/controller/EmployeeControllerTest.java
package com.example.controller;

import com.example.dto.EmployeeDto;
import com.example.entity.Employee;
import com.example.service.EmployeeService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EmployeeController.class)
public class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmployeeService employeeService;

    @Test
    void testGetAllEmployees() throws Exception {
        Employee employee = new Employee(1L, "John Doe", LocalDate.of(1990, 1, 1), "http://example.com/avatar.png", "Developer", "Male", 30, "john.doe@example.com", null, null);

        // Mocking pageable
        Pageable pageable = PageRequest.of(0, 10);
        Page<Employee> employeePage = new PageImpl<>(List.of(employee), pageable, 1);
        Mockito.when(employeeService.getAllEmployees(Mockito.any(Pageable.class))).thenReturn(employeePage);

        mockMvc.perform(get("/api/employees").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].employeeId").value(1L))
                .andExpect(jsonPath("$.content[0].name").value("John Doe"));
    }


    @Test
    void testGetEmployeeById() throws Exception {
        Employee employee = new Employee(1L, "John Doe", LocalDate.of(1990, 1, 1), "http://example.com/avatar.png", "Developer", "Male", 30, "john.doe@example.com", null, null);
        Mockito.when(employeeService.getEmployeeById(1L)).thenReturn(employee);

        mockMvc.perform(get("/api/employees/1").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.employeeId").value(1L))
                .andExpect(jsonPath("$.name").value("John Doe"));
    }

    @Test
    void testCreateEmployee() throws Exception {
        EmployeeDto employeeDto = new EmployeeDto(null, "John Doe", LocalDate.of(1990, 1, 1), "http://example.com/avatar.png", "Developer", "Male", null, null, null, null);
        Employee createdEmployee = new Employee(1L, "John Doe", LocalDate.of(1990, 1, 1), "http://example.com/avatar.png", "Developer", "Male", 30, "john.doe@example.com", null, null);
        Mockito.when(employeeService.createEmployee(Mockito.any(EmployeeDto.class))).thenReturn(createdEmployee);

        mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"John Doe\", \"dateOfBirth\": \"1990-01-01\", \"avatarUrl\": \"http://example.com/avatar.png\", \"jobRole\": \"Developer\", \"gender\": \"Male\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.employeeId").value(1L))
                .andExpect(jsonPath("$.name").value("John Doe"));
    }

    @Test
    void testUpdateEmployee() throws Exception {
        EmployeeDto employeeDto = new EmployeeDto(null, "John Doe Updated", LocalDate.of(1990, 1, 1), "http://example.com/avatar.png", "Senior Developer", "Male", null, null, null, null);
        Employee updatedEmployee = new Employee(1L, "John Doe Updated", LocalDate.of(1990, 1, 1), "http://example.com/avatar.png", "Senior Developer", "Male", 30, "john.doe@example.com", null, null);
        Mockito.when(employeeService.updateEmployee(Mockito.eq(1L), Mockito.any(EmployeeDto.class))).thenReturn(updatedEmployee);

        mockMvc.perform(put("/api/employees/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"John Doe Updated\", \"dateOfBirth\": \"1990-01-01\", \"avatarUrl\": \"http://example.com/avatar.png\", \"jobRole\": \"Senior Developer\", \"gender\": \"Male\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.employeeId").value(1L))
                .andExpect(jsonPath("$.name").value("John Doe Updated"));
    }

    @Test
    void testDeleteEmployee() throws Exception {
        Mockito.doNothing().when(employeeService).deleteEmployee(1L);

        mockMvc.perform(delete("/api/employees/1").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }
}