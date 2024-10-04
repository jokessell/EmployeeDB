package com.example.service;

import com.example.dto.EmployeeDto;
import com.example.entity.Employee;
import com.example.exception.ResourceNotFoundException;
import com.example.mapper.EmployeeMapper;
import com.example.repository.EmployeeRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final EmployeeMapper employeeMapper;

    public EmployeeService(EmployeeRepository employeeRepository, EmployeeMapper employeeMapper) {
        this.employeeRepository = employeeRepository;
        this.employeeMapper = employeeMapper;
    }

    // Returning full Employee data
    public List<Employee> getAllEmployees() {
        List<Employee> employees = employeeRepository.findAll();
        if (employees.isEmpty()) {
            throw new ResourceNotFoundException("No employees found.");
        }
        return employees;
    }

    public Employee getEmployeeById(Long employeeId) {
        if (employeeId == null || employeeId <= 0) {
            throw new IllegalArgumentException("Invalid employee ID.");
        }
        return employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with ID: " + employeeId));
    }

    // Returning Employee after processing data and saving
    public Employee createEmployee(EmployeeDto employeeDto) {
        validateEmployeeInput(employeeDto);
        Employee employee = employeeMapper.toEntity(employeeDto);
        processEmployeeData(employee);
        return employeeRepository.save(employee);
    }

    public Employee updateEmployee(Long employeeId, EmployeeDto employeeDto) {
        if (employeeId == null || employeeId <= 0) {
            throw new IllegalArgumentException("Invalid employee ID.");
        }
        validateEmployeeInput(employeeDto);
        Employee existingEmployee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with ID: " + employeeId));
        employeeMapper.updateFromDto(employeeDto, existingEmployee);
        processEmployeeData(existingEmployee);
        return employeeRepository.save(existingEmployee);
    }

    public void deleteEmployee(Long employeeId) {
        if (employeeId == null || employeeId <= 0) {
            throw new IllegalArgumentException("Invalid employee ID.");
        }
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with ID: " + employeeId));
        employeeRepository.delete(employee);
    }

    // Batch creation
    public List<Employee> createEmployeesBatch(List<EmployeeDto> employeeDtos) {
        if (employeeDtos == null || employeeDtos.isEmpty()) {
            throw new IllegalArgumentException("Employee list cannot be null or empty.");
        }
        List<Employee> employees = employeeDtos.stream()
                .peek(this::validateEmployeeInput)
                .map(employeeMapper::toEntity)
                .peek(this::processEmployeeData)
                .collect(Collectors.toList());
        return employeeRepository.saveAll(employees);
    }

    // Process employee to calculate age and generate email
    private void processEmployeeData(Employee employee) {
        employee.setAge(calculateAge(employee.getDateOfBirth()));
        employee.setEmail(generateEmail(employee.getName()));
    }

    private int calculateAge(LocalDate dateOfBirth) {
        return Period.between(dateOfBirth, LocalDate.now()).getYears();
    }

    private String generateEmail(String name) {
        String[] nameParts = name.split(" ");
        if (nameParts.length >= 2) {
            return nameParts[0].toLowerCase() + "." + nameParts[1].toLowerCase() + "@email.com";
        } else {
            return nameParts[0].toLowerCase() + "@email.com";
        }
    }

    // Validation logic for input
    private void validateEmployeeInput(EmployeeDto employeeDto) {
        List<Supplier<Boolean>> validations = List.of(
                () -> employeeDto.getName() == null || employeeDto.getName().trim().isEmpty(),
                () -> employeeDto.getDateOfBirth() == null || employeeDto.getDateOfBirth().isAfter(LocalDate.now()),
                () -> employeeDto.getJobRole() == null || employeeDto.getJobRole().trim().isEmpty(),
                () -> employeeDto.getGender() == null || employeeDto.getGender().trim().isEmpty()
        );
        List<String> errorMessages = List.of(
                "Employee name is required.",
                "Invalid date of birth.",
                "Job role is required.",
                "Gender is required."
        );
        for (int i = 0; i < validations.size(); i++) {
            if (validations.get(i).get()) {
                throw new IllegalArgumentException(errorMessages.get(i));
            }
        }
    }
}
