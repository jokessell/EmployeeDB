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
import com.example.EmployeeDB.service.EmployeeService;
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
        employeeDto = new EmployeeDto();
        employeeDto.setEmployeeId(1L); // Set the employeeId here
        employeeDto.setName("John Doe");
        employeeDto.setDateOfBirth(LocalDate.of(1990, 1, 1));
        employeeDto.setJobRole("Developer");
        employeeDto.setGender("Male");
        employeeDto.setAvatarUrl("http://example.com/avatar.jpg");
        employeeDto.setSkillIds(new HashSet<>(Arrays.asList(1L, 2L)));
        employeeDto.setProjectIds(new HashSet<>(Arrays.asList(1L, 2L)));

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

        skill1 = new Skill();
        skill1.setSkillId(1L);
        skill1.setName("Java");

        skill2 = new Skill();
        skill2.setSkillId(2L);
        skill2.setName("Spring");

        project1 = new Project();
        project1.setProjectId(1L);
        project1.setProjectName("Project Alpha");

        project2 = new Project();
        project2.setProjectId(2L);
        project2.setProjectName("Project Beta");
    }

    @Test
    void testProcessEmployeeData_shouldSetCorrectProjectsSkillsAgeAndEmail() {
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project1));
        when(projectRepository.findById(2L)).thenReturn(Optional.of(project2));
        when(skillRepository.findById(1L)).thenReturn(Optional.of(skill1));
        when(skillRepository.findById(2L)).thenReturn(Optional.of(skill2));

        employeeService.processEmployeeData(employee, employeeDto);

        assertEquals(2, employee.getProjects().size(), "Projects size mismatch.");
        assertTrue(employee.getProjects().containsAll(Arrays.asList(project1, project2)), "Projects not set correctly.");
        assertEquals(2, employee.getSkills().size(), "Skills size mismatch.");
        assertTrue(employee.getSkills().containsAll(Arrays.asList(skill1, skill2)), "Skills not set correctly.");
        assertEquals(Period.between(employeeDto.getDateOfBirth(), LocalDate.now()).getYears(), employee.getAge(), "Age not calculated correctly.");
        assertEquals("john.doe@email.com", employee.getEmail(), "Email not generated correctly.");
    }

    @Test
    void testCalculateAge_shouldReturnCorrectAge_whenDateOfBirthIsValid() {
        LocalDate dateOfBirth = LocalDate.of(1990, 1, 1);
        int expectedAge = Period.between(dateOfBirth, LocalDate.now()).getYears();

        int age = employeeService.calculateAge(dateOfBirth);

        assertEquals(expectedAge, age, "Age calculation is incorrect.");
    }

    @Test
    void testCalculateAge_shouldReturnZero_whenDateOfBirthIsNull() {
        int age = employeeService.calculateAge(null);

        assertEquals(0, age, "Age should be zero when date of birth is null.");
    }

    @Test
    void testGenerateEmail_shouldGenerateEmail_whenNameIsValid() {
        String name = "John Doe";

        String email = employeeService.generateEmail(name);

        assertEquals("john.doe@email.com", email, "Email generation is incorrect.");
    }

    @Test
    void testGenerateEmail_shouldReturnEmpty_whenNameIsNull() {
        String email = employeeService.generateEmail(null);

        assertEquals("", email, "Email should be empty when name is null.");
    }

    @Test
    void testGenerateEmail_shouldReturnEmailWithSingleName_whenNameHasOnlyOnePart() {
        String name = "John";

        String email = employeeService.generateEmail(name);

        assertEquals("john@email.com", email, "Email generation for single name part is incorrect.");
    }

    @Test
    void testCreateEmployee_shouldReturnEmployeeDto_whenInputIsValid() {
        when(employeeMapper.toEntity(employeeDto)).thenReturn(employee);
        when(skillRepository.findAllById(employeeDto.getSkillIds())).thenReturn(Arrays.asList(skill1, skill2));
        when(projectRepository.findAllById(employeeDto.getProjectIds())).thenReturn(Arrays.asList(project1, project2));
        when(employeeRepository.save(any(Employee.class))).thenReturn(employee);
        when(employeeMapper.toDto(employee)).thenReturn(employeeDto);

        EmployeeDto result = employeeService.createEmployee(employeeDto);

        assertNotNull(result, "The created employee DTO should not be null.");
        assertEquals(employeeDto.getName(), result.getName(), "Employee name mismatch.");
        assertEquals(employeeDto.getJobRole(), result.getJobRole(), "Job role mismatch.");
        assertEquals(employeeDto.getGender(), result.getGender(), "Gender mismatch.");
    }

    @Test
    void testUpdateEmployee_shouldReturnUpdatedEmployeeDto_whenInputIsValid() {
        Long employeeId = 1L;
        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(employee));
        when(skillRepository.findAllById(employeeDto.getSkillIds())).thenReturn(Arrays.asList(skill1, skill2));
        when(projectRepository.findAllById(employeeDto.getProjectIds())).thenReturn(Arrays.asList(project1, project2));
        when(employeeRepository.save(any(Employee.class))).thenReturn(employee);
        when(employeeMapper.toDto(employee)).thenReturn(employeeDto);

        EmployeeDto result = employeeService.updateEmployee(employeeId, employeeDto);

        assertNotNull(result, "The updated employee DTO should not be null.");
        assertEquals(employeeDto.getName(), result.getName(), "Employee name mismatch.");
        assertEquals(employeeDto.getJobRole(), result.getJobRole(), "Job role mismatch.");
        assertEquals(employeeDto.getGender(), result.getGender(), "Gender mismatch.");
    }

    @Test
    void testGetAllEmployees_Success() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Employee> employees = Arrays.asList(employee);
        Page<Employee> employeePage = new PageImpl<>(employees, pageable, employees.size());

        when(employeeRepository.findAll(pageable)).thenReturn(employeePage);
        when(employeeMapper.toDto(employee)).thenReturn(employeeDto);

        Page<EmployeeDto> result = employeeService.getAllEmployees(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(employeeDto.getName(), result.getContent().get(0).getName());
        verify(employeeRepository, times(1)).findAll(pageable);
    }

    @Test
    void testGetAllEmployees_NoEmployees() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Employee> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);

        when(employeeRepository.findAll(pageable)).thenReturn(emptyPage);

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            employeeService.getAllEmployees(pageable);
        });

        assertEquals("No employees found.", exception.getMessage());
        verify(employeeRepository, times(1)).findAll(pageable);
    }

    @Test
    void testGetEmployeeById_Success() {
        Long employeeId = 1L;
        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(employee));
        when(employeeMapper.toDto(employee)).thenReturn(employeeDto);

        EmployeeDto result = employeeService.getEmployeeById(employeeId);

        assertNotNull(result);
        assertEquals(employeeId, result.getEmployeeId());
        assertEquals(employeeDto.getName(), result.getName());
        verify(employeeRepository, times(1)).findById(employeeId);
        verify(employeeMapper, times(1)).toDto(employee);
    }

    @Test
    void testGetEmployeeById_InvalidId() {
        Long invalidId = -1L;

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            employeeService.getEmployeeById(invalidId);
        });

        assertEquals("Invalid employee ID.", exception.getMessage());
        verify(employeeRepository, never()).findById(anyLong());
    }

    @Test
    void testGetEmployeeById_NotFound() {
        Long employeeId = 1L;
        when(employeeRepository.findById(employeeId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            employeeService.getEmployeeById(employeeId);
        });

        assertEquals("Employee not found with ID: 1", exception.getMessage());
        verify(employeeRepository, times(1)).findById(employeeId);
    }

    @Test
    void testCreateEmployee_InvalidInput_NameNull() {
        employeeDto.setName(null);

        InvalidInputException exception = assertThrows(InvalidInputException.class, () -> {
            employeeService.createEmployee(employeeDto);
        }, "Expected InvalidInputException when name is null.");

        assertEquals("Employee name is required.", exception.getMessage(), "Exception message should match.");

        verify(employeeMapper, never()).toEntity(any(EmployeeDto.class));
        verify(skillRepository, never()).findAllById(anySet());
        verify(projectRepository, never()).findAllById(anySet());
        verify(employeeRepository, never()).save(any(Employee.class));
    }


    @Test
    void testDeleteEmployee_Success() {
        Long employeeId = 1L;
        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(employee));

        employeeService.deleteEmployee(employeeId);

        verify(employeeRepository, times(1)).findById(employeeId);
        verify(employeeRepository, times(1)).delete(employee);
    }

    @Test
    void testDeleteEmployee_InvalidId() {
        Long invalidId = 0L;

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            employeeService.deleteEmployee(invalidId);
        });

        assertEquals("Invalid employee ID.", exception.getMessage());
        verify(employeeRepository, never()).findById(anyLong());
        verify(employeeRepository, never()).delete(any(Employee.class));
    }

    @Test
    void testDeleteEmployee_NotFound() {
        Long employeeId = 1L;
        when(employeeRepository.findById(employeeId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            employeeService.deleteEmployee(employeeId);
        });

        assertEquals("Employee not found with ID: 1", exception.getMessage());
        verify(employeeRepository, times(1)).findById(employeeId);
        verify(employeeRepository, never()).delete(any(Employee.class));
    }
}
