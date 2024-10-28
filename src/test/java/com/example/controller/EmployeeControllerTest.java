// src/test/java/com/example/controller/EmployeeControllerTest.java
package com.example.controller;

import com.example.dto.EmployeeDto;
import com.example.service.EmployeeService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
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
        EmployeeDto employeeDto = new EmployeeDto(
                1L,
                "John Doe",
                LocalDate.of(1990, 1, 1),
                "http://example.com/avatar.png",
                "Developer",
                "Male",
                30,
                "john.doe@example.com",
                null, // skills
                null, // skillIds
                null, // projects
                null  // projectIds
        );

        // Mocking pageable
        Pageable pageable = PageRequest.of(0, 10);
        Page<EmployeeDto> employeePage = new PageImpl<>(List.of(employeeDto), pageable, 1);
        Mockito.when(employeeService.getAllEmployees(any(Pageable.class))).thenReturn(employeePage);

        mockMvc.perform(get("/api/employees").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].employeeId").value(1L))
                .andExpect(jsonPath("$.content[0].name").value("John Doe"));
    }

    @Test
    void testGetEmployeeById() throws Exception {
        EmployeeDto employeeDto = new EmployeeDto(
                1L,
                "John Doe",
                LocalDate.of(1990, 1, 1),
                "http://example.com/avatar.png",
                "Developer",
                "Male",
                30,
                "john.doe@example.com",
                null, // skills
                null, // skillIds
                null, // projects
                null  // projectIds
        );
        Mockito.when(employeeService.getEmployeeById(1L)).thenReturn(employeeDto);

        mockMvc.perform(get("/api/employees/1").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.employeeId").value(1L))
                .andExpect(jsonPath("$.name").value("John Doe"));
    }

    @Test
    void testCreateEmployee() throws Exception {
        EmployeeDto employeeDto = new EmployeeDto(
                null,
                "John Doe",
                LocalDate.of(1990, 1, 1),
                "http://example.com/avatar.png",
                "Developer",
                "Male",
                0,
                null,
                null,
                null,
                null,
                null
        );
        EmployeeDto createdEmployeeDto = new EmployeeDto(
                1L,
                "John Doe",
                LocalDate.of(1990, 1, 1),
                "http://example.com/avatar.png",
                "Developer",
                "Male",
                30,
                "john.doe@example.com",
                null,
                null,
                null,
                null
        );
        Mockito.when(employeeService.createEmployee(any(EmployeeDto.class))).thenReturn(createdEmployeeDto);

        mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"John Doe\", \"dateOfBirth\": \"1990-01-01\", \"avatarUrl\": \"http://example.com/avatar.png\", \"jobRole\": \"Developer\", \"gender\": \"Male\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.employeeId").value(1L))
                .andExpect(jsonPath("$.name").value("John Doe"));
    }

    @Test
    void testUpdateEmployee() throws Exception {
        EmployeeDto employeeDto = new EmployeeDto(
                null,
                "John Doe Updated",
                LocalDate.of(1990, 1, 1),
                "http://example.com/avatar.png",
                "Senior Developer",
                "Male",
                0,
                null,
                null,
                null,
                null,
                null
        );
        EmployeeDto updatedEmployeeDto = new EmployeeDto(
                1L,
                "John Doe Updated",
                LocalDate.of(1990, 1, 1),
                "http://example.com/avatar.png",
                "Senior Developer",
                "Male",
                30,
                "john.doe@example.com",
                null,
                null,
                null,
                null
        );
        Mockito.when(employeeService.updateEmployee(eq(1L), any(EmployeeDto.class))).thenReturn(updatedEmployeeDto);

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
