// src/main/java/com/example/service/ProjectService.java
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
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final EmployeeRepository employeeRepository;
    private final SkillRepository skillRepository;
    private final ProjectMapper projectMapper;

    public Page<ProjectDto> getAllProjects(Pageable pageable) {
        Page<Project> projectsPage = projectRepository.findAll(pageable);
        return projectsPage.map(projectMapper::toDto);
    }

    // Retrieve a project by ID
    public ProjectDto getProjectById(Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with ID: " + projectId));
        return projectMapper.toDto(project);
    }

    // Create a new project
    @Transactional
    public ProjectDto createProject(ProjectDto projectDto) {
        Project project = projectMapper.toEntity(projectDto);
        processProjectData(project, projectDto);
        Project savedProject = projectRepository.save(project);
        return projectMapper.toDto(savedProject);
    }

    // Update an existing project
    @Transactional
    public ProjectDto updateProject(Long projectId, ProjectDto projectDto) {
        Project existingProject = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with ID: " + projectId));

        existingProject.setProjectName(projectDto.getProjectName());
        existingProject.setDescription(projectDto.getDescription());

        processProjectData(existingProject, projectDto);

        Project updatedProject = projectRepository.save(existingProject);
        return projectMapper.toDto(updatedProject);
    }

    // Delete a project
    public void deleteProject(Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with ID: " + projectId));
        projectRepository.delete(project);
    }

    // Retrieve all projects for a specific employee
    public List<ProjectDto> getProjectsByEmployeeId(Long employeeId) {
        List<Project> projects = projectRepository.findByEmployeesEmployeeId(employeeId);
        return projects.stream()
                .map(projectMapper::toDto)
                .collect(Collectors.toList());
    }

    // Process project to set employees and skills
    private void processProjectData(Project project, ProjectDto projectDto) {
        if (projectDto.getEmployeeIds() != null && !projectDto.getEmployeeIds().isEmpty()) {
            Set<Employee> employees = projectDto.getEmployeeIds().stream()
                    .map(employeeRepository::findById)
                    .filter(java.util.Optional::isPresent)
                    .map(java.util.Optional::get)
                    .collect(Collectors.toSet());
            project.setEmployees(employees);
        }

        if (projectDto.getSkillIds() != null && !projectDto.getSkillIds().isEmpty()) {
            Set<Skill> skills = projectDto.getSkillIds().stream()
                    .map(skillRepository::findById)
                    .filter(java.util.Optional::isPresent)
                    .map(java.util.Optional::get)
                    .collect(Collectors.toSet());
            project.setSkills(skills);
        }
    }
}
