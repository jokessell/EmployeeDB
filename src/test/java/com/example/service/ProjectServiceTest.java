// src/test/java/com/example/service/ProjectServiceTest.java
package com.example.service;

import com.example.dto.ProjectDto;
import com.example.entity.Employee;
import com.example.entity.Project;
import com.example.entity.Skill;
import com.example.exception.ResourceNotFoundException;
import com.example.mapper.ProjectMapper;
import com.example.repository.EmployeeRepository;
import com.example.repository.ProjectRepository;
import com.example.repository.SkillRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;

import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private SkillRepository skillRepository;

    @Mock
    private ProjectMapper projectMapper;

    @InjectMocks
    private ProjectService projectService;

    @Captor
    ArgumentCaptor<Project> projectCaptor;

    @Captor
    ArgumentCaptor<Employee> employeeCaptor;

    @Captor
    ArgumentCaptor<Skill> skillCaptor;

    private Project project1;
    private Project project2;
    private Employee employee1;
    private Employee employee2;
    private Skill skill1;
    private Skill skill2;
    private ProjectDto projectDto1;
    private ProjectDto projectDto2;

    @BeforeEach
    void setUp() {
        // Initialize Employees
        employee1 = new Employee();
        employee1.setEmployeeId(1L);
        employee1.setName("Alice Smith");
        // Initialize other fields as necessary

        employee2 = new Employee();
        employee2.setEmployeeId(2L);
        employee2.setName("Bob Johnson");
        // Initialize other fields as necessary

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
        project1.setDescription("Alpha Description");
        project1.setEmployees(new HashSet<>(Arrays.asList(employee1)));
        project1.setSkills(new HashSet<>(Arrays.asList(skill1)));

        project2 = new Project();
        project2.setProjectId(2L);
        project2.setProjectName("Project Beta");
        project2.setDescription("Beta Description");
        project2.setEmployees(new HashSet<>(Arrays.asList(employee2)));
        project2.setSkills(new HashSet<>(Arrays.asList(skill2)));

        // Initialize ProjectDtos
        projectDto1 = new ProjectDto();
        projectDto1.setProjectId(1L);
        projectDto1.setProjectName("Project Alpha");
        projectDto1.setDescription("Alpha Description");
        projectDto1.setEmployeeIds(new HashSet<>(Arrays.asList(1L)));
        projectDto1.setSkillIds(new HashSet<>(Arrays.asList(1L)));
        // Assuming employees and skills are populated in GET responses

        projectDto2 = new ProjectDto();
        projectDto2.setProjectId(2L);
        projectDto2.setProjectName("Project Beta");
        projectDto2.setDescription("Beta Description");
        projectDto2.setEmployeeIds(new HashSet<>(Arrays.asList(2L)));
        projectDto2.setSkillIds(new HashSet<>(Arrays.asList(2L)));
    }

    /**
     * Test retrieving all projects successfully.
     */
    @Test
    void testGetAllProjects_Success() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        List<Project> projects = Arrays.asList(project1, project2);
        Page<Project> projectPage = new PageImpl<>(projects, pageable, projects.size());

        when(projectRepository.findAll(pageable)).thenReturn(projectPage);
        when(projectMapper.toDto(project1)).thenReturn(projectDto1);
        when(projectMapper.toDto(project2)).thenReturn(projectDto2);

        // Act
        Page<ProjectDto> result = projectService.getAllProjects(pageable);

        // Assert
        assertNotNull(result, "Result should not be null.");
        assertEquals(2, result.getTotalElements(), "Total elements should be 2.");
        List<ProjectDto> projectDtos = result.getContent();
        assertEquals(2, projectDtos.size(), "Number of project DTOs should be 2.");
        assertTrue(projectDtos.contains(projectDto1), "Result should contain projectDto1.");
        assertTrue(projectDtos.contains(projectDto2), "Result should contain projectDto2.");

        verify(projectRepository, times(1)).findAll(pageable);
        verify(projectMapper, times(1)).toDto(project1);
        verify(projectMapper, times(1)).toDto(project2);
    }

    /**
     * Test retrieving all projects when none exist.
     * Expects an empty page.
     */
    @Test
    void testGetAllProjects_NoProjects() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        List<Project> projects = Collections.emptyList();
        Page<Project> projectPage = new PageImpl<>(projects, pageable, 0);

        when(projectRepository.findAll(pageable)).thenReturn(projectPage);

        // Act
        Page<ProjectDto> result = projectService.getAllProjects(pageable);

        // Assert
        assertNotNull(result, "Result should not be null.");
        assertEquals(0, result.getTotalElements(), "Total elements should be 0.");
        assertTrue(result.getContent().isEmpty(), "Project DTO list should be empty.");

        verify(projectRepository, times(1)).findAll(pageable);
        verify(projectMapper, never()).toDto(any(Project.class));
    }

    /**
     * Test retrieving a project by valid ID.
     */
    @Test
    void testGetProjectById_Success() {
        // Arrange
        Long projectId = 1L;
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project1));
        when(projectMapper.toDto(project1)).thenReturn(projectDto1);

        // Act
        ProjectDto result = projectService.getProjectById(projectId);

        // Assert
        assertNotNull(result, "Result should not be null.");
        assertEquals(projectDto1, result, "Returned ProjectDto should match expected.");

        verify(projectRepository, times(1)).findById(projectId);
        verify(projectMapper, times(1)).toDto(project1);
    }

    /**
     * Test retrieving a project by non-existent ID.
     * Expects ResourceNotFoundException.
     */
    @Test
    void testGetProjectById_NotFound() {
        // Arrange
        Long projectId = 99L;
        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            projectService.getProjectById(projectId);
        }, "Expected ResourceNotFoundException for non-existent project ID.");

        assertEquals("Project not found with ID: 99", exception.getMessage(), "Exception message should match.");
        verify(projectRepository, times(1)).findById(projectId);
        verify(projectMapper, never()).toDto(any(Project.class));
    }

    /**
     * Test creating a project with valid data.
     */
    @Test
    void testCreateProject_Success() {
        // Arrange
        Project newProject = new Project();
        newProject.setProjectName("Project Gamma");
        newProject.setDescription("Gamma Description");
        newProject.setEmployees(new HashSet<>(Arrays.asList(employee1)));
        newProject.setSkills(new HashSet<>(Arrays.asList(skill1)));

        ProjectDto newProjectDto = new ProjectDto();
        newProjectDto.setProjectName("Project Gamma");
        newProjectDto.setDescription("Gamma Description");
        newProjectDto.setEmployeeIds(new HashSet<>(Arrays.asList(1L)));
        newProjectDto.setSkillIds(new HashSet<>(Arrays.asList(1L)));

        Project savedProject = new Project();
        savedProject.setProjectId(3L);
        savedProject.setProjectName("Project Gamma");
        savedProject.setDescription("Gamma Description");
        savedProject.setEmployees(new HashSet<>(Arrays.asList(employee1)));
        savedProject.setSkills(new HashSet<>(Arrays.asList(skill1)));

        ProjectDto savedProjectDto = new ProjectDto();
        savedProjectDto.setProjectId(3L);
        savedProjectDto.setProjectName("Project Gamma");
        savedProjectDto.setDescription("Gamma Description");
        savedProjectDto.setEmployeeIds(new HashSet<>(Arrays.asList(1L)));
        savedProjectDto.setSkillIds(new HashSet<>(Arrays.asList(1L)));

        when(projectMapper.toEntity(newProjectDto)).thenReturn(newProject);
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee1));
        when(skillRepository.findById(1L)).thenReturn(Optional.of(skill1));
        when(projectRepository.save(newProject)).thenReturn(savedProject);
        when(projectMapper.toDto(savedProject)).thenReturn(savedProjectDto);

        // Act
        ProjectDto result = projectService.createProject(newProjectDto);

        // Assert
        assertNotNull(result, "Result should not be null.");
        assertEquals(savedProjectDto, result, "Saved ProjectDto should match expected.");

        verify(projectMapper, times(1)).toEntity(newProjectDto);
        verify(employeeRepository, times(1)).findById(1L);
        verify(skillRepository, times(1)).findById(1L);
        verify(projectRepository, times(1)).save(newProject);
        verify(projectMapper, times(1)).toDto(savedProject);
    }

    /**
     * Test updating an existing project with valid data.
     */
    @Test
    void testUpdateProject_Success() {
        // Arrange
        Long projectId = 1L;
        Project existingProject = project1;
        ProjectDto updateDto = new ProjectDto();
        updateDto.setProjectName("Project Alpha Updated");
        updateDto.setDescription("Updated Description");
        updateDto.setEmployeeIds(new HashSet<>(Arrays.asList(1L, 2L)));
        updateDto.setSkillIds(new HashSet<>(Arrays.asList(1L, 2L)));

        Project updatedProject = new Project();
        updatedProject.setProjectId(projectId);
        updatedProject.setProjectName("Project Alpha Updated");
        updatedProject.setDescription("Updated Description");
        updatedProject.setEmployees(new HashSet<>(Arrays.asList(employee1, employee2)));
        updatedProject.setSkills(new HashSet<>(Arrays.asList(skill1, skill2)));

        ProjectDto updatedProjectDto = new ProjectDto();
        updatedProjectDto.setProjectId(projectId);
        updatedProjectDto.setProjectName("Project Alpha Updated");
        updatedProjectDto.setDescription("Updated Description");
        updatedProjectDto.setEmployeeIds(new HashSet<>(Arrays.asList(1L, 2L)));
        updatedProjectDto.setSkillIds(new HashSet<>(Arrays.asList(1L, 2L)));

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(existingProject));
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee1));
        when(employeeRepository.findById(2L)).thenReturn(Optional.of(employee2));
        when(skillRepository.findById(1L)).thenReturn(Optional.of(skill1));
        when(skillRepository.findById(2L)).thenReturn(Optional.of(skill2));
        when(projectRepository.save(existingProject)).thenReturn(updatedProject);
        when(projectMapper.toDto(updatedProject)).thenReturn(updatedProjectDto);

        // Act
        ProjectDto result = projectService.updateProject(projectId, updateDto);

        // Assert
        assertNotNull(result, "Result should not be null.");
        assertEquals(updatedProjectDto, result, "Updated ProjectDto should match expected.");

        verify(projectRepository, times(1)).findById(projectId);
        verify(employeeRepository, times(1)).findById(1L);
        verify(employeeRepository, times(1)).findById(2L);
        verify(skillRepository, times(1)).findById(1L);
        verify(skillRepository, times(1)).findById(2L);
        verify(projectRepository, times(1)).save(existingProject);
        verify(projectMapper, times(1)).toDto(updatedProject);
    }

    /**
     * Test updating a non-existent project.
     * Expects ResourceNotFoundException.
     */
    @Test
    void testUpdateProject_NotFound() {
        // Arrange
        Long projectId = 99L;
        ProjectDto updateDto = new ProjectDto();
        updateDto.setProjectName("Non-existent Project");
        updateDto.setDescription("No Description");

        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            projectService.updateProject(projectId, updateDto);
        }, "Expected ResourceNotFoundException for non-existent project ID.");

        assertEquals("Project not found with ID: 99", exception.getMessage(), "Exception message should match.");
        verify(projectRepository, times(1)).findById(projectId);
        verify(projectRepository, never()).save(any(Project.class));
        verify(projectMapper, never()).toDto(any(Project.class));
    }

    /**
     * Test partially updating an existing project (only project name).
     */
    @Test
    void testPartialUpdateProject_Success() {
        // Arrange
        Long projectId = 1L;
        Project existingProject = project1;
        ProjectDto partialUpdateDto = new ProjectDto();
        partialUpdateDto.setProjectName("Project Alpha Partially Updated");

        Project updatedProject = new Project();
        updatedProject.setProjectId(projectId);
        updatedProject.setProjectName("Project Alpha Partially Updated");
        updatedProject.setDescription(existingProject.getDescription());
        updatedProject.setEmployees(existingProject.getEmployees());
        updatedProject.setSkills(existingProject.getSkills());

        ProjectDto updatedProjectDto = new ProjectDto();
        updatedProjectDto.setProjectId(projectId);
        updatedProjectDto.setProjectName("Project Alpha Partially Updated");
        updatedProjectDto.setDescription(existingProject.getDescription());
        updatedProjectDto.setEmployeeIds(existingProject.getEmployees().stream().map(Employee::getEmployeeId).collect(Collectors.toSet()));
        updatedProjectDto.setSkillIds(existingProject.getSkills().stream().map(Skill::getSkillId).collect(Collectors.toSet()));

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(existingProject));
        when(projectRepository.save(existingProject)).thenReturn(updatedProject);
        when(projectMapper.toDto(updatedProject)).thenReturn(updatedProjectDto);

        // Act
        ProjectDto result = projectService.partialUpdateProject(projectId, partialUpdateDto);

        // Assert
        assertNotNull(result, "Result should not be null.");
        assertEquals(updatedProjectDto, result, "Partially updated ProjectDto should match expected.");

        verify(projectRepository, times(1)).findById(projectId);
        verify(projectRepository, times(1)).save(existingProject);
        verify(projectMapper, times(1)).toDto(updatedProject);
    }

    /**
     * Test partially updating a non-existent project.
     * Expects ResourceNotFoundException.
     */
    @Test
    void testPartialUpdateProject_NotFound() {
        // Arrange
        Long projectId = 99L;
        ProjectDto partialUpdateDto = new ProjectDto();
        partialUpdateDto.setProjectName("Non-existent Project");

        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            projectService.partialUpdateProject(projectId, partialUpdateDto);
        }, "Expected ResourceNotFoundException for non-existent project ID.");

        assertEquals("Project not found with ID: 99", exception.getMessage(), "Exception message should match.");
        verify(projectRepository, times(1)).findById(projectId);
        verify(projectRepository, never()).save(any(Project.class));
        verify(projectMapper, never()).toDto(any(Project.class));
    }

    /**
     * Test deleting an existing project.
     */
    @Test
    void testDeleteProject_Success() {
        // Arrange
        Long projectId = 1L;
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project1));
        doNothing().when(projectRepository).delete(project1);

        // Act
        projectService.deleteProject(projectId);

        // Assert
        verify(projectRepository, times(1)).findById(projectId);
        verify(projectRepository, times(1)).delete(project1);
    }

    /**
     * Test deleting a non-existent project.
     * Expects ResourceNotFoundException.
     */
    @Test
    void testDeleteProject_NotFound() {
        // Arrange
        Long projectId = 99L;
        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            projectService.deleteProject(projectId);
        }, "Expected ResourceNotFoundException for non-existent project ID.");

        assertEquals("Project not found with ID: 99", exception.getMessage(), "Exception message should match.");
        verify(projectRepository, times(1)).findById(projectId);
        verify(projectRepository, never()).delete(any(Project.class));
    }

    /**
     * Test retrieving projects by employee ID successfully.
     */
    @Test
    void testGetProjectsByEmployeeId_Success() {
        // Arrange
        Long employeeId = 1L;
        when(projectRepository.findByEmployeesEmployeeId(employeeId)).thenReturn(Arrays.asList(project1, project2));
        when(projectMapper.toDto(project1)).thenReturn(projectDto1);
        when(projectMapper.toDto(project2)).thenReturn(projectDto2);

        // Act
        List<ProjectDto> result = projectService.getProjectsByEmployeeId(employeeId);

        // Assert
        assertNotNull(result, "Result should not be null.");
        assertEquals(2, result.size(), "Number of projects should be 2.");
        assertTrue(result.contains(projectDto1), "Result should contain projectDto1.");
        assertTrue(result.contains(projectDto2), "Result should contain projectDto2.");

        verify(projectRepository, times(1)).findByEmployeesEmployeeId(employeeId);
        verify(projectMapper, times(1)).toDto(project1);
        verify(projectMapper, times(1)).toDto(project2);
    }

    /**
     * Test retrieving projects by non-existent employee ID.
     * Expects an empty list or specific exception based on implementation.
     */
    @Test
    void testGetProjectsByEmployeeId_NoProjects() {
        // Arrange
        Long employeeId = 99L;
        when(projectRepository.findByEmployeesEmployeeId(employeeId)).thenReturn(Collections.emptyList());

        // Act
        List<ProjectDto> result = projectService.getProjectsByEmployeeId(employeeId);

        // Assert
        assertNotNull(result, "Result should not be null.");
        assertTrue(result.isEmpty(), "Result should be empty as no projects are associated.");

        verify(projectRepository, times(1)).findByEmployeesEmployeeId(employeeId);
        verify(projectMapper, never()).toDto(any(Project.class));
    }

    /**
     * Test creating a project with non-existent employee ID.
     * Expects ResourceNotFoundException.
     */
    @Test
    void testCreateProject_NonExistentEmployee() {
        // Arrange
        ProjectDto newProjectDto = new ProjectDto();
        newProjectDto.setProjectName("Project Delta");
        newProjectDto.setDescription("Delta Description");
        newProjectDto.setEmployeeIds(new HashSet<>(Arrays.asList(99L))); // Non-existent employee ID
        newProjectDto.setSkillIds(new HashSet<>(Arrays.asList(1L)));

        Project newProject = new Project();
        newProject.setProjectName("Project Delta");
        newProject.setDescription("Delta Description");
        newProject.setEmployees(new HashSet<>());
        newProject.setSkills(new HashSet<>());

        when(projectMapper.toEntity(newProjectDto)).thenReturn(newProject);
        when(employeeRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            projectService.createProject(newProjectDto);
        }, "Expected ResourceNotFoundException for non-existent employee ID.");

        assertEquals("Employee not found with ID: 99", exception.getMessage(), "Exception message should match.");
        verify(projectMapper, times(1)).toEntity(newProjectDto);
        verify(employeeRepository, times(1)).findById(99L);
        verify(skillRepository, never()).findById(anyLong());
        verify(projectRepository, never()).save(any(Project.class));
        verify(projectMapper, never()).toDto(any(Project.class));
    }

    /**
     * Test creating a project with non-existent skill ID.
     * Expects ResourceNotFoundException.
     */
    @Test
    void testCreateProject_NonExistentSkill() {
        // Arrange
        ProjectDto newProjectDto = new ProjectDto();
        newProjectDto.setProjectName("Project Epsilon");
        newProjectDto.setDescription("Epsilon Description");
        newProjectDto.setEmployeeIds(new HashSet<>(Arrays.asList(1L)));
        newProjectDto.setSkillIds(new HashSet<>(Arrays.asList(99L))); // Non-existent skill ID

        Project newProject = new Project();
        newProject.setProjectName("Project Epsilon");
        newProject.setDescription("Epsilon Description");
        newProject.setEmployees(new HashSet<>(Arrays.asList(employee1)));
        newProject.setSkills(new HashSet<>());

        when(projectMapper.toEntity(newProjectDto)).thenReturn(newProject);
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee1));
        when(skillRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            projectService.createProject(newProjectDto);
        }, "Expected ResourceNotFoundException for non-existent skill ID.");

        assertEquals("Skill not found with ID: 99", exception.getMessage(), "Exception message should match.");
        verify(projectMapper, times(1)).toEntity(newProjectDto);
        verify(employeeRepository, times(1)).findById(1L);
        verify(skillRepository, times(1)).findById(99L);
        verify(projectRepository, never()).save(any(Project.class));
        verify(projectMapper, never()).toDto(any(Project.class));
    }

    // ... (Additional test cases as needed)
}
