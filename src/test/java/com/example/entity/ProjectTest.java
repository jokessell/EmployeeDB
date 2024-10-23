// src/test/java/com/example/entity/ProjectTest.java
package com.example.entity;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Set;

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