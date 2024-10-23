// src/test/java/com/example/dto/ProjectDtoTest.java
package com.example.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Set;

public class ProjectDtoTest {

    @Test
    void testProjectDtoGettersAndSetters() {
        ProjectDto projectDto = new ProjectDto();
        projectDto.setProjectId(1L);
        projectDto.setProjectName("Project A");
        projectDto.setDescription("Description of Project A");
        projectDto.setEmployeeIds(Set.of(1L, 2L));
        projectDto.setSkillIds(Set.of(3L, 4L));

        assertEquals(1L, projectDto.getProjectId());
        assertEquals("Project A", projectDto.getProjectName());
        assertEquals("Description of Project A", projectDto.getDescription());
        assertEquals(Set.of(1L, 2L), projectDto.getEmployeeIds());
        assertEquals(Set.of(3L, 4L), projectDto.getSkillIds());
    }

    @Test
    void testProjectDtoAllArgsConstructor() {
        ProjectDto projectDto = new ProjectDto(1L, "Project A", "Description of Project A", Set.of(1L, 2L), Set.of(3L, 4L), null, null);
        assertEquals(1L, projectDto.getProjectId());
        assertEquals("Project A", projectDto.getProjectName());
        assertEquals("Description of Project A", projectDto.getDescription());
        assertEquals(Set.of(1L, 2L), projectDto.getEmployeeIds());
        assertEquals(Set.of(3L, 4L), projectDto.getSkillIds());
    }

    @Test
    void testProjectDtoNoArgsConstructor() {
        ProjectDto projectDto = new ProjectDto();
        assertNull(projectDto.getProjectId());
        assertNull(projectDto.getProjectName());
        assertNull(projectDto.getDescription());
        assertNull(projectDto.getEmployeeIds());
        assertNull(projectDto.getSkillIds());
    }
}