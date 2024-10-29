package com.example.EmployeeDB.service;

import com.example.EmployeeDB.dto.EmployeeDto;
import com.example.EmployeeDB.entity.Employee;
import com.example.EmployeeDB.entity.Project;
import com.example.EmployeeDB.entity.Skill;
import com.example.EmployeeDB.exception.InvalidInputException;
import com.example.EmployeeDB.exception.ResourceNotFoundException;
import com.example.EmployeeDB.mapper.EmployeeMapper;
import com.example.EmployeeDB.repository.EmployeeRepository;
import com.example.EmployeeDB.repository.ProjectRepository;
import com.example.EmployeeDB.repository.SkillRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final ProjectRepository projectRepository;
    private final SkillRepository skillRepository;
    private final EmployeeMapper employeeMapper;

    public Page<EmployeeDto> getAllEmployees(Pageable pageable) {
        Page<Employee> employees = employeeRepository.findAll(pageable);
        if (employees.isEmpty()) {
            throw new ResourceNotFoundException("No employees found.");
        }
        return employees.map(employeeMapper::toDto);
    }

    public EmployeeDto getEmployeeById(Long employeeId) {
        if (employeeId == null || employeeId <= 0) {
            throw new IllegalArgumentException("Invalid employee ID.");
        }
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with ID: " + employeeId));
        return employeeMapper.toDto(employee);
    }

    public EmployeeDto createEmployee(EmployeeDto employeeDto) {
        validateEmployeeInput(employeeDto);
        Employee employee = employeeMapper.toEntity(employeeDto);

        Set<Skill> skills = new HashSet<>();
        if (employeeDto.getSkillIds() != null && !employeeDto.getSkillIds().isEmpty()) {
            skills.addAll(skillRepository.findAllById(employeeDto.getSkillIds()));
            if (skills.size() != employeeDto.getSkillIds().size()) {
                throw new EntityNotFoundException("One or more skills not found.");
            }
        }
        employee.setSkills(skills);

        Set<Project> projects = new HashSet<>();
        if (employeeDto.getProjectIds() != null && !employeeDto.getProjectIds().isEmpty()) {
            projects.addAll(projectRepository.findAllById(employeeDto.getProjectIds()));
            if (projects.size() != employeeDto.getProjectIds().size()) {
                throw new EntityNotFoundException("One or more projects not found.");
            }
        }
        employee.setProjects(projects);

        processEmployeeData(employee, employeeDto);

        Employee savedEmployee = employeeRepository.save(employee);

        return employeeMapper.toDto(savedEmployee);
    }

    @Transactional
    public EmployeeDto updateEmployee(Long employeeId, EmployeeDto dto) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EntityNotFoundException("Employee not found with ID: " + employeeId));

        employee.setName(dto.getName());
        employee.setDateOfBirth(dto.getDateOfBirth());
        employee.setAvatarUrl(dto.getAvatarUrl());
        employee.setJobRole(dto.getJobRole());
        employee.setGender(dto.getGender());

        processEmployeeData(employee, dto);

        Set<Skill> skills = new HashSet<>();
        if (dto.getSkillIds() != null && !dto.getSkillIds().isEmpty()) {
            skills.addAll(skillRepository.findAllById(dto.getSkillIds()));
            if (skills.size() != dto.getSkillIds().size()) {
                throw new EntityNotFoundException("One or more skills not found.");
            }
        }
        employee.getSkills().clear();
        employee.getSkills().addAll(skills);

        Set<Project> projects = new HashSet<>();
        if (dto.getProjectIds() != null && !dto.getProjectIds().isEmpty()) {
            projects.addAll(projectRepository.findAllById(dto.getProjectIds()));
            if (projects.size() != dto.getProjectIds().size()) {
                throw new EntityNotFoundException("One or more projects not found.");
            }
        }
        employee.getProjects().clear();
        employee.getProjects().addAll(projects);

        Employee updatedEmployee = employeeRepository.save(employee);

        return employeeMapper.toDto(updatedEmployee);
    }

    public void deleteEmployee(Long employeeId) {
        if (employeeId == null || employeeId <= 0) {
            throw new IllegalArgumentException("Invalid employee ID.");
        }
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with ID: " + employeeId));
        employeeRepository.delete(employee);
    }

    void processEmployeeData(Employee employee, EmployeeDto employeeDto) {
        if (employeeDto != null) {
            if (employeeDto.getProjectIds() != null && !employeeDto.getProjectIds().isEmpty()) {
                Set<Project> projects = employeeDto.getProjectIds().stream()
                        .map(projectRepository::findById)
                        .filter(java.util.Optional::isPresent)
                        .map(java.util.Optional::get)
                        .collect(Collectors.toSet());
                employee.setProjects(projects);
            }

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

    int calculateAge(LocalDate dateOfBirth) {
        if (dateOfBirth == null) {
            return 0; // Or throw an exception if dateOfBirth is mandatory
        }
        return Period.between(dateOfBirth, LocalDate.now()).getYears();
    }

    String generateEmail(String name) {
        if (name == null || name.trim().isEmpty()) {
            return ""; // Or throw an exception if name is mandatory
        }
        String[] nameParts = name.trim().split("\\s+");
        if (nameParts.length >= 2) {
            return nameParts[0].toLowerCase() + "." + nameParts[1].toLowerCase() + "@email.com";
        } else {
            return nameParts[0].toLowerCase() + "@email.com";
        }
    }

    private void validateEmployeeInput(EmployeeDto employeeDto) {
        if (employeeDto.getName() == null || employeeDto.getName().trim().isEmpty()) { //Apache commonslang
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
