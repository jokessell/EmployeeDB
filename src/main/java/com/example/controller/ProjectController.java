// src/main/java/com/example/controller/ProjectController.java
package com.example.controller;

import com.example.dto.ProjectDto;
import com.example.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @GetMapping
    public ResponseEntity<Page<ProjectDto>> getAllProjects(Pageable pageable) {
        Page<ProjectDto> projects = projectService.getAllProjects(pageable);
        return ResponseEntity.ok(projects);
    }

    // Get a project by ID
    @GetMapping("/{id}")
    public ResponseEntity<ProjectDto> getProjectById(@PathVariable(name = "id") Long id) {
        ProjectDto project = projectService.getProjectById(id);
        return ResponseEntity.ok(project);
    }

    // Create a new project
    @PostMapping
    public ResponseEntity<ProjectDto> createProject(@Valid @RequestBody ProjectDto projectDto) {
        ProjectDto createdProject = projectService.createProject(projectDto);
        return ResponseEntity.ok(createdProject);
    }

    // Update an existing project
    @PutMapping("/{id}")
    public ResponseEntity<ProjectDto> updateProject(@PathVariable Long id, @Valid @RequestBody ProjectDto projectDto) {
        ProjectDto updatedProject = projectService.updateProject(id, projectDto);
        return ResponseEntity.ok(updatedProject);
    }

    // Delete a project
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable Long id) {
        projectService.deleteProject(id);
        return ResponseEntity.noContent().build();
    }

    // Get all projects for a specific employee
    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<ProjectDto>> getProjectsByEmployeeId(@PathVariable Long employeeId) {
        List<ProjectDto> projects = projectService.getProjectsByEmployeeId(employeeId);
        return ResponseEntity.ok(projects);
    }
}
