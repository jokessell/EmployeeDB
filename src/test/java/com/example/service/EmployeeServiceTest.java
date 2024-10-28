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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.time.LocalDate;
import java.time.Period;
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
        employeeDto.setEmployeeId(1L); // Set the employeeId here
        employeeDto.setName("John Doe");
        employeeDto.setDateOfBirth(LocalDate.of(1990, 1, 1));
        employeeDto.setJobRole("Developer");
        employeeDto.setGender("Male");
        employeeDto.setAvatarUrl("http://example.com/avatar.jpg");
        employeeDto.setSkillIds(new HashSet<>(Arrays.asList(1L, 2L)));
        employeeDto.setProjectIds(new HashSet<>(Arrays.asList(1L, 2L)));

        // Initialize Employee entity
        employee = new Employee();
        employee.setEmployeeId(1L);
        employee.setName("John Doe");
        employee.setDateOfBirth(employeeDto.getDateOfBirth());
        employee.setJobRole("Developer");
        employee.setGender("Male");
        employee.setAvatarUrl(employeeDto.getAvatarUrl());
        employee.setEmail("john.doe@email.com");
        employee.setAge(Period.between(employeeDto.getDateOfBirth(), LocalDate.now()).getYears());
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

    @Test
    void testProcessEmployeeData_shouldSetCorrectProjectsSkillsAgeAndEmail() {
        // Arrange
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project1));
        when(projectRepository.findById(2L)).thenReturn(Optional.of(project2));
        when(skillRepository.findById(1L)).thenReturn(Optional.of(skill1));
        when(skillRepository.findById(2L)).thenReturn(Optional.of(skill2));

        // Act
        employeeService.processEmployeeData(employee, employeeDto);

        // Assert
        assertEquals(2, employee.getProjects().size(), "Projects size mismatch.");
        assertTrue(employee.getProjects().containsAll(Arrays.asList(project1, project2)), "Projects not set correctly.");
        assertEquals(2, employee.getSkills().size(), "Skills size mismatch.");
        assertTrue(employee.getSkills().containsAll(Arrays.asList(skill1, skill2)), "Skills not set correctly.");
        assertEquals(Period.between(employeeDto.getDateOfBirth(), LocalDate.now()).getYears(), employee.getAge(), "Age not calculated correctly.");
        assertEquals("john.doe@email.com", employee.getEmail(), "Email not generated correctly.");
    }

    @Test
    void testCalculateAge_shouldReturnCorrectAge_whenDateOfBirthIsValid() {
        // Arrange
        LocalDate dateOfBirth = LocalDate.of(1990, 1, 1);
        int expectedAge = Period.between(dateOfBirth, LocalDate.now()).getYears();

        // Act
        int age = employeeService.calculateAge(dateOfBirth);

        // Assert
        assertEquals(expectedAge, age, "Age calculation is incorrect.");
    }

    @Test
    void testCalculateAge_shouldReturnZero_whenDateOfBirthIsNull() {
        // Act
        int age = employeeService.calculateAge(null);

        // Assert
        assertEquals(0, age, "Age should be zero when date of birth is null.");
    }

    @Test
    void testGenerateEmail_shouldGenerateEmail_whenNameIsValid() {
        // Arrange
        String name = "John Doe";

        // Act
        String email = employeeService.generateEmail(name);

        // Assert
        assertEquals("john.doe@email.com", email, "Email generation is incorrect.");
    }

    @Test
    void testGenerateEmail_shouldReturnEmpty_whenNameIsNull() {
        // Act
        String email = employeeService.generateEmail(null);

        // Assert
        assertEquals("", email, "Email should be empty when name is null.");
    }

    @Test
    void testGenerateEmail_shouldReturnEmailWithSingleName_whenNameHasOnlyOnePart() {
        // Arrange
        String name = "John";

        // Act
        String email = employeeService.generateEmail(name);

        // Assert
        assertEquals("john@email.com", email, "Email generation for single name part is incorrect.");
    }

    @Test
    void testCreateEmployee_shouldReturnEmployeeDto_whenInputIsValid() {
        // Arrange
        when(employeeMapper.toEntity(employeeDto)).thenReturn(employee);
        when(skillRepository.findAllById(employeeDto.getSkillIds())).thenReturn(Arrays.asList(skill1, skill2));
        when(projectRepository.findAllById(employeeDto.getProjectIds())).thenReturn(Arrays.asList(project1, project2));
        when(employeeRepository.save(any(Employee.class))).thenReturn(employee);
        when(employeeMapper.toDto(employee)).thenReturn(employeeDto);

        // Act
        EmployeeDto result = employeeService.createEmployee(employeeDto);

        // Assert
        assertNotNull(result, "The created employee DTO should not be null.");
        assertEquals(employeeDto.getName(), result.getName(), "Employee name mismatch.");
        assertEquals(employeeDto.getJobRole(), result.getJobRole(), "Job role mismatch.");
        assertEquals(employeeDto.getGender(), result.getGender(), "Gender mismatch.");
        // Additional assertions can be added as needed
    }

    @Test
    void testUpdateEmployee_shouldReturnUpdatedEmployeeDto_whenInputIsValid() {
        // Arrange
        Long employeeId = 1L;
        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(employee));
        when(skillRepository.findAllById(employeeDto.getSkillIds())).thenReturn(Arrays.asList(skill1, skill2));
        when(projectRepository.findAllById(employeeDto.getProjectIds())).thenReturn(Arrays.asList(project1, project2));
        when(employeeRepository.save(any(Employee.class))).thenReturn(employee);
        when(employeeMapper.toDto(employee)).thenReturn(employeeDto);

        // Act
        EmployeeDto result = employeeService.updateEmployee(employeeId, employeeDto);

        // Assert
        assertNotNull(result, "The updated employee DTO should not be null.");
        assertEquals(employeeDto.getName(), result.getName(), "Employee name mismatch.");
        assertEquals(employeeDto.getJobRole(), result.getJobRole(), "Job role mismatch.");
        assertEquals(employeeDto.getGender(), result.getGender(), "Gender mismatch.");
        // Additional assertions can be added as needed
    }

    @Test
    void testGetAllEmployees_Success() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        List<Employee> employees = Arrays.asList(employee);
        Page<Employee> employeePage = new PageImpl<>(employees, pageable, employees.size());

        when(employeeRepository.findAll(pageable)).thenReturn(employeePage);
        when(employeeMapper.toDto(employee)).thenReturn(employeeDto);

        // Act
        Page<EmployeeDto> result = employeeService.getAllEmployees(pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(employeeDto.getName(), result.getContent().get(0).getName());
        verify(employeeRepository, times(1)).findAll(pageable);
    }

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

    @Test
    void testGetEmployeeById_Success() {
        // Arrange
        Long employeeId = 1L;
        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(employee));
        when(employeeMapper.toDto(employee)).thenReturn(employeeDto);

        // Act
        EmployeeDto result = employeeService.getEmployeeById(employeeId);

        // Assert
        assertNotNull(result);
        assertEquals(employeeId, result.getEmployeeId());
        assertEquals(employeeDto.getName(), result.getName());
        verify(employeeRepository, times(1)).findById(employeeId);
        verify(employeeMapper, times(1)).toDto(employee);
    }

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

    // ... other test methods remain mostly unchanged ...

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
