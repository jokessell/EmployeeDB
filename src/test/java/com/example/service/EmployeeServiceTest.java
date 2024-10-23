// src/test/java/com/example/service/EmployeeServiceTest.java
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;

import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private SkillRepository skillRepository;

    @Mock
    private EmployeeMapper employeeMapper;

    @Captor
    ArgumentCaptor<Employee> employeeCaptor;

    @InjectMocks
    private EmployeeService employeeService;

    private EmployeeDto employeeDto;
    private Employee employee;
    private Skill skill1;
    private Skill skill2;
    private Project project1;
    private Project project2;

    @BeforeEach
    void setUp() {
        // Initialize EmployeeDto
        employeeDto = new EmployeeDto();
        employeeDto.setName("John Doe");
        employeeDto.setDateOfBirth(LocalDate.of(1990, 1, 1));
        employeeDto.setJobRole("Developer");
        employeeDto.setGender("Male");
        employeeDto.setAvatarUrl("http://example.com/avatar.jpg");
        employeeDto.setEmail("john.doe@email.com");
        employeeDto.setSkillIds(new HashSet<>(Arrays.asList(1L, 2L)));
        employeeDto.setProjectIds(new HashSet<>(Arrays.asList(1L, 2L)));

        // Initialize Employee entity
        employee = new Employee();
        employee.setEmployeeId(1L);
        employee.setName("John Doe");
        employee.setDateOfBirth(employeeDto.getDateOfBirth());
        employee.setJobRole("Developer"); // Ensure this matches employeeDto.getJobRole()
        employee.setGender("Male");
        employee.setAvatarUrl(employeeDto.getAvatarUrl());
        employee.setEmail("john.doe@email.com");
        employee.setAge(33); // assuming current year is 2023
        employee.setSkills(new HashSet<>());
        employee.setProjects(new HashSet<>());

        // Initialize Skills
        skill1 = new Skill();
        skill1.setSkillId(1L);
        skill1.setName("Java");

        skill2 = new Skill();
        skill2.setSkillId(2L);
        skill2.setName("Spring");

        // Initialize Projects
        project1 = new Project();
        project1.setProjectId(1L);
        project1.setProjectName("Project Alpha");

        project2 = new Project();
        project2.setProjectId(2L);
        project2.setProjectName("Project Beta");
    }


    // **Test Cases**

    /**
     * Test getting all employees when employees exist.
     */
    @Test
    void testGetAllEmployees_Success() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        List<Employee> employees = Arrays.asList(employee);
        Page<Employee> employeePage = new PageImpl<>(employees, pageable, employees.size());

        when(employeeRepository.findAll(pageable)).thenReturn(employeePage);

        // Act
        Page<Employee> result = employeeService.getAllEmployees(pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(employees, result.getContent());
        verify(employeeRepository, times(1)).findAll(pageable);
    }

    /**
     * Test getting all employees when no employees exist.
     * Expects ResourceNotFoundException.
     */
    @Test
    void testGetAllEmployees_NoEmployees() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Employee> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);

        when(employeeRepository.findAll(pageable)).thenReturn(emptyPage);

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            employeeService.getAllEmployees(pageable);
        });

        assertEquals("No employees found.", exception.getMessage());
        verify(employeeRepository, times(1)).findAll(pageable);
    }

    /**
     * Test getting an employee by valid ID.
     */
    @Test
    void testGetEmployeeById_Success() {
        // Arrange
        Long employeeId = 1L;
        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(employee));

        // Act
        Employee result = employeeService.getEmployeeById(employeeId);

        // Assert
        assertNotNull(result);
        assertEquals(employeeId, result.getEmployeeId());
        verify(employeeRepository, times(1)).findById(employeeId);
    }

    /**
     * Test getting an employee by invalid ID (negative).
     * Expects IllegalArgumentException.
     */
    @Test
    void testGetEmployeeById_InvalidId() {
        // Arrange
        Long invalidId = -1L;

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            employeeService.getEmployeeById(invalidId);
        });

        assertEquals("Invalid employee ID.", exception.getMessage());
        verify(employeeRepository, never()).findById(anyLong());
    }

    /**
     * Test getting an employee by ID when employee does not exist.
     * Expects ResourceNotFoundException.
     */
    @Test
    void testGetEmployeeById_NotFound() {
        // Arrange
        Long employeeId = 1L;
        when(employeeRepository.findById(employeeId)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            employeeService.getEmployeeById(employeeId);
        });

        assertEquals("Employee not found with ID: 1", exception.getMessage());
        verify(employeeRepository, times(1)).findById(employeeId);
    }

    /**
     * Test creating an employee with valid data.
     */
