// src/main/java/com/example/service/EmployeeService.java
package com.example.service;

import com.example.dto.EmployeeDto;
import com.example.entity.Employee;
import com.example.entity.Project;
import com.example.entity.Skill;
import com.example.exception.InvalidInputException;
import com.example.exception.ResourceNotFoundException;
import com.example.mapper.EmployeeMapper;
import com.example.repository.EmployeeRepository;
import com.example.repository.ProjectRepository;
import com.example.repository.SkillRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final ProjectRepository projectRepository;
    private final SkillRepository skillRepository;
    private final EmployeeMapper employeeMapper;

    // Returning paginated Employee data
    public Page<Employee> getAllEmployees(Pageable pageable) {
        Page<Employee> employees = employeeRepository.findAll(pageable);
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
    @Transactional
    public Employee createEmployee(EmployeeDto employeeDto) {
        validateEmployeeInput(employeeDto);
        Employee employee = employeeMapper.toEntity(employeeDto);
        processEmployeeData(employee, employeeDto);
        return employeeRepository.save(employee);
    }

    @Transactional
    public Employee updateEmployee(Long employeeId, EmployeeDto dto) {
        // Fetch the employee by ID
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EntityNotFoundException("Employee not found with ID: " + employeeId));

        // Update basic fields
        employee.setName(dto.getName());
        employee.setDateOfBirth(dto.getDateOfBirth());
        employee.setAvatarUrl(dto.getAvatarUrl());
        employee.setJobRole(dto.getJobRole());
        employee.setGender(dto.getGender());
        employee.setEmail(generateEmail(dto.getName()));

        // Fetch and set skills
        Set<Skill> skills = new HashSet<>(skillRepository.findAllById(dto.getSkillIds()));
        if (skills.size() != dto.getSkillIds().size()) {
            throw new EntityNotFoundException("One or more skills not found.");
        }

        // Clear existing skills and set new ones
        employee.getSkills().clear();
        employee.getSkills().addAll(skills);

        // Save the updated employee
        return employeeRepository.save(employee);
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
    @Transactional
    public List<Employee> createEmployeesBatch(List<EmployeeDto> employeeDtos) {
        if (employeeDtos == null || employeeDtos.isEmpty()) {
            throw new IllegalArgumentException("Employee list cannot be null or empty.");
        }
        List<Employee> employees = employeeDtos.stream()
                .peek(this::validateEmployeeInput)
                .map(employeeMapper::toEntity)
                .collect(Collectors.toList());
        employees.forEach(employee -> processEmployeeData(employee, null));
        return employeeRepository.saveAll(employees);
    }

    // Process employee to calculate age, generate email, and set projects and skills
    private void processEmployeeData(Employee employee, EmployeeDto employeeDto) {
        if (employeeDto != null) {
            // Set Projects
            if (employeeDto.getProjectIds() != null && !employeeDto.getProjectIds().isEmpty()) {
                Set<Project> projects = employeeDto.getProjectIds().stream()
                        .map(projectRepository::findById)
                        .filter(java.util.Optional::isPresent)
                        .map(java.util.Optional::get)
                        .collect(Collectors.toSet());
                employee.setProjects(projects);
            }

            // Set Skills
            if (employeeDto.getSkillIds() != null && !employeeDto.getSkillIds().isEmpty()) {
                Set<Skill> skills = employeeDto.getSkillIds().stream()
                        .map(skillRepository::findById)
                        .filter(java.util.Optional::isPresent)
                        .map(java.util.Optional::get)
                        .collect(Collectors.toSet());
                employee.setSkills(skills);
            }
        }

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
        if (employeeDto.getName() == null || employeeDto.getName().trim().isEmpty()) {
            throw new InvalidInputException("Employee name is required.");
        }

        if (employeeDto.getDateOfBirth() == null || employeeDto.getDateOfBirth().isAfter(LocalDate.now())) {
            throw new InvalidInputException("Invalid date of birth. It cannot be in the future.");
        }

        if (employeeDto.getJobRole() == null || employeeDto.getJobRole().trim().isEmpty()) {
            throw new InvalidInputException("Job role is required.");
        }

        if (employeeDto.getGender() == null || employeeDto.getGender().trim().isEmpty()) {
            throw new InvalidInputException("Gender is required.");
        }

    }
}
