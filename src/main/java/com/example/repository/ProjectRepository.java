// src/main/java/com/example/repository/ProjectRepository.java
package com.example.repository;

import com.example.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    // Find all projects by employee ID (many-to-many)
    List<Project> findByEmployeesEmployeeId(Long employeeId);
}
