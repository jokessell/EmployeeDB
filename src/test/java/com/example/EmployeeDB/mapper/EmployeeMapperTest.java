package com.example.EmployeeDB.mapper;

import com.example.EmployeeDB.dto.EmployeeDto;
import com.example.EmployeeDB.entity.Employee;
import com.example.EmployeeDB.mapper.EmployeeMapper;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDate;

public class EmployeeMapperTest {

    private final EmployeeMapper employeeMapper = Mappers.getMapper(EmployeeMapper.class);

    @Test
    void testToEntity() {
        EmployeeDto employeeDto = new EmployeeDto();
        employeeDto.setName("John Doe");
        employeeDto.setDateOfBirth(LocalDate.of(1990, 1, 1));
        employeeDto.setJobRole("Developer");
        employeeDto.setGender("Male");

        Employee employee = employeeMapper.toEntity(employeeDto);

        assertNotNull(employee);
        assertEquals("John Doe", employee.getName());
        assertEquals(LocalDate.of(1990, 1, 1), employee.getDateOfBirth());
        assertEquals("Developer", employee.getJobRole());
        assertEquals("Male", employee.getGender());
    }

    @Test
    void testToDto() {
        Employee employee = new Employee();
        employee.setEmployeeId(1L);
        employee.setName("John Doe");
        employee.setDateOfBirth(LocalDate.of(1990, 1, 1));
        employee.setJobRole("Developer");
        employee.setGender("Male");

        EmployeeDto employeeDto = employeeMapper.toDto(employee);

        assertNotNull(employeeDto);
        assertEquals(1L, employeeDto.getEmployeeId());
        assertEquals("John Doe", employeeDto.getName());
        assertEquals(LocalDate.of(1990, 1, 1), employeeDto.getDateOfBirth());
        assertEquals("Developer", employeeDto.getJobRole());
        assertEquals("Male", employeeDto.getGender());
    }
}
