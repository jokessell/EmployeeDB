// src/test/java/com/example/controller/ProjectControllerTest.java
package com.example.controller;

import com.example.dto.ProjectDto;
import com.example.service.ProjectService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProjectController.class)
public class ProjectControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProjectService projectService;

    @Test
    void testGetAllProjects() throws Exception {
        ProjectDto projectDto = new ProjectDto(1L, "Project X", "Top secret project", null, null, null, null);

        // Mocking pageable
        Pageable pageable = PageRequest.of(0, 10);
        Page<ProjectDto> projectPage = new PageImpl<>(List.of(projectDto), pageable, 1);
        Mockito.when(projectService.getAllProjects(Mockito.any(Pageable.class))).thenReturn(projectPage);

        mockMvc.perform(get("/api/projects").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].projectId").value(1L))
                .andExpect(jsonPath("$.content[0].projectName").value("Project X"));
    }


    @Test
    void testGetProjectById() throws Exception {
        ProjectDto projectDto = new ProjectDto(1L, "Project X", "Top secret project", null, null, null, null);
        Mockito.when(projectService.getProjectById(1L)).thenReturn(projectDto);

        mockMvc.perform(get("/api/projects/1").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.projectId").value(1L))
                .andExpect(jsonPath("$.projectName").value("Project X"));
    }

    @Test
    void testCreateProject() throws Exception {
        ProjectDto projectDto = new ProjectDto(null, "Project X", "Top secret project", null, null, null, null);
        ProjectDto createdProject = new ProjectDto(1L, "Project X", "Top secret project", null, null, null, null);
        Mockito.when(projectService.createProject(Mockito.any(ProjectDto.class))).thenReturn(createdProject);

        mockMvc.perform(post("/api/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"projectName\": \"Project X\", \"description\": \"Top secret project\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.projectId").value(1L))
                .andExpect(jsonPath("$.projectName").value("Project X"));
    }

    @Test
    void testUpdateProject() throws Exception {
        ProjectDto projectDto = new ProjectDto(1L, "Project X Updated", "Top secret project updated", null, null, null, null);
        Mockito.when(projectService.updateProject(Mockito.eq(1L), Mockito.any(ProjectDto.class))).thenReturn(projectDto);

        mockMvc.perform(put("/api/projects/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"projectName\": \"Project X Updated\", \"description\": \"Top secret project updated\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.projectId").value(1L))
                .andExpect(jsonPath("$.projectName").value("Project X Updated"));
    }

    @Test
    void testDeleteProject() throws Exception {
        Mockito.doNothing().when(projectService).deleteProject(1L);

        mockMvc.perform(delete("/api/projects/1").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void partialUpdateProject_shouldReturnUpdatedProject_whenValidIdAndDataProvided() throws Exception {
        Long projectId = 1L;
        ProjectDto partialUpdateDto = new ProjectDto();
        partialUpdateDto.setDescription("Updated description");

        ProjectDto updatedProjectDto = new ProjectDto(projectId, "Project X", "Updated description", null, null, null, null);
        Mockito.when(projectService.partialUpdateProject(Mockito.eq(projectId), Mockito.any(ProjectDto.class))).thenReturn(updatedProjectDto);

        mockMvc.perform(patch("/api/projects/" + projectId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"description\": \"Updated description\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.projectId").value(1L))
                .andExpect(jsonPath("$.description").value("Updated description"));
    }

    @Test
    void getProjectsByEmployeeId_shouldReturnProjectList_whenEmployeeIdIsValid() throws Exception {
        Long employeeId = 1L;
        ProjectDto projectDto = new ProjectDto(1L, "Project X", "Top secret project", null, null, null, null);
        List<ProjectDto> projects = Collections.singletonList(projectDto);
        Mockito.when(projectService.getProjectsByEmployeeId(employeeId)).thenReturn(projects);

        mockMvc.perform(get("/api/projects/employee/" + employeeId).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].projectId").value(1L))
                .andExpect(jsonPath("$[0].projectName").value("Project X"));
    }
}
