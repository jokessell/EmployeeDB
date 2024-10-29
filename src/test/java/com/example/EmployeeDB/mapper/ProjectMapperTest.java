package com.example.EmployeeDB.mapper;

import com.example.EmployeeDB.dto.ProjectDto;
import com.example.EmployeeDB.entity.Project;
import com.example.EmployeeDB.mapper.ProjectMapper;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import static org.junit.jupiter.api.Assertions.*;

public class ProjectMapperTest {

    private final ProjectMapper projectMapper = Mappers.getMapper(ProjectMapper.class);

    @Test
    void testToEntity() {
        ProjectDto projectDto = new ProjectDto();
        projectDto.setProjectName("Project X");
        projectDto.setDescription("Top secret project");

        Project project = projectMapper.toEntity(projectDto);

        assertNotNull(project);
        assertEquals("Project X", project.getProjectName());
        assertEquals("Top secret project", project.getDescription());
    }

    @Test
    void testToDto() {
        Project project = new Project();
        project.setProjectId(1L);
        project.setProjectName("Project X");
        project.setDescription("Top secret project");

        ProjectDto projectDto = projectMapper.toDto(project);

        assertNotNull(projectDto);
        assertEquals(1L, projectDto.getProjectId());
        assertEquals("Project X", projectDto.getProjectName());
        assertEquals("Top secret project", projectDto.getDescription());
    }
}
