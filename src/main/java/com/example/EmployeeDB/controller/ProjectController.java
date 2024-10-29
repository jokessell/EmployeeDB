package com.example.EmployeeDB.controller;

import com.example.EmployeeDB.dto.ProjectDto;
import com.example.EmployeeDB.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;
    private static final Logger logger = LoggerFactory.getLogger(ProjectController.class);

    @GetMapping
    public ResponseEntity<Page<ProjectDto>> getAllProjects(Pageable pageable) {
        logger.debug("Fetching all projects with pageable: {}", pageable);
        Page<ProjectDto> projects = projectService.getAllProjects(pageable);
        logger.debug("Fetched {} projects", projects.getTotalElements());
        return ResponseEntity.ok(projects);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectDto> getProjectById(@PathVariable(name = "id") Long id) {
        logger.debug("Fetching project with ID: {}", id);
        ProjectDto project = projectService.getProjectById(id);
        logger.debug("Fetched project: {}", project);
        return ResponseEntity.ok(project);
    }

    @PostMapping
    public ResponseEntity<ProjectDto> createProject(@Valid @RequestBody ProjectDto projectDto) {
        logger.debug("Creating project: {}", projectDto);
        ProjectDto createdProject = projectService.createProject(projectDto);
        logger.debug("Created project: {}", createdProject);
        return ResponseEntity.ok(createdProject);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProjectDto> updateProject(@PathVariable Long id, @Valid @RequestBody ProjectDto projectDto) {
        logger.debug("Updating project with ID: {} using data: {}", id, projectDto);
        ProjectDto updatedProject = projectService.updateProject(id, projectDto);
        logger.debug("Updated project: {}", updatedProject);
        return ResponseEntity.ok(updatedProject);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ProjectDto> partialUpdateProject(@PathVariable Long id, @RequestBody ProjectDto projectDto) {
        logger.debug("Partially updating project with ID: {} using data: {}", id, projectDto);
        ProjectDto updatedProject = projectService.partialUpdateProject(id, projectDto);
        logger.debug("Partially updated project: {}", updatedProject);
        return ResponseEntity.ok(updatedProject);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable Long id) {
        logger.debug("Deleting project with ID: {}", id);
        projectService.deleteProject(id);
        logger.debug("Deleted project with ID: {}", id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<ProjectDto>> getProjectsByEmployeeId(@PathVariable Long employeeId) {
        logger.debug("Fetching projects for employee ID: {}", employeeId);
        List<ProjectDto> projects = projectService.getProjectsByEmployeeId(employeeId);
        logger.debug("Fetched {} projects for employee ID: {}", projects.size(), employeeId);
        return ResponseEntity.ok(projects);
    }
}
