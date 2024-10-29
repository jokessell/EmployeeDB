package com.example.EmployeeDB.service;

import com.example.EmployeeDB.dto.ProjectDto;
import com.example.EmployeeDB.entity.Employee;
import com.example.EmployeeDB.entity.Project;
import com.example.EmployeeDB.entity.Skill;
import com.example.EmployeeDB.exception.ResourceNotFoundException;
import com.example.EmployeeDB.mapper.ProjectMapper;
import com.example.EmployeeDB.repository.EmployeeRepository;
import com.example.EmployeeDB.repository.ProjectRepository;
import com.example.EmployeeDB.repository.SkillRepository;
import com.example.EmployeeDB.service.ProjectService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;

import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.*;

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
        employee1 = new Employee();
        employee1.setEmployeeId(1L);
        employee1.setName("Alice Smith");

        employee2 = new Employee();
        employee2.setEmployeeId(2L);
        employee2.setName("Bob Johnson");

        skill1 = new Skill();
        skill1.setSkillId(1L);
        skill1.setName("Java");

        skill2 = new Skill();
        skill2.setSkillId(2L);
        skill2.setName("Spring");

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

        projectDto1 = new ProjectDto();
        projectDto1.setProjectId(1L);
        projectDto1.setProjectName("Project Alpha");
        projectDto1.setDescription("Alpha Description");
        projectDto1.setEmployeeIds(new HashSet<>(Arrays.asList(1L)));
        projectDto1.setSkillIds(new HashSet<>(Arrays.asList(1L)));

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
        Pageable pageable = PageRequest.of(0, 10);
        List<Project> projects = Arrays.asList(project1, project2);
        Page<Project> projectPage = new PageImpl<>(projects, pageable, projects.size());

        when(projectRepository.findAll(pageable)).thenReturn(projectPage);
        when(projectMapper.toDto(project1)).thenReturn(projectDto1);
        when(projectMapper.toDto(project2)).thenReturn(projectDto2);

        Page<ProjectDto> result = projectService.getAllProjects(pageable);

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
        Pageable pageable = PageRequest.of(0, 10);
        List<Project> projects = Collections.emptyList();
        Page<Project> projectPage = new PageImpl<>(projects, pageable, 0);

        when(projectRepository.findAll(pageable)).thenReturn(projectPage);

        Page<ProjectDto> result = projectService.getAllProjects(pageable);

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
        Long projectId = 1L;
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project1));
        when(projectMapper.toDto(project1)).thenReturn(projectDto1);

        ProjectDto result = projectService.getProjectById(projectId);

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
        Long projectId = 99L;
        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());

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

        ProjectDto result = projectService.createProject(newProjectDto);

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

        ProjectDto result = projectService.updateProject(projectId, updateDto);

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
        Long projectId = 99L;
        ProjectDto updateDto = new ProjectDto();
        updateDto.setProjectName("Non-existent Project");
        updateDto.setDescription("No Description");

        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());

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
        Long projectId = 1L;
        ProjectDto partialUpdateDto = new ProjectDto();
        partialUpdateDto.setProjectName("Updated Project Alpha");
        partialUpdateDto.setDescription("Updated Alpha Description");
        partialUpdateDto.setEmployeeIds(new HashSet<>(Arrays.asList(1L, 2L)));
        partialUpdateDto.setSkillIds(new HashSet<>(Arrays.asList(1L, 2L)));

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project1));
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee1));
        when(employeeRepository.findById(2L)).thenReturn(Optional.of(employee2));
        when(skillRepository.findById(1L)).thenReturn(Optional.of(skill1));
        when(skillRepository.findById(2L)).thenReturn(Optional.of(skill2));
        when(projectRepository.save(any(Project.class))).thenReturn(project1);
        when(projectMapper.toDto(project1)).thenReturn(projectDto1);

        ProjectDto result = projectService.partialUpdateProject(projectId, partialUpdateDto);

        assertNotNull(result, "Result should not be null.");
        assertEquals(projectDto1, result, "Updated ProjectDto should match expected.");

        verify(projectRepository, times(1)).findById(projectId);
        verify(employeeRepository, times(1)).findById(1L);
        verify(employeeRepository, times(1)).findById(2L);
        verify(skillRepository, times(1)).findById(1L);
        verify(skillRepository, times(1)).findById(2L);
        verify(projectRepository, times(1)).save(projectCaptor.capture());
        verify(projectMapper, times(1)).toDto(project1);

        Project capturedProject = projectCaptor.getValue();
        assertEquals("Updated Project Alpha", capturedProject.getProjectName(), "Project name should be updated.");
        assertEquals("Updated Alpha Description", capturedProject.getDescription(), "Project description should be updated.");
        assertEquals(new HashSet<>(Arrays.asList(employee1, employee2)), capturedProject.getEmployees(), "Project employees should be updated.");
        assertEquals(new HashSet<>(Arrays.asList(skill1, skill2)), capturedProject.getSkills(), "Project skills should be updated.");
    }

    /**
     * Test partially updating a non-existent project.
     * Expects ResourceNotFoundException.
     */
    @Test
    void testPartialUpdateProject_NotFound() {
        Long projectId = 99L;
        ProjectDto partialUpdateDto = new ProjectDto();
        partialUpdateDto.setProjectName("Non-existent Project");

        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());

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
        Long projectId = 1L;
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project1));
        doNothing().when(projectRepository).delete(project1);

        projectService.deleteProject(projectId);

        verify(projectRepository, times(1)).findById(projectId);
        verify(projectRepository, times(1)).delete(project1);
    }

    /**
     * Test deleting a non-existent project.
     * Expects ResourceNotFoundException.
     */
    @Test
    void testDeleteProject_NotFound() {
        Long projectId = 99L;
        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());

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
        Long employeeId = 1L;
        when(projectRepository.findByEmployeesEmployeeId(employeeId)).thenReturn(Arrays.asList(project1, project2));
        when(projectMapper.toDto(project1)).thenReturn(projectDto1);
        when(projectMapper.toDto(project2)).thenReturn(projectDto2);

        List<ProjectDto> result = projectService.getProjectsByEmployeeId(employeeId);

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
        Long employeeId = 99L;
        when(projectRepository.findByEmployeesEmployeeId(employeeId)).thenReturn(Collections.emptyList());

        List<ProjectDto> result = projectService.getProjectsByEmployeeId(employeeId);

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

    @Test
    void testCreateProject_InvalidProjectName() {
        ProjectDto invalidProjectDto = new ProjectDto();
        invalidProjectDto.setProjectName(""); // Invalid name
        invalidProjectDto.setDescription("Invalid Project without name");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            projectService.createProject(invalidProjectDto);
        }, "Expected IllegalArgumentException for null or empty project name.");

        assertEquals("Project name cannot be null or empty", exception.getMessage(), "Exception message should match.");
    }

    @Test
    void testPartialUpdateProject_NoChanges() {
        Long projectId = 1L;
        Project existingProject = project1;
        ProjectDto partialUpdateDto = new ProjectDto(); // No changes in DTO

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(existingProject));
        when(projectRepository.save(existingProject)).thenReturn(existingProject);
        when(projectMapper.toDto(existingProject)).thenReturn(projectDto1);

        ProjectDto result = projectService.partialUpdateProject(projectId, partialUpdateDto);

        assertNotNull(result, "Result should not be null.");
        assertEquals(projectDto1, result, "ProjectDto should match expected.");

        verify(projectRepository, times(1)).findById(projectId);
        verify(projectRepository, times(1)).save(existingProject);
        verify(projectMapper, times(1)).toDto(existingProject);
    }

    @Test
    void testUpdateProject_ClearEmployeesAndSkills() {
        Long projectId = 1L;
        Project existingProject = project1;
        ProjectDto updateDto = new ProjectDto();
        updateDto.setProjectName("Project Alpha Cleared");
        updateDto.setDescription("Cleared Employees and Skills");
        updateDto.setEmployeeIds(new HashSet<>()); // Empty set to clear employees
        updateDto.setSkillIds(new HashSet<>()); // Empty set to clear skills

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(existingProject));
        when(projectRepository.save(existingProject)).thenReturn(existingProject);
        when(projectMapper.toDto(existingProject)).thenReturn(projectDto1);

        ProjectDto result = projectService.updateProject(projectId, updateDto);

        assertNotNull(result, "Result should not be null.");
        assertEquals(projectDto1, result, "Updated ProjectDto should match expected.");
        assertTrue(existingProject.getEmployees().isEmpty(), "Employees should be cleared.");
        assertTrue(existingProject.getSkills().isEmpty(), "Skills should be cleared.");

        verify(projectRepository, times(1)).findById(projectId);
        verify(projectRepository, times(1)).save(existingProject);
        verify(projectMapper, times(1)).toDto(existingProject);
    }
}
