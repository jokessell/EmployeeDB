// src/main/java/com/example/service/ProjectService.java
package com.example.service;

import com.example.dto.ProjectDto;
import com.example.entity.Employee;
import com.example.entity.Project;
import com.example.exception.ResourceNotFoundException;
import com.example.mapper.ProjectMapper;
import com.example.repository.EmployeeRepository;
import com.example.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;
    private final EmployeeRepository employeeRepository; // Injected directly

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
    public ProjectDto createProject(ProjectDto projectDto) {
        Project project = projectMapper.toEntity(projectDto);
        // Associate with Employee
        if (projectDto.getEmployeeId() != null) {
            Employee employee = employeeRepository.findById(projectDto.getEmployeeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Employee not found with ID: " + projectDto.getEmployeeId()));
            project.setEmployee(employee);
        } else {
            throw new ResourceNotFoundException("Employee ID is required to associate the project.");
        }
        Project savedProject = projectRepository.save(project);
        return projectMapper.toDto(savedProject);
    }

    // Update an existing project
    public ProjectDto updateProject(Long projectId, ProjectDto projectDto) {
        Project existingProject = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with ID: " + projectId));

        existingProject.setProjectName(projectDto.getProjectName());
        existingProject.setDescription(projectDto.getDescription());

        if (projectDto.getEmployeeId() != null) {
            if (existingProject.getEmployee() != null && existingProject.getEmployee().getEmployeeId().equals(projectDto.getEmployeeId())) {
                // Same employee, no action needed
            } else {
                // Fetch the new employee directly
                Employee newEmployee = employeeRepository.findById(projectDto.getEmployeeId())
                        .orElseThrow(() -> new ResourceNotFoundException("Employee not found with ID: " + projectDto.getEmployeeId()));
                existingProject.setEmployee(newEmployee);
            }
        } else {
            // Optionally handle removing the association
            existingProject.setEmployee(null);
        }

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
        List<Project> projects = projectRepository.findByEmployeeEmployeeId(employeeId);
        return projects.stream()
                .map(projectMapper::toDto)
                .collect(Collectors.toList());
    }
}
