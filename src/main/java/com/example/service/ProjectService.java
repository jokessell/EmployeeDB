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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final EmployeeRepository employeeRepository;
    private final SkillRepository skillRepository;
    private final ProjectMapper projectMapper;

    private static final Logger logger = LoggerFactory.getLogger(ProjectService.class);

    public Page<ProjectDto> getAllProjects(Pageable pageable) {
        logger.debug("Retrieving all projects with pageable: {}", pageable);
        Page<Project> projectsPage = projectRepository.findAll(pageable);
        logger.debug("Retrieved {} projects", projectsPage.getTotalElements());
        return projectsPage.map(projectMapper::toDto);
    }

    public ProjectDto getProjectById(Long projectId) {
        logger.debug("Retrieving project with ID: {}", projectId);
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with ID: " + projectId));
        logger.debug("Retrieved project: {}", project);
        return projectMapper.toDto(project);
    }

    @Transactional
    public ProjectDto createProject(ProjectDto projectDto) {
        if (projectDto.getProjectName() == null || projectDto.getProjectName().isEmpty()) {
            throw new IllegalArgumentException("Project name cannot be null or empty");
        }
        // Proceed with mapping and processing
        Project project = projectMapper.toEntity(projectDto);
        processProjectData(project, projectDto);
        Project savedProject = projectRepository.save(project);
        return projectMapper.toDto(savedProject);
    }

    @Transactional
    public ProjectDto updateProject(Long projectId, ProjectDto projectDto) {
        logger.debug("Updating project ID: {} with DTO: {}", projectId, projectDto);
        Project existingProject = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with ID: " + projectId));

        existingProject.setProjectName(projectDto.getProjectName());
        existingProject.setDescription(projectDto.getDescription());

        processProjectData(existingProject, projectDto);

        Project updatedProject = projectRepository.save(existingProject);
        logger.debug("Updated project: {}", updatedProject);
        return projectMapper.toDto(updatedProject);
    }

    @Transactional
    public ProjectDto partialUpdateProject(Long projectId, ProjectDto projectDto) {
        logger.debug("Partially updating project ID: {} with DTO: {}", projectId, projectDto);
        Project existingProject = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with ID: " + projectId));

        // Update projectName if present
        if (projectDto.getProjectName() != null) {
            logger.debug("Updating projectName from '{}' to '{}'", existingProject.getProjectName(), projectDto.getProjectName());
            existingProject.setProjectName(projectDto.getProjectName());
        }

        // Update description if present
        if (projectDto.getDescription() != null) {
            logger.debug("Updating description from '{}' to '{}'", existingProject.getDescription(), projectDto.getDescription());
            existingProject.setDescription(projectDto.getDescription());
        }

        // Handle Employees
        if (projectDto.getEmployeeIds() != null) {
            if (!projectDto.getEmployeeIds().isEmpty()) {
                Set<Employee> employees = projectDto.getEmployeeIds().stream()
                        .map(id -> {
                            Optional<Employee> employeeOpt = employeeRepository.findById(id);
                            if (employeeOpt.isEmpty()) {
                                logger.warn("Employee ID {} not found. Skipping.", id);
                            }
                            return employeeOpt;
                        })
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .collect(Collectors.toSet());
                existingProject.setEmployees(employees);
                logger.debug("Assigned Employees to Project ID {}: {}", projectId, employees);
            } else {
                existingProject.getEmployees().clear();
                logger.debug("Cleared all Employees for Project ID: {}", projectId);
            }
        }

        // Handle Skills if necessary
        if (projectDto.getSkillIds() != null) {
            Set<Skill> skills = projectDto.getSkillIds().stream()
                    .map(id -> skillRepository.findById(id)
                            .orElseThrow(() -> new ResourceNotFoundException("Skill not found with ID: " + id)))
                    .collect(Collectors.toSet());
            existingProject.setSkills(skills);
        }

        // Save changes
        Project updatedProject = projectRepository.save(existingProject);
        logger.debug("Partially updated project: {}", updatedProject);
        return projectMapper.toDto(updatedProject);
    }

    public void deleteProject(Long projectId) {
        logger.debug("Deleting project with ID: {}", projectId);
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with ID: " + projectId));
        projectRepository.delete(project);
        logger.debug("Deleted project with ID: {}", projectId);
    }

    public List<ProjectDto> getProjectsByEmployeeId(Long employeeId) {
        logger.debug("Fetching projects for employee ID: {}", employeeId);
        List<Project> projects = projectRepository.findByEmployeesEmployeeId(employeeId);
        logger.debug("Fetched {} projects for employee ID: {}", projects.size(), employeeId);
        return projects.stream()
                .map(projectMapper::toDto)
                .collect(Collectors.toList());
    }

    // Process project to set employees and skills
    private void processProjectData(Project project, ProjectDto projectDto) {
        // Set Employees
        if (projectDto.getEmployeeIds() != null) {
            Set<Employee> employees = projectDto.getEmployeeIds().stream()
                    .map(id -> employeeRepository.findById(id)
                            .orElseThrow(() -> new ResourceNotFoundException("Employee not found with ID: " + id)))
                    .collect(Collectors.toSet());
            project.setEmployees(employees);
            logger.debug("Assigned Employees: {}", employees);
        }

        // Set Skills
        if (projectDto.getSkillIds() != null) {
            Set<Skill> skills = projectDto.getSkillIds().stream()
                    .map(id -> skillRepository.findById(id)
                            .orElseThrow(() -> new ResourceNotFoundException("Skill not found with ID: " + id)))
                    .collect(Collectors.toSet());
            project.setSkills(skills);
            logger.debug("Assigned Skills: {}", skills);
        }
    }
}