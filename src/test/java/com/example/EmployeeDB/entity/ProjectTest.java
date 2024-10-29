package com.example.EmployeeDB.entity;

import com.example.EmployeeDB.entity.Project;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ProjectTest {

    @Test
    void testProjectGettersAndSetters() {
        Project project = new Project();
        project.setProjectId(1L);
        project.setProjectName("Project X");
        project.setDescription("Top secret project");

        assertEquals(1L, project.getProjectId());
        assertEquals("Project X", project.getProjectName());
        assertEquals("Top secret project", project.getDescription());
    }
}