//    @Test
//    void testCreateEmployee_Success() {
//        // Arrange
//        when(employeeMapper.toEntity(employeeDto)).thenReturn(employee);
//        when(skillRepository.findAllById(employeeDto.getSkillIds()))
//                .thenReturn(new ArrayList<>(Arrays.asList(skill1, skill2)));
//        when(projectRepository.findAllById(employeeDto.getProjectIds()))
//                .thenReturn(Arrays.asList(project1, project2));
//        when(employeeRepository.save(any(Employee.class))).thenAnswer(invocation -> invocation.getArgument(0));
//
//        // Act
//        Employee result = employeeService.createEmployee(employeeDto);
//
//        // Assert
//        assertNotNull(result, "The created employee should not be null.");
//        assertEquals(employeeDto.getName(), result.getName(), "Employee name mismatch.");
//        assertEquals(employeeDto.getJobRole(), result.getJobRole(), "Job role mismatch.");
//        assertEquals(employeeDto.getGender(), result.getGender(), "Gender mismatch.");
//        assertEquals(employeeDto.getEmail(), result.getEmail(), "Email mismatch.");
//        assertEquals(2, result.getSkills().size(), "Number of skills should be 2.");
//        assertEquals(2, result.getProjects().size(), "Number of projects should be 2.");
//
//        // Optionally, verify specific skills and projects
//        assertTrue(result.getSkills().contains(skill1), "Skill1 should be associated.");
//        assertTrue(result.getSkills().contains(skill2), "Skill2 should be associated.");
//        assertTrue(result.getProjects().contains(project1), "Project1 should be associated.");
//        assertTrue(result.getProjects().contains(project2), "Project2 should be associated.");
//
//        // Capture the Employee object passed to save
//        verify(employeeRepository, times(1)).save(employeeCaptor.capture());
//
//        Employee savedEmployee = employeeCaptor.getValue();
//        assertEquals(employeeDto.getJobRole(), savedEmployee.getJobRole(), "Job role mismatch in saved employee.");
//        assertEquals(2, savedEmployee.getProjects().size(), "Saved employee should have 2 projects.");
//    }


    /**
     * Test creating an employee with invalid input (name is null).
     * Expects InvalidInputException.
     */
    @Test
    void testCreateEmployee_InvalidInput_NameNull() {
        // Arrange
        employeeDto.setName(null);

        // Act & Assert
        InvalidInputException exception = assertThrows(InvalidInputException.class, () -> {
            employeeService.createEmployee(employeeDto);
        }, "Expected InvalidInputException when name is null.");

        assertEquals("Employee name is required.", exception.getMessage(), "Exception message should match.");

        // Verify that mapper and repositories are never called
        verify(employeeMapper, never()).toEntity(any(EmployeeDto.class));
        verify(skillRepository, never()).findAllById(anySet());
        verify(projectRepository, never()).findAllById(anySet());
        verify(employeeRepository, never()).save(any(Employee.class));
    }

    /**
     * Test creating an employee with invalid input (date of birth in the future).
     * Expects InvalidInputException.
     */
    @Test
    void testCreateEmployee_InvalidInput_DateOfBirthInFuture() {
        // Arrange
        employeeDto.setDateOfBirth(LocalDate.now().plusDays(1)); // Future date

        // Act & Assert
        InvalidInputException exception = assertThrows(InvalidInputException.class, () -> {
            employeeService.createEmployee(employeeDto);
        });

        assertEquals("Invalid date of birth. It cannot be in the future.", exception.getMessage());

        // Verify that mapper and repositories are never called
        verify(employeeMapper, never()).toEntity(any(EmployeeDto.class));
        verify(skillRepository, never()).findAllById(anySet());
        verify(projectRepository, never()).findById(anyLong());
        verify(employeeRepository, never()).save(any(Employee.class));
    }

    /**
     * Test creating an employee with invalid input (job role is null).
     * Expects InvalidInputException.
     */
    @Test
    void testCreateEmployee_InvalidInput_JobRoleNull() {
        // Arrange
        employeeDto.setJobRole(null);

        // Act & Assert
        InvalidInputException exception = assertThrows(InvalidInputException.class, () -> {
            employeeService.createEmployee(employeeDto);
        });

        assertEquals("Job role is required.", exception.getMessage());

        // Verify that mapper and repositories are never called
        verify(employeeMapper, never()).toEntity(any(EmployeeDto.class));
        verify(skillRepository, never()).findAllById(anySet());
        verify(projectRepository, never()).findById(anyLong());
        verify(employeeRepository, never()).save(any(Employee.class));
    }

    /**
     * Test creating an employee with invalid input (gender is null).
     * Expects InvalidInputException.
     */
    @Test
    void testCreateEmployee_InvalidInput_GenderNull() {
        // Arrange
        employeeDto.setGender(null);

        // Act & Assert
        InvalidInputException exception = assertThrows(InvalidInputException.class, () -> {
            employeeService.createEmployee(employeeDto);
        });

        assertEquals("Gender is required.", exception.getMessage());

        // Verify that mapper and repositories are never called
        verify(employeeMapper, never()).toEntity(any(EmployeeDto.class));
        verify(skillRepository, never()).findAllById(anySet());
        verify(projectRepository, never()).findById(anyLong());
        verify(employeeRepository, never()).save(any(Employee.class));
    }

    /**
     * Test updating an employee with invalid ID (negative).
     * Expects EntityNotFoundException.
     */
    @Test
    void testUpdateEmployee_InvalidId() {
        // Arrange
        Long invalidId = -1L;
        when(employeeRepository.findById(invalidId)).thenReturn(Optional.empty());

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            employeeService.updateEmployee(invalidId, employeeDto);
        });

        assertEquals("Employee not found with ID: -1", exception.getMessage());
        verify(employeeRepository, times(1)).findById(invalidId);
        verify(skillRepository, never()).findAllById(anySet());
        verify(projectRepository, never()).findById(anyLong());
        verify(employeeRepository, never()).save(any(Employee.class));
    }

    /**
     * Test updating a non-existing employee.
     * Expects EntityNotFoundException.
     */
    @Test
    void testUpdateEmployee_NotFound() {
        // Arrange
        Long employeeId = 1L;
        when(employeeRepository.findById(employeeId)).thenReturn(Optional.empty());

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            employeeService.updateEmployee(employeeId, employeeDto);
        });

        assertEquals("Employee not found with ID: 1", exception.getMessage());
        verify(employeeRepository, times(1)).findById(employeeId);
        verify(skillRepository, never()).findAllById(anySet());
        verify(projectRepository, never()).findById(anyLong());
        verify(employeeRepository, never()).save(any(Employee.class));
    }

    /**
     * Test deleting an existing employee.
     */
    @Test
    void testDeleteEmployee_Success() {
        // Arrange
        Long employeeId = 1L;
        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(employee));

        // Act
        employeeService.deleteEmployee(employeeId);

        // Assert
        verify(employeeRepository, times(1)).findById(employeeId);
        verify(employeeRepository, times(1)).delete(employee);
    }

    /**
     * Test deleting an employee with invalid ID (zero).
     * Expects IllegalArgumentException.
     */
    @Test
    void testDeleteEmployee_InvalidId() {
        // Arrange
        Long invalidId = 0L;

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            employeeService.deleteEmployee(invalidId);
        });

        assertEquals("Invalid employee ID.", exception.getMessage());
        verify(employeeRepository, never()).findById(anyLong());
        verify(employeeRepository, never()).delete(any(Employee.class));
    }

    /**
     * Test deleting a non-existing employee.
     * Expects ResourceNotFoundException.
     */
    @Test
    void testDeleteEmployee_NotFound() {
        // Arrange
        Long employeeId = 1L;
        when(employeeRepository.findById(employeeId)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            employeeService.deleteEmployee(employeeId);
        });

        assertEquals("Employee not found with ID: 1", exception.getMessage());
        verify(employeeRepository, times(1)).findById(employeeId);
        verify(employeeRepository, never()).delete(any(Employee.class));
    }
}